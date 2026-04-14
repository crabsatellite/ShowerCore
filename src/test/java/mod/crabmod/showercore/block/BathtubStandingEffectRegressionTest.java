package mod.crabmod.showercore.block;

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
 * Source-level regression test for Bug #8 — the 6 vanilla bath effects were only
 * applied when a player was SITTING on a {@code SeatEntity}, not when merely
 * STANDING in the bathtub.
 *
 * <p><b>Bug under regression:</b> {@code SeatEntity#applyBathEffects} was
 * {@code private} and only invoked from the seat's own tick loop. The 6 vanilla
 * liquid types — {@code HOT_WATER → MOVEMENT_SPEED}, {@code HERBAL_BATH →
 * REGENERATION}, {@code HONEY_BATH → ABSORPTION}, {@code MILK_BATH →
 * SATURATION}, {@code PEONY_BATH → LUCK}, {@code ROSE_BATH → DAMAGE_BOOST} —
 * therefore produced no buff when a player (or any {@code LivingEntity}) simply
 * stood inside the bathtub block's AABB.
 *
 * <p><b>Fix:</b>
 * <ol>
 *   <li>{@code SeatEntity#applyBathEffects} was promoted from {@code private} to
 *       {@code public static} so {@code BathtubBlock} can reuse it.</li>
 *   <li>{@code BathtubBlock#entityInside} now invokes
 *       {@code SeatEntity.applyBathEffects(living, liquid)} for every
 *       {@code LivingEntity} that is inside the block, gated by
 *       {@code !level.isClientSide}, {@code instanceof LivingEntity}, and
 *       {@code entity.tickCount % 20 == 0} (once per second, server-side, to
 *       mirror the seated path's cadence and avoid effect-icon flicker). The
 *       call sits BEFORE the "Hot bathtub interactions" block so standing
 *       buffs apply regardless of whether the liquid is a hot variant.</li>
 * </ol>
 *
 * <p><b>Why file-content-based:</b> Minecraft classes (e.g. {@code Level},
 * {@code Entity}, {@code LivingEntity}, {@code MobEffects}) are not on the
 * unit-test classpath, so {@code entityInside} can't be exercised behaviorally.
 * This test enforces the fix as a textual invariant on both source files:
 * {@code applyBathEffects} must be accessible and complete, and
 * {@code entityInside} must call it with the documented guards in the
 * documented position.
 */
class BathtubStandingEffectRegressionTest {

    private static final Path SEAT_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "entity", "SeatEntity.java");

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");

    @Test
    @DisplayName("SeatEntity.applyBathEffects is public static so BathtubBlock can call it")
    void seatEntityApplyBathEffectsIsPublicStatic() throws IOException {
        String source = readSource(SEAT_SOURCE);
        Pattern sigPattern = Pattern.compile(
                "public\\s+static\\s+void\\s+applyBathEffects\\s*\\(");
        assertTrue(
                sigPattern.matcher(source).find(),
                "SeatEntity.applyBathEffects must be declared 'public static void applyBathEffects(' "
                        + "so BathtubBlock.entityInside can invoke it for standing LivingEntities. "
                        + "If a future change removes 'public' or 'static', BathtubBlock will fail to "
                        + "compile against 'SeatEntity.applyBathEffects(living, liquid)' and Bug #8 "
                        + "(no standing buff in a vanilla bathtub) will regress. Regex "
                        + "'public\\s+static\\s+void\\s+applyBathEffects\\s*\\(' did not match.");
    }

    @Test
    @DisplayName("SeatEntity.applyBathEffects body references all six vanilla bath liquid types")
    void seatEntityApplyBathEffectsCoversAllSixBathTypes() throws IOException {
        String source = readSource(SEAT_SOURCE);
        String body = extractMethodBody(source,
                Pattern.compile("public\\s+static\\s+void\\s+applyBathEffects\\s*\\("),
                "SeatEntity.applyBathEffects");

        String[] liquidTypes = {
                "HOT_WATER", "HERBAL_BATH", "HONEY_BATH",
                "MILK_BATH", "PEONY_BATH", "ROSE_BATH"
        };

        for (String liquid : liquidTypes) {
            assertTrue(
                    body.contains(liquid),
                    "SeatEntity.applyBathEffects body must reference the liquid-type constant '"
                            + liquid + "' so its corresponding vanilla MobEffect is applied to "
                            + "both seated AND standing entities. Missing '" + liquid + "' means "
                            + "a player standing in that bath type receives no buff, which is "
                            + "exactly the Bug #8 regression this test locks against.");
        }
    }

    @Test
    @DisplayName("BathtubBlock.entityInside invokes SeatEntity.applyBathEffects(...)")
    void bathtubBlockEntityInsideCallsApplyBathEffects() throws IOException {
        String body = extractEntityInsideBody(readSource(BATHTUB_SOURCE));
        Pattern callPattern = Pattern.compile(
                "SeatEntity\\s*\\.\\s*applyBathEffects\\s*\\(");
        assertTrue(
                callPattern.matcher(body).find(),
                "BathtubBlock.entityInside must contain a call matching "
                        + "'SeatEntity\\s*\\.\\s*applyBathEffects\\s*\\(' so entities standing "
                        + "inside the bathtub block receive the same vanilla bath effects the "
                        + "SeatEntity grants to seated riders. Removing this call regresses "
                        + "Bug #8: no buff when simply standing in a HOT_WATER / HERBAL_BATH / "
                        + "HONEY_BATH / MILK_BATH / PEONY_BATH / ROSE_BATH bathtub.");
    }

    @Test
    @DisplayName("Standing-effect call is server-side, LivingEntity-only, and gated to once-per-second")
    void bathtubStandingEffectIsServerSideAndGatedTo20Ticks() throws IOException {
        String body = extractEntityInsideBody(readSource(BATHTUB_SOURCE));

        Pattern callPattern = Pattern.compile(
                "SeatEntity\\s*\\.\\s*applyBathEffects\\s*\\(");
        Matcher callMatcher = callPattern.matcher(body);
        assertTrue(
                callMatcher.find(),
                "Could not locate the 'SeatEntity.applyBathEffects(' call in entityInside (see "
                        + "bathtubBlockEntityInsideCallsApplyBathEffects for details).");

        int callIdx = callMatcher.start();
        int windowStart = Math.max(0, callIdx - 300);
        String guardWindow = body.substring(windowStart, callIdx);

        Pattern serverSideGuard = Pattern.compile("!\\s*level\\s*\\.\\s*isClientSide");
        Pattern livingEntityGuard = Pattern.compile("instanceof\\s+LivingEntity");
        Pattern tickGateGuard = Pattern.compile("tickCount\\s*%\\s*20\\s*==\\s*0");

        assertTrue(
                serverSideGuard.matcher(guardWindow).find(),
                "The SeatEntity.applyBathEffects call must be guarded by '!level.isClientSide' "
                        + "(regex '!\\s*level\\s*\\.\\s*isClientSide') within ~300 chars before "
                        + "the call. MobEffect application is a server-authoritative concern; "
                        + "applying effects on the client duplicates them or desyncs the effect "
                        + "timer. Guard not found in window.");

        assertTrue(
                livingEntityGuard.matcher(guardWindow).find(),
                "The SeatEntity.applyBathEffects call must be guarded by 'instanceof LivingEntity' "
                        + "(regex 'instanceof\\s+LivingEntity') so only LivingEntity subtypes are "
                        + "passed (items, arrows, etc. cannot receive MobEffects and would NPE or "
                        + "ClassCastException). Guard not found in window.");

        assertTrue(
                tickGateGuard.matcher(guardWindow).find(),
                "The SeatEntity.applyBathEffects call must be gated by 'entity.tickCount % 20 == 0' "
                        + "(regex 'tickCount\\s*%\\s*20\\s*==\\s*0') so the effect is reapplied at "
                        + "most once per second. Without this gate the 260-tick MobEffectInstance "
                        + "is refreshed every tick, causing effect-icon flicker and wasted network "
                        + "traffic. Guard not found in window.");
    }

    @Test
    @DisplayName("Standing-effect call sits BEFORE the 'Hot bathtub interactions' block")
    void bathtubStandingEffectIsBeforeHotInteractions() throws IOException {
        String body = extractEntityInsideBody(readSource(BATHTUB_SOURCE));

        int applyIdx = body.indexOf("SeatEntity.applyBathEffects");
        assertNotEquals(-1, applyIdx,
                "Could not locate literal 'SeatEntity.applyBathEffects' in entityInside body "
                        + "(see bathtubBlockEntityInsideCallsApplyBathEffects for details).");

        int hotIdx = body.indexOf("Hot bathtub interactions");
        assertNotEquals(-1, hotIdx,
                "Could not locate the 'Hot bathtub interactions' section marker comment in "
                        + "entityInside. This comment anchors the hot-variant-only damage block; "
                        + "if it was removed or renamed, update this test's marker string in "
                        + "tandem with the production code.");

        assertTrue(
                applyIdx < hotIdx,
                "The 'SeatEntity.applyBathEffects' call must appear BEFORE the "
                        + "'Hot bathtub interactions' block in entityInside. The standing-effect "
                        + "path must apply to ALL six vanilla liquid types (including cool ones "
                        + "like MILK_BATH, PEONY_BATH, ROSE_BATH, HERBAL_BATH, HONEY_BATH), "
                        + "whereas the 'Hot bathtub interactions' block is gated to hot variants "
                        + "only. Placing the applyBathEffects call INSIDE or AFTER that block "
                        + "would drop cool-bath buffs entirely and regress Bug #8. Found "
                        + "applyBathEffects at body-index " + applyIdx + " and "
                        + "'Hot bathtub interactions' at body-index " + hotIdx + ".");
    }

    // ---- Helpers ----------------------------------------------------------

    private static String readSource(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            fail(path.getFileName() + " not found at " + path.toAbsolutePath()
                    + " (cwd=" + Paths.get("").toAbsolutePath() + ")");
        }
        return Files.readString(path);
    }

    /**
     * Returns the body (between the opening and matching closing brace) of
     * {@code BathtubBlock#entityInside}. Fails the test if the method can't be
     * located or its braces are unbalanced. The opening brace is located by
     * scanning past the parameter list so generics / parameter parens do not
     * interfere.
     */
    private static String extractEntityInsideBody(String source) {
        Pattern sigPattern = Pattern.compile(
                "\\bvoid\\s+entityInside\\s*\\(");
        return extractMethodBody(source, sigPattern, "BathtubBlock.entityInside");
    }

    /**
     * Generic brace-balanced method-body extractor: finds the first signature
     * matching {@code sigPattern}, skips past its parameter list, and returns
     * the text between the first {@code {} and its matching {@code }}.
     */
    private static String extractMethodBody(String source, Pattern sigPattern, String humanName) {
        Matcher sigMatcher = sigPattern.matcher(source);
        if (!sigMatcher.find()) {
            fail("Could not locate signature for '" + humanName + "' (pattern: "
                    + sigPattern.pattern() + ")");
        }
        int sigIdx = sigMatcher.start();

        int parenOpen = source.indexOf('(', sigIdx);
        assertNotEquals(-1, parenOpen,
                "Signature for '" + humanName + "' has no opening paren");
        int pDepth = 1;
        int j = parenOpen + 1;
        while (j < source.length() && pDepth > 0) {
            char c = source.charAt(j);
            if (c == '(') pDepth++;
            else if (c == ')') pDepth--;
            j++;
        }
        if (pDepth != 0) {
            fail("Unbalanced parentheses in '" + humanName + "' parameter list");
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
        if (depth != 0) {
            fail("Unbalanced braces while extracting body of '" + humanName + "'");
        }
        return source.substring(openBrace + 1, i - 1);
    }
}
