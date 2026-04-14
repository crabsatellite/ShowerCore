package mod.crabmod.showercore.block.entity;

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
 * Source-level regression test for Bug #5 in {@code BathtubBlockEntity}.
 *
 * <p><b>Bug under regression:</b> {@code BathtubBlock#hasDynamicLightEmission}
 * returns {@code true} and {@code getLightEmission} reads the stored
 * {@code customFluidId} from {@code BathtubBlockEntity}. The vanilla light
 * engine has no automatic way to know that a BE-backed emission value has
 * changed, so the BE must explicitly call
 * {@code level.getLightEngine().checkBlock(getBlockPos())} in four lifecycle
 * hooks:
 *
 * <ol>
 *   <li>{@code setCustomFluidId(...)} — mutation of the fluid id (both sides)</li>
 *   <li>{@code onLoad()} — saved chunk loads its BE data after the chunk's
 *       initial light pass</li>
 *   <li>{@code handleUpdateTag(...)} — bulk chunk-data path on the client</li>
 *   <li>{@code onDataPacket(...)} — single-BE update path on the client
 *       (e.g. live pour into an already-loaded chunk)</li>
 * </ol>
 *
 * <p>Missing any of these leaves stale lightmaps on re-entry or after a live
 * fluid change, so the bathtub appears dark until a neighboring block update
 * forces a relight.
 *
 * <p><b>Why file-content-based:</b> Minecraft classes (e.g. {@code Level},
 * {@code LevelLightEngine}, {@code BlockEntity}) are not on the unit-test
 * classpath, so we can't instantiate a {@code BathtubBlockEntity} or observe
 * light-engine calls behaviorally. This test enforces the fix as a textual
 * invariant: each of the four method bodies must contain the recheck call with
 * the appropriate null/side guards.
 */
class BathtubLightRecheckRegressionTest {

    private static final Path SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "entity",
            "BathtubBlockEntity.java");

    /** The exact recheck call all four hooks must contain. */
    private static final String RECHECK_CALL = "level.getLightEngine().checkBlock(getBlockPos())";

    @Test
    @DisplayName("setCustomFluidId rechecks light and guards on level != null")
    void setCustomFluidIdRechecksLight() throws IOException {
        String body = extractMethodBody(readSource(), "public void setCustomFluidId(");
        assertTrue(
                body.contains(RECHECK_CALL),
                "setCustomFluidId must call " + RECHECK_CALL + " so the light engine "
                        + "re-evaluates the bathtub's emission whenever the custom fluid id "
                        + "is mutated (server or client).");
        assertTrue(
                containsLevelNotNullGuard(body),
                "setCustomFluidId must guard the light recheck with 'level != null' to avoid "
                        + "NPEs before the BE is attached to a level.");
    }

    @Test
    @DisplayName("onLoad rechecks light, guarded by level != null && customFluidId != null")
    void onLoadRechecksLight() throws IOException {
        String body = extractMethodBody(readSource(), "public void onLoad()");
        assertTrue(
                body.contains(RECHECK_CALL),
                "onLoad must call " + RECHECK_CALL + ": saved chunks load BE data AFTER the "
                        + "chunk's initial light pass, so without this call a luminous custom "
                        + "fluid won't light its bathtub on world re-entry.");
        assertTrue(
                containsLevelNotNullGuard(body),
                "onLoad must guard the recheck with 'level != null'.");
        assertTrue(
                containsCustomFluidNotNullGuard(body),
                "onLoad must guard the recheck with 'customFluidId != null' so empty bathtubs "
                        + "don't trigger unnecessary light work on every chunk load.");
    }

    @Test
    @DisplayName("handleUpdateTag rechecks light, guarded by level != null && isClientSide && customFluidId != null")
    void handleUpdateTagRechecksLight() throws IOException {
        String body = extractMethodBody(readSource(), "public void handleUpdateTag(");
        assertTrue(
                body.contains(RECHECK_CALL),
                "handleUpdateTag must call " + RECHECK_CALL + ": this is the bulk chunk-data "
                        + "path on the client and the recheck must fire after the client's "
                        + "lightmap has been built.");
        assertTrue(
                containsLevelNotNullGuard(body),
                "handleUpdateTag must guard the recheck with 'level != null'.");
        assertTrue(
                containsClientSideGuard(body),
                "handleUpdateTag must guard the recheck with 'level.isClientSide' (this hook "
                        + "is a client-only concern).");
        assertTrue(
                containsCustomFluidNotNullGuard(body),
                "handleUpdateTag must guard the recheck with 'customFluidId != null'.");
    }

    @Test
    @DisplayName("onDataPacket rechecks light, guarded by level != null && isClientSide && customFluidId != null")
    void onDataPacketRechecksLight() throws IOException {
        String body = extractMethodBody(readSource(), "public void onDataPacket(");
        assertTrue(
                body.contains(RECHECK_CALL),
                "onDataPacket must call " + RECHECK_CALL + ": handleUpdateTag does NOT fire on "
                        + "the single-BE update path (live pour into an already-loaded chunk), "
                        + "so an explicit recheck is required here.");
        assertTrue(
                containsLevelNotNullGuard(body),
                "onDataPacket must guard the recheck with 'level != null'.");
        assertTrue(
                containsClientSideGuard(body),
                "onDataPacket must guard the recheck with 'level.isClientSide'.");
        assertTrue(
                containsCustomFluidNotNullGuard(body),
                "onDataPacket must guard the recheck with 'customFluidId != null'.");
    }

    @Test
    @DisplayName("All four lifecycle hooks together contain at least 4 light rechecks")
    void allFourHooksContributeLightRechecks() throws IOException {
        String source = readSource();
        int count = countOccurrences(source, "level.getLightEngine().checkBlock");
        assertTrue(
                count >= 4,
                "Expected at least 4 occurrences of 'level.getLightEngine().checkBlock' across "
                        + "setCustomFluidId, onLoad, handleUpdateTag, and onDataPacket — found "
                        + count + ". Removing any hook leaves stale lightmaps for dynamic "
                        + "bathtub emission.");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String readSource() throws IOException {
        if (!Files.isRegularFile(SOURCE)) {
            fail("BathtubBlockEntity.java not found at " + SOURCE.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        return Files.readString(SOURCE);
    }

    /**
     * Returns the body (between the opening and matching closing brace) of the
     * first method whose signature starts with {@code signaturePrefix}. Fails
     * the test if the method can't be located or its braces are unbalanced.
     */
    private static String extractMethodBody(String source, String signaturePrefix) {
        int sigIdx = source.indexOf(signaturePrefix);
        if (sigIdx < 0) {
            fail("Could not locate '" + signaturePrefix + "' in BathtubBlockEntity.java");
        }
        int openBrace = source.indexOf('{', sigIdx);
        assertNotEquals(-1, openBrace,
                "Method signature '" + signaturePrefix + "' has no opening brace");

        int depth = 1;
        int i = openBrace + 1;
        while (i < source.length() && depth > 0) {
            char c = source.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            i++;
        }
        if (depth != 0) {
            fail("Unbalanced braces while extracting body of '" + signaturePrefix + "'");
        }
        return source.substring(openBrace + 1, i - 1);
    }

    /** Matches {@code level != null} anywhere in the body (whitespace-tolerant). */
    private static boolean containsLevelNotNullGuard(String body) {
        return Pattern.compile("level\\s*!=\\s*null").matcher(body).find();
    }

    /** Matches {@code customFluidId != null} anywhere in the body (whitespace-tolerant). */
    private static boolean containsCustomFluidNotNullGuard(String body) {
        return Pattern.compile("customFluidId\\s*!=\\s*null").matcher(body).find();
    }

    /** Matches {@code level.isClientSide} or {@code .isClientSide} anywhere in the body. */
    private static boolean containsClientSideGuard(String body) {
        return Pattern.compile("\\.isClientSide\\b").matcher(body).find();
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        Matcher m = Pattern.compile(Pattern.quote(needle)).matcher(haystack);
        while (m.find()) count++;
        return count;
    }
}
