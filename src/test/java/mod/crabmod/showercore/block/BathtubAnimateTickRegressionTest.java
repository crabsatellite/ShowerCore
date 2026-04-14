package mod.crabmod.showercore.block;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Source-level regression test for a bug in {@code BathtubBlock.animateTick}.
 *
 * <p><b>Bug under regression:</b> {@code animateTick} used to spawn
 * {@code ParticleRegister.HOT_WATER_BUBBLE} particles for CUSTOM fluids when
 * {@code showBubbles=true}. However, hotBath's {@code HotBathBubbleParticle}
 * self-destructs on its first tick unless the block at its position is vanilla
 * water or an {@code AbstractHotbathBlock}. A {@code BathtubBlock} is neither,
 * so every spawned bubble died as a one-frame flicker. The fix removed the
 * bubble spawn call entirely.
 *
 * <p><b>Why file-content-based:</b> Minecraft classes are not on the unit-test
 * classpath, so {@code animateTick} can't be exercised behaviorally here. This
 * test enforces the fix as a textual invariant: the {@code animateTick} method
 * body must not reference {@code HOT_WATER_BUBBLE}, and the explanatory
 * comment (mentioning {@code HotBathBubbleParticle}) must remain so future
 * maintainers understand why the spawn was removed before re-adding it.
 */
class BathtubAnimateTickRegressionTest {

    private static final Path SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");

    @Test
    @DisplayName("animateTick method body must not reference HOT_WATER_BUBBLE")
    void animateTickBodyDoesNotSpawnHotWaterBubble() throws IOException {
        String body = extractAnimateTickBody(readSource());
        assertFalse(
                body.contains("HOT_WATER_BUBBLE"),
                "animateTick must not spawn HOT_WATER_BUBBLE: HotBathBubbleParticle self-destructs "
                        + "unless inside vanilla water or an AbstractHotbathBlock, so bubbles flicker "
                        + "and die instantly in a BathtubBlock. If you need bubbles, spawn them from "
                        + "hotBath's own fluid blocks, not here.");
    }

    @Test
    @DisplayName("animateTick retains the HotBathBubbleParticle explanatory comment")
    void animateTickKeepsExplanatoryComment() throws IOException {
        String body = extractAnimateTickBody(readSource());
        assertTrue(
                body.contains("HotBathBubbleParticle"),
                "The explanatory comment referencing HotBathBubbleParticle must remain inside "
                        + "animateTick so maintainers see why bubbles are intentionally not spawned.");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String readSource() throws IOException {
        if (!Files.isRegularFile(SOURCE)) {
            fail("BathtubBlock.java not found at " + SOURCE.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        return Files.readString(SOURCE);
    }

    /**
     * Returns the body (between the opening and matching closing brace) of the
     * {@code animateTick} method. Fails the test if the method can't be located.
     */
    private static String extractAnimateTickBody(String source) {
        int sigIdx = source.indexOf("public void animateTick(");
        if (sigIdx < 0) {
            fail("Could not locate 'public void animateTick(' in BathtubBlock.java");
        }
        int openBrace = source.indexOf('{', sigIdx);
        assertNotEquals(-1, openBrace, "animateTick signature without opening brace");

        int depth = 1;
        int i = openBrace + 1;
        while (i < source.length() && depth > 0) {
            char c = source.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            i++;
        }
        if (depth != 0) {
            fail("Unbalanced braces while extracting animateTick body");
        }
        return source.substring(openBrace + 1, i - 1);
    }
}
