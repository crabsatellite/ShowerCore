package mod.crabmod.showercore.block;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Source-level regression test for Bug #7 in {@code BathtubBlock#useItemOn}.
 *
 * <p><b>Bug under regression:</b> Previously, when the player right-clicked a
 * bathtub with a bucket-like item and the fluid interaction could not complete
 * (e.g. the bathtub was already full, an empty bucket on an empty bathtub,
 * incompatible fluid type), {@code useItemOn} fell through and returned
 * {@code ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION}. That return
 * value lets vanilla {@code BucketItem.use()} fire next, which places the
 * held fluid in the adjacent (targeted) block. In creative mode the bucket
 * item is NOT consumed, so a player holding a water/lava bucket could repeatedly
 * right-click a full bathtub to spawn unlimited fluid in its neighboring grid.
 *
 * <p><b>Fix:</b> Near the end of the {@code be instanceof BathtubBlockEntity}
 * branch, after all fluid-interaction attempts have failed, a guard checks
 * whether the held item is a bucket-like type — {@code BucketItem},
 * {@code CustomFluidBucketItem}, or an item carrying a custom fluid via
 * {@code CustomFluidAPI.hasCustomFluid(...)} — and returns
 * {@code ItemInteractionResult.FAIL} to CONSUME the click instead of passing
 * it through to the vanilla default bucket use path. Non-bucket items still
 * fall through to {@code PASS_TO_DEFAULT_BLOCK_INTERACTION} at the end.
 *
 * <p><b>Why file-content-based:</b> Minecraft classes (e.g. {@code BucketItem},
 * {@code ItemInteractionResult}, {@code Player}, {@code BlockState}) are not on
 * the unit-test classpath, so we can't exercise {@code useItemOn} behaviorally.
 * This test enforces the fix as a textual invariant on the {@code useItemOn}
 * method body: the bucket-type guard must exist, cover all three bucket forms,
 * sit inside the {@code BathtubBlockEntity} branch, and appear BEFORE the
 * single remaining {@code PASS_TO_DEFAULT_BLOCK_INTERACTION} fall-through.
 */
class BathtubBucketGuardRegressionTest {

    private static final Path SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");

    @Test
    @DisplayName("useItemOn body contains ItemInteractionResult.FAIL BEFORE the final PASS_TO_DEFAULT_BLOCK_INTERACTION")
    void useItemOnContainsBucketGuardBeforeFinalPass() throws IOException {
        String body = extractUseItemOnBody(readSource());

        int failIdx = body.indexOf("ItemInteractionResult.FAIL");
        assertNotEquals(-1, failIdx,
                "useItemOn must contain 'ItemInteractionResult.FAIL' as the guard return for "
                        + "bucket-like items whose fluid interaction failed. Without this guard, "
                        + "the method falls through to PASS_TO_DEFAULT_BLOCK_INTERACTION and "
                        + "vanilla BucketItem.use() places fluid in the adjacent block — in "
                        + "creative mode this enables infinite fluid spawning by repeatedly "
                        + "right-clicking a full bathtub (Bug #7).");

        int lastPassIdx = body.lastIndexOf("return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION");
        assertNotEquals(-1, lastPassIdx,
                "useItemOn must retain its final 'return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION' "
                        + "fall-through for non-bucket items that the bathtub does not handle.");

        assertTrue(
                failIdx < lastPassIdx,
                "The 'ItemInteractionResult.FAIL' guard must appear BEFORE the final "
                        + "'return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION' so "
                        + "bucket-like items are caught and the click is consumed. Found "
                        + "FAIL at body-index " + failIdx + " and final PASS at body-index "
                        + lastPassIdx + ". If the guard was moved AFTER the fall-through, the "
                        + "Bug #7 exploit is reopened.");
    }

