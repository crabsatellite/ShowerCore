package mod.crabmod.showercore.utils;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static final Pattern CHECK_BUILT_IN_SIG = Pattern.compile(
            "private\\s+static\\s+boolean\\s+checkBuiltInHotBathtubAt\\s*\\(");

    private static String coreUtilsSource;
    private static String mixinSource;
    private static String checkBuiltInBody;

    @BeforeAll
    static void loadSources() throws IOException {
        coreUtilsSource = TestSourceUtils.readSource(CORE_UTILS);
        mixinSource = TestSourceUtils.readSource(DIRTINESS_MIXIN);
        checkBuiltInBody = TestSourceUtils.extractMethodBody(
                coreUtilsSource, CHECK_BUILT_IN_SIG, "CoreUtils.checkBuiltInHotBathtubAt");
    }

    @Test
    @DisplayName("CoreUtils.isPlayerInShowerCoreHotWaterNonCustom exists and is public static")
    void nonCustomHelperExists() {
        Pattern sig = Pattern.compile(
                "public\\s+static\\s+boolean\\s+isPlayerInShowerCoreHotWaterNonCustom\\s*\\(\\s*Entity\\s+\\w+\\s*\\)");
        assertTrue(sig.matcher(coreUtilsSource).find(),
                "CoreUtils must declare 'public static boolean isPlayerInShowerCoreHotWaterNonCustom(Entity)'. "
                        + "Removing or renaming this helper regresses Bug R because the DirtinessHandler mixin "
                        + "will fall back to the all-inclusive helper and drain custom-fluid bathtubs too fast.");
    }

    @Test
    @DisplayName("Non-custom helper body excludes CUSTOM via checkBuiltInHotBathtubAt")
    void helperBodyExcludesCustom() {
        Pattern empty = Pattern.compile("\\w+\\s*!=\\s*BathtubBlock\\s*\\.\\s*LiquidType\\s*\\.\\s*EMPTY");
        Pattern water = Pattern.compile("\\w+\\s*!=\\s*BathtubBlock\\s*\\.\\s*LiquidType\\s*\\.\\s*WATER");
        Pattern custom = Pattern.compile("\\w+\\s*!=\\s*BathtubBlock\\s*\\.\\s*LiquidType\\s*\\.\\s*CUSTOM");

        assertTrue(empty.matcher(checkBuiltInBody).find(),
                "checkBuiltInHotBathtubAt must exclude LiquidType.EMPTY — an empty tub is not hot water.");
        assertTrue(water.matcher(checkBuiltInBody).find(),
                "checkBuiltInHotBathtubAt must exclude LiquidType.WATER — cold water is not hot water.");
        assertTrue(custom.matcher(checkBuiltInBody).find(),
                "checkBuiltInHotBathtubAt must exclude LiquidType.CUSTOM. Removing this exclusion means "
                        + "DirtinessHandler.isInHotBathFluid returns true for CUSTOM tubs, the hotBath "
                        + "per-tick fast-decrement fires, and dirtiness drops too quickly — the exact Bug R "
                        + "regression this test locks against.");
    }

    @Test
    @DisplayName("DirtinessHandlerMixin calls isPlayerInShowerCoreHotWaterNonCustom (not the CUSTOM-inclusive one)")
    void mixinUsesNonCustomHelper() {
        Pattern nonCustomCall = Pattern.compile(
                "CoreUtils\\s*\\.\\s*isPlayerInShowerCoreHotWaterNonCustom\\s*\\(");
        assertTrue(nonCustomCall.matcher(mixinSource).find(),
                "DirtinessHandlerMixin must call 'CoreUtils.isPlayerInShowerCoreHotWaterNonCustom(player)'. "
                        + "If this was changed back to 'isPlayerInShowerCoreHotWater', custom-fluid tubs will "
                        + "re-enter the hotBath fast path and Bug R (dirtiness drops too fast) will regress.");
    }

    @Test
    @DisplayName("DirtinessHandlerMixin HEAD-injects into isInHotBathFluid with cancellable return")
    void mixinTargetIsCorrect() {
        Pattern inj = Pattern.compile(
                "@Inject\\s*\\(\\s*method\\s*=\\s*\"isInHotBathFluid\"\\s*,"
                        + "\\s*at\\s*=\\s*@At\\s*\\(\\s*\"HEAD\"\\s*\\)\\s*,"
                        + "\\s*cancellable\\s*=\\s*true"
                        + "[^)]*\\)");
        assertTrue(inj.matcher(mixinSource).find(),
                "DirtinessHandlerMixin must '@Inject(method = \"isInHotBathFluid\", at = @At(\"HEAD\"), "
                        + "cancellable = true, ...)'. Changing method name, target, or removing cancellable "
                        + "means the mixin can't short-circuit the check and Bug R fix is lost.");

        Pattern setReturn = Pattern.compile("\\w+\\s*\\.\\s*setReturnValue\\s*\\(\\s*true\\s*\\)");
        assertTrue(setReturn.matcher(mixinSource).find(),
                "Mixin must call '<cir>.setReturnValue(true)' when the helper matches, so isInHotBathFluid "
                        + "short-circuits without running hotBath's own logic.");
    }
}
