package mod.crabmod.showercore.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Source-level regression test for Bug R — dirtiness dropped too fast in a
 * custom-fluid bathtub because the hotBath {@code DirtinessHandler.isInHotBathFluid}
 * mixin returned true for ALL ShowerCore hot water (including CUSTOM), letting
 * hotBath's fast per-tick dirtiness decrement fire. CUSTOM fluids must instead
 * rely on ShowerCore's own slower 0.08/40-tick path so cleaning speed stays
 * pack-tunable per custom fluid definition.
 *
 * <p>Fix:
 * <ol>
 *   <li>{@code CoreUtils#isPlayerInShowerCoreHotWaterNonCustom} — a new helper
 *       that returns true only for the 6 built-in hot liquids and shower heads,
 *       explicitly excluding EMPTY, WATER, and CUSTOM.</li>
 *   <li>{@code DirtinessHandlerMixin} — rewrites its HEAD inject on
 *       {@code isInHotBathFluid} to delegate to the new non-custom helper
 *       instead of the all-inclusive one.</li>
 * </ol>
 *
 * <p>CUSTOM is excluded by a three-way check in {@code checkBuiltInHotBathtubAt}:
 * the LiquidType must not be EMPTY, not be WATER, and not be CUSTOM.
 */
class DirtinessNonCustomHelperRegressionTest {

    private static final Path CORE_UTILS = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "utils", "CoreUtils.java");

    private static final Path DIRTINESS_MIXIN = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "mixin", "DirtinessHandlerMixin.java");

    @Test
    @DisplayName("CoreUtils.isPlayerInShowerCoreHotWaterNonCustom exists and is public static")
    void nonCustomHelperExists() throws IOException {
        String source = readSource(CORE_UTILS);
        Pattern sig = Pattern.compile(
                "public\\s+static\\s+boolean\\s+isPlayerInShowerCoreHotWaterNonCustom\\s*\\(\\s*Entity\\s+\\w+\\s*\\)");
        assertTrue(sig.matcher(source).find(),
                "CoreUtils must declare 'public static boolean isPlayerInShowerCoreHotWaterNonCustom(Entity)'. "
                        + "Removing or renaming this helper regresses Bug R because the DirtinessHandler mixin "
                        + "will fall back to the all-inclusive helper and drain custom-fluid bathtubs too fast.");
    }

    @Test
    @DisplayName("Non-custom helper body excludes CUSTOM via checkBuiltInHotBathtubAt")
    void helperBodyExcludesCustom() throws IOException {
        String source = readSource(CORE_UTILS);

        Pattern checkSig = Pattern.compile(
                "private\\s+static\\s+boolean\\s+checkBuiltInHotBathtubAt\\s*\\(");
        String body = extractMethodBody(source, checkSig, "CoreUtils.checkBuiltInHotBathtubAt");

        Pattern empty = Pattern.compile("type\\s*!=\\s*BathtubBlock\\s*\\.\\s*LiquidType\\s*\\.\\s*EMPTY");
        Pattern water = Pattern.compile("type\\s*!=\\s*BathtubBlock\\s*\\.\\s*LiquidType\\s*\\.\\s*WATER");
        Pattern custom = Pattern.compile("type\\s*!=\\s*BathtubBlock\\s*\\.\\s*LiquidType\\s*\\.\\s*CUSTOM");

        assertTrue(empty.matcher(body).find(),
                "checkBuiltInHotBathtubAt must exclude LiquidType.EMPTY — an empty tub is not hot water.");
        assertTrue(water.matcher(body).find(),
                "checkBuiltInHotBathtubAt must exclude LiquidType.WATER — cold water is not hot water.");
        assertTrue(custom.matcher(body).find(),
                "checkBuiltInHotBathtubAt must exclude LiquidType.CUSTOM. Removing this exclusion means "
                        + "DirtinessHandler.isInHotBathFluid returns true for CUSTOM tubs, the hotBath "
                        + "per-tick fast-decrement fires, and dirtiness drops too quickly — the exact Bug R "
                        + "regression this test locks against.");
    }

    @Test
    @DisplayName("DirtinessHandlerMixin calls isPlayerInShowerCoreHotWaterNonCustom (not the CUSTOM-inclusive one)")
    void mixinUsesNonCustomHelper() throws IOException {
        String source = readSource(DIRTINESS_MIXIN);
        Pattern nonCustomCall = Pattern.compile(
                "CoreUtils\\s*\\.\\s*isPlayerInShowerCoreHotWaterNonCustom\\s*\\(");
        assertTrue(nonCustomCall.matcher(source).find(),
                "DirtinessHandlerMixin must call 'CoreUtils.isPlayerInShowerCoreHotWaterNonCustom(player)'. "
                        + "If this was changed back to 'isPlayerInShowerCoreHotWater', custom-fluid tubs will "
                        + "re-enter the hotBath fast path and Bug R (dirtiness drops too fast) will regress.");
    }

    @Test
    @DisplayName("DirtinessHandlerMixin HEAD-injects into isInHotBathFluid with cancellable return")
    void mixinTargetIsCorrect() throws IOException {
        String source = readSource(DIRTINESS_MIXIN);
        Pattern inj = Pattern.compile(
                "@Inject\\s*\\(\\s*method\\s*=\\s*\"isInHotBathFluid\"\\s*,"
                        + "\\s*at\\s*=\\s*@At\\s*\\(\\s*\"HEAD\"\\s*\\)\\s*,"
                        + "\\s*cancellable\\s*=\\s*true"
                        + "[^)]*\\)");
        assertTrue(inj.matcher(source).find(),
                "DirtinessHandlerMixin must '@Inject(method = \"isInHotBathFluid\", at = @At(\"HEAD\"), "
                        + "cancellable = true, ...)'. Changing method name, target, or removing cancellable "
                        + "means the mixin can't short-circuit the check and Bug R fix is lost.");

        Pattern setReturn = Pattern.compile(
                "cir\\s*\\.\\s*setReturnValue\\s*\\(\\s*true\\s*\\)");
        assertTrue(setReturn.matcher(source).find(),
                "Mixin must call 'cir.setReturnValue(true)' when the helper matches, so isInHotBathFluid "
                        + "short-circuits without running hotBath's own logic.");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String readSource(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            fail(path.getFileName() + " not found at " + path.toAbsolutePath());
        }
        return Files.readString(path);
    }

    private static String extractMethodBody(String source, Pattern sig, String humanName) {
        Matcher m = sig.matcher(source);
        if (!m.find()) {
            fail("Could not locate signature for '" + humanName + "' (pattern: " + sig.pattern() + ")");
        }
        int parenOpen = source.indexOf('(', m.start());
        int pDepth = 1;
        int j = parenOpen + 1;
        while (j < source.length() && pDepth > 0) {
            char c = source.charAt(j);
            if (c == '(') pDepth++;
            else if (c == ')') pDepth--;
            j++;
        }
        int openBrace = source.indexOf('{', j);
        assertNotEquals(-1, openBrace,
                "Signature for '" + humanName + "' has no opening body brace");

        int depth = 1;
        int i = openBrace + 1;
        while (i < source.length() && depth > 0) {
            char c = source.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            i++;
        }
        return source.substring(openBrace + 1, i - 1);
    }
}