    @Test
    @DisplayName("Bucket guard checks all three bucket-like types (BucketItem, CustomFluidBucketItem, hasCustomFluid)")
    void bucketGuardChecksAllThreeBucketTypes() throws IOException {
        String body = extractUseItemOnBody(readSource());

        Pattern bucketItem = Pattern.compile(
                "instanceof\\s+BucketItem\\b");
        Pattern customFluidBucket = Pattern.compile(
                "instanceof\\s+CustomFluidBucketItem\\b");
        Pattern hasCustomFluid = Pattern.compile(
                "CustomFluidAPI\\s*\\.\\s*hasCustomFluid\\s*\\(");

        assertTrue(
                bucketItem.matcher(body).find(),
                "useItemOn bucket-guard must check 'instanceof BucketItem' so vanilla buckets "
                        + "(water, lava, milk, etc.) that reach the fall-through still consume "
                        + "the click. Regex 'instanceof\\s+BucketItem\\b' did not match in the "
                        + "method body.");

        assertTrue(
                customFluidBucket.matcher(body).find(),
                "useItemOn bucket-guard must check 'instanceof CustomFluidBucketItem' so "
                        + "hotBath / custom-fluid buckets are also consumed on failure. Regex "
                        + "'instanceof\\s+CustomFluidBucketItem\\b' did not match in the method "
                        + "body.");

        assertTrue(
                hasCustomFluid.matcher(body).find(),
                "useItemOn bucket-guard must check 'CustomFluidAPI.hasCustomFluid(...)' so "
                        + "items that only carry a custom fluid via data components (not via a "
                        + "BucketItem/CustomFluidBucketItem subclass) are also consumed. Regex "
                        + "'CustomFluidAPI\\s*\\.\\s*hasCustomFluid\\s*\\(' did not match in the "
                        + "method body.");
    }

    @Test
    @DisplayName("useItemOn body has exactly ONE trailing PASS_TO_DEFAULT_BLOCK_INTERACTION return")
    void useItemOnHasExactlyOneFinalPassReturn() throws IOException {
        String body = extractUseItemOnBody(readSource());

        // Count all PASS_TO_DEFAULT_BLOCK_INTERACTION returns anywhere in the body.
        // At the time this test was written there are exactly two:
        //   1) Rubber Duck early-out (held item is RUBBER_DUCK) — this PASSes through
        //      so vanilla block-placement of the duck entity takes over. It fires
        //      BEFORE the bucket guard could ever run and is not part of the
        //      bucket-failure path, so it does NOT re-open Bug #7.
        //   2) The single final fall-through at the end of the method for any other
        //      non-bucket item that the bathtub does not specially handle.
        //
        // The guard test {@link #useItemOnContainsBucketGuardBeforeFinalPass} covers
        // the ordering invariant. This test guards against a future refactor adding
        // a THIRD PASS return inside the fluid-interaction branch (which would
        // re-open Bug #7 by letting vanilla BucketItem.use() fire on some failure
        // path).
        Pattern p = Pattern.compile(
                "return\\s+ItemInteractionResult\\s*\\.\\s*PASS_TO_DEFAULT_BLOCK_INTERACTION\\s*;");
        int count = 0;
        Matcher m = p.matcher(body);
        while (m.find()) count++;

        assertEquals(
                2, count,
                "useItemOn is expected to contain exactly 2 'return ItemInteractionResult."
                        + "PASS_TO_DEFAULT_BLOCK_INTERACTION;' statements: (1) the Rubber Duck "
                        + "early-out at the top of the method, and (2) the final non-bucket "
                        + "fall-through at the bottom. Found " + count + " occurrence(s). An "
                        + "ADDITIONAL PASS inside the fluid-interaction branch would re-open "
                        + "Bug #7 by letting vanilla BucketItem.use() place fluid adjacent to a "
                        + "full bathtub on some failure paths. Removing a PASS would break "
                        + "either rubber-duck placement or the non-bucket fall-through. If the "
                        + "intended count has genuinely changed, update both this test and the "
                        + "ordering test in tandem.");

        // Ordering sanity: ensure the LAST PASS really is the trailing fall-through
        // (last statement before the closing brace), so the guard-before-final-PASS
        // test keeps meaning what it says.
        int lastPassIdx = body.lastIndexOf("return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION");
        String tail = body.substring(lastPassIdx).trim();
        assertTrue(
                tail.startsWith("return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;"),
                "The final PASS_TO_DEFAULT_BLOCK_INTERACTION must be the LAST statement of "
                        + "useItemOn (tail of the method body). Tail snippet found: \""
                        + tail.substring(0, Math.min(tail.length(), 120)) + "\"");
    }

    @Test
    @DisplayName("Bucket guard sits inside or after the 'if (be instanceof BathtubBlockEntity bathtubBe)' block")
    void bucketGuardIsInsideBathtubBlockEntityCheck() throws IOException {
        String body = extractUseItemOnBody(readSource());

        int bathtubBeIdx = body.indexOf("BathtubBlockEntity bathtubBe");
        assertNotEquals(-1, bathtubBeIdx,
                "Could not locate the 'BathtubBlockEntity bathtubBe' pattern binding in "
                        + "useItemOn. The fluid-interaction branch that contains the guard must "
                        + "begin with 'if (be instanceof BathtubBlockEntity bathtubBe)'.");

        int failIdx = body.indexOf("ItemInteractionResult.FAIL");
        assertNotEquals(-1, failIdx,
                "Could not locate 'ItemInteractionResult.FAIL' in useItemOn body (see "
                        + "useItemOnContainsBucketGuardBeforeFinalPass for details).");

        assertTrue(
                bathtubBeIdx < failIdx,
                "The bucket guard ('ItemInteractionResult.FAIL') must appear AFTER the "
                        + "'BathtubBlockEntity bathtubBe' pattern binding so it only runs when the "
                        + "target is actually a bathtub block entity (and thus after all fluid "
                        + "interaction attempts). Found BathtubBlockEntity-binding at body-index "
                        + bathtubBeIdx + " and FAIL at body-index " + failIdx + ". Hoisting the "
                        + "guard above the BathtubBlockEntity branch would FAIL on rubber-duck / "
                        + "empty-hand / sit-on-bathtub interactions, breaking other features.");
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
     * Returns the body (between the opening and matching closing brace) of
     * {@code useItemOn}. Fails the test if the method can't be located or its
     * braces are unbalanced. The opening brace is located by scanning forward
     * from the method signature, so method-parameter parentheses / generics do
     * not interfere.
     */
    private static String extractUseItemOnBody(String source) {
        // Match the useItemOn signature tolerantly: return type may be
        // 'ItemInteractionResult' or fully-qualified, modifiers may vary.
        Pattern sigPattern = Pattern.compile(
                "\\bItemInteractionResult\\s+useItemOn\\s*\\(");
        Matcher sigMatcher = sigPattern.matcher(source);
        if (!sigMatcher.find()) {
            fail("Could not locate 'ItemInteractionResult useItemOn(' signature in BathtubBlock.java");
        }
        int sigIdx = sigMatcher.start();

        // Skip past the parameter list to find the opening brace of the body.
        int parenOpen = source.indexOf('(', sigIdx);
        assertNotEquals(-1, parenOpen, "useItemOn signature without opening paren");
        int pDepth = 1;
        int j = parenOpen + 1;
        while (j < source.length() && pDepth > 0) {
            char c = source.charAt(j);
            if (c == '(') pDepth++;
            else if (c == ')') pDepth--;
            j++;
        }
        if (pDepth != 0) {
            fail("Unbalanced parentheses in useItemOn parameter list");
        }

        int openBrace = source.indexOf('{', j);
        assertNotEquals(-1, openBrace, "useItemOn signature without opening body brace");

        int depth = 1;
        int i = openBrace + 1;
        while (i < source.length() && depth > 0) {
            char c = source.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            i++;
        }
        if (depth != 0) {
            fail("Unbalanced braces while extracting useItemOn body");
        }
        return source.substring(openBrace + 1, i - 1);
    }
}
