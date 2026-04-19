package mod.crabmod.showercore.block;

import mod.crabmod.showercore.testutil.TestSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Source-level regression test for Bug D-1 (1.20 Forge port) in
 * {@code BathtubBlock#use}.
 *
 * <p><b>Bug under regression:</b> When the player right-clicked a bathtub with
 * a bucket-like item and the fluid interaction could not complete (e.g. the
 * bathtub was already full, an empty bucket on an empty bathtub, incompatible
 * fluid type), {@code use} fell through and returned
 * {@code InteractionResult.FAIL}. That return value does NOT stop the
 * interaction chain on the 1.20 client path either: vanilla
 * {@code MultiPlayerGameMode.performUseItemOn} checks
 * {@code InteractionResult#consumesAction()} first, and
 * {@code FAIL.consumesAction()} is {@code false}, so the chain falls through to
 * {@code itemstack.useOn(ctx)}. {@code BucketItem} only overrides
 * {@code use(...)}, NOT {@code useOn(...)} — the default {@code Item#useOn}
 * returns {@code InteractionResult.PASS}. That PASS propagates back and
 * bypasses the {@code if (result == FAIL) return;} guard in
 * {@code Minecraft#startUseItem}, so the chain proceeds to
 * {@code gameMode.useItem} → {@code BucketItem#use} → {@code emptyContents} at
 * {@code hit.pos.relative(hit.getDirection())} — the block adjacent to the
 * bathtub. In creative mode the bucket item is NOT consumed, so a player
 * holding a water/lava bucket could repeatedly right-click a full bathtub to
 * spawn unlimited fluid in its neighboring grid.
 *
 * <p><b>Fix:</b> Near the end of the {@code be instanceof BathtubBlockEntity}
 * branch, after all fluid-interaction attempts have failed, a guard checks
 * whether the held item is a bucket-like type — {@code BucketItem},
 * {@code CustomFluidBucketItem}, or an item carrying a custom fluid via
 * {@code CustomFluidAPI.hasCustomFluid(...)} — and returns
 * {@code InteractionResult.CONSUME} to short-circuit the interaction chain at
 * {@code MultiPlayerGameMode.performUseItemOn}'s {@code consumesAction()}
 * check.
 *
 * <p><b>Why not FAIL:</b> {@code InteractionResult.FAIL} has
 * {@code consumesAction() == false}, so returning it here is not a hard stop —
 * see Bug D-1 root-cause chain above. Only {@code CONSUME} / {@code SUCCESS} /
 * {@code sidedSuccess} (all of which have {@code consumesAction()==true})
 * actually stop the chain.
 *
 * <p><b>Why file-content-based:</b> Minecraft classes (e.g. {@code BucketItem},
 * {@code InteractionResult}, {@code Player}, {@code BlockState}) are not on
 * the unit-test classpath, so we can't exercise {@code use} behaviorally. This
 * test enforces the fix as a textual invariant on the {@code use} method body:
 * the bucket-type guard must exist, cover all three bucket forms, sit inside
 * the {@code BathtubBlockEntity} branch, return a consumesAction result (not
 * FAIL), and appear BEFORE the trailing {@code return InteractionResult.PASS;}
 * fall-through.
 */
class BathtubBucketGuardRegressionTest {

    private static final Path BATHTUB_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "block", "BathtubBlock.java");

    // Match the use() signature tolerantly: modifiers may vary, but the return
    // type must be exactly 'InteractionResult' (not 'ItemInteractionResult',
    // which is the 1.21 NeoForge API). The negative lookbehind prevents
    // matching 'ItemInteractionResult' as a suffix.
    private static final Pattern USE_SIG = Pattern.compile(
            "(?<![A-Za-z0-9_.])InteractionResult\\s+use\\s*\\(");

    private static String body;

    @BeforeAll
    static void loadSource() throws IOException {
        String source = TestSourceUtils.readSource(BATHTUB_SOURCE);
        body = TestSourceUtils.extractMethodBody(source, USE_SIG, "BathtubBlock.use");
    }

    @Test
    @DisplayName("use body contains InteractionResult.CONSUME BEFORE the final InteractionResult.PASS fall-through")
    void useContainsBucketGuardBeforeFinalPass() {
        int consumeIdx = body.indexOf("InteractionResult.CONSUME");
        assertNotEquals(-1, consumeIdx,
                "use must contain 'InteractionResult.CONSUME' as the guard return for "
                        + "bucket-like items whose fluid interaction failed. CONSUME has "
                        + "consumesAction()==true so MultiPlayerGameMode.performUseItemOn "
                        + "short-circuits before falling through to itemstack.useOn. Returning "
                        + "FAIL here is insufficient: FAIL.consumesAction()==false, so the chain "
                        + "falls through to BucketItem's default useOn (PASS), which bypasses "
                        + "Minecraft.startUseItem's FAIL guard and reaches gameMode.useItem → "
                        + "BucketItem.use → fluid placement in the adjacent block (Bug D-1 dupe).");

        int lastPassIdx = body.lastIndexOf("return InteractionResult.PASS");
        assertNotEquals(-1, lastPassIdx,
                "use must retain its final 'return InteractionResult.PASS;' fall-through for "
                        + "non-bucket items that the bathtub does not handle.");

        assertTrue(
                consumeIdx < lastPassIdx,
                "The 'InteractionResult.CONSUME' guard must appear BEFORE the final "
                        + "'return InteractionResult.PASS;' so bucket-like items are caught and "
                        + "the click is consumed. Found CONSUME at body-index " + consumeIdx
                        + " and final PASS at body-index " + lastPassIdx + ". If the guard was "
                        + "moved AFTER the fall-through, the Bug D-1 exploit is reopened.");
    }

    @Test
    @DisplayName("Bucket guard checks all three bucket-like types (BucketItem, CustomFluidBucketItem, hasCustomFluid)")
    void bucketGuardChecksAllThreeBucketTypes() {
        Pattern bucketItem = Pattern.compile(
                "instanceof\\s+BucketItem\\b");
        Pattern customFluidBucket = Pattern.compile(
                "instanceof\\s+CustomFluidBucketItem\\b");
        Pattern hasCustomFluid = Pattern.compile(
                "CustomFluidAPI\\s*\\.\\s*hasCustomFluid\\s*\\(");

        assertTrue(
                bucketItem.matcher(body).find(),
                "use bucket-guard must check 'instanceof BucketItem' so vanilla buckets "
                        + "(water, lava, milk, etc.) that reach the fall-through still consume "
                        + "the click. Regex 'instanceof\\s+BucketItem\\b' did not match in the "
                        + "method body.");

        assertTrue(
                customFluidBucket.matcher(body).find(),
                "use bucket-guard must check 'instanceof CustomFluidBucketItem' so "
                        + "hotBath / custom-fluid buckets are also consumed on failure. Regex "
                        + "'instanceof\\s+CustomFluidBucketItem\\b' did not match in the method "
                        + "body.");

        assertTrue(
                hasCustomFluid.matcher(body).find(),
                "use bucket-guard must check 'CustomFluidAPI.hasCustomFluid(...)' so "
                        + "items that only carry a custom fluid via NBT (not via a "
                        + "BucketItem/CustomFluidBucketItem subclass) are also consumed. Regex "
                        + "'CustomFluidAPI\\s*\\.\\s*hasCustomFluid\\s*\\(' did not match in the "
                        + "method body.");
    }

    @Test
    @DisplayName("use body has exactly TWO InteractionResult.PASS returns (Rubber Duck early-out + trailing fall-through)")
    void useHasExpectedNumberOfPassReturns() {
        // Count all 'return InteractionResult.PASS;' statements in the body.
        // At the time this test was written there are exactly two:
        //   1) Rubber Duck early-out (held item is RUBBER_DUCK) — this PASSes through
        //      so vanilla BlockItem placement of the duck takes over. It fires BEFORE
        //      the bucket guard could ever run and is not part of the bucket-failure
        //      path, so it does NOT re-open Bug D-1.
        //   2) The single final fall-through at the end of the method for any other
        //      non-bucket item that the bathtub does not specially handle.
        //
        // The guard test {@link #useContainsBucketGuardBeforeFinalPass} covers the
        // ordering invariant. This test guards against a future refactor adding a
        // THIRD PASS return inside the fluid-interaction branch (which would re-open
        // Bug D-1 by letting vanilla BucketItem.use() fire on some failure path).
        //
        // NOTE: we use a regex that REQUIRES a terminating semicolon + word-boundary
        // so we don't accidentally match 'InteractionResult.PASS_TO_DEFAULT...' (1.21
        // API, not present here) or nested substrings.
        Pattern p = Pattern.compile(
                "return\\s+InteractionResult\\s*\\.\\s*PASS\\s*;");
        int count = 0;
        Matcher m = p.matcher(body);
        while (m.find()) count++;

        assertEquals(
                2, count,
                "use is expected to contain exactly 2 'return InteractionResult.PASS;' "
                        + "statements: (1) the Rubber Duck early-out at the top of the method, "
                        + "and (2) the final non-bucket fall-through at the bottom. Found "
                        + count + " occurrence(s). An ADDITIONAL PASS inside the "
                        + "fluid-interaction branch would re-open Bug D-1 by letting vanilla "
                        + "BucketItem.use() place fluid adjacent to a full bathtub on some "
                        + "failure paths. Removing a PASS would break either rubber-duck "
                        + "placement or the non-bucket fall-through. If the intended count has "
                        + "genuinely changed, update both this test and the ordering test in "
                        + "tandem.");

        // Ordering sanity: ensure the LAST PASS really is the trailing fall-through
        // (last statement before the closing brace), so the guard-before-final-PASS
        // test keeps meaning what it says.
        int lastPassIdx = body.lastIndexOf("return InteractionResult.PASS");
        String tail = body.substring(lastPassIdx).trim();
        assertTrue(
                tail.startsWith("return InteractionResult.PASS;"),
                "The final InteractionResult.PASS must be the LAST statement of use (tail of "
                        + "the method body). Tail snippet found: \""
                        + tail.substring(0, Math.min(tail.length(), 120)) + "\"");
    }

    @Test
    @DisplayName("Bucket guard sits inside or after the 'if (be instanceof BathtubBlockEntity bathtubBe)' block")
    void bucketGuardIsInsideBathtubBlockEntityCheck() {
        int bathtubBeIdx = body.indexOf("BathtubBlockEntity bathtubBe");
        assertNotEquals(-1, bathtubBeIdx,
                "Could not locate the 'BathtubBlockEntity bathtubBe' pattern binding in use. "
                        + "The fluid-interaction branch that contains the guard must begin with "
                        + "'if (be instanceof BathtubBlockEntity bathtubBe)'.");

        int consumeIdx = body.indexOf("InteractionResult.CONSUME");
        assertNotEquals(-1, consumeIdx,
                "Could not locate 'InteractionResult.CONSUME' in use body (see "
                        + "useContainsBucketGuardBeforeFinalPass for details).");

        assertTrue(
                bathtubBeIdx < consumeIdx,
                "The bucket guard ('InteractionResult.CONSUME') must appear AFTER the "
                        + "'BathtubBlockEntity bathtubBe' pattern binding so it only runs when "
                        + "the target is actually a bathtub block entity (and thus after all "
                        + "fluid interaction attempts). Found BathtubBlockEntity-binding at "
                        + "body-index " + bathtubBeIdx + " and CONSUME at body-index "
                        + consumeIdx + ". Hoisting the guard above the BathtubBlockEntity "
                        + "branch would affect rubber-duck / empty-hand / sit-on-bathtub "
                        + "interactions.");
    }

    @Test
    @DisplayName("use body must NOT return InteractionResult.FAIL from the bucket guard (Bug D-1 regression)")
    void useMustNotReturnFailFromBucketGuard() {
        // FAIL from the block is NOT a hard stop. MultiPlayerGameMode.performUseItemOn
        // checks interactionresult.consumesAction() first, and FAIL.consumesAction()
        // is false — so the chain falls through to itemstack.useOn(ctx). BucketItem
        // doesn't override useOn; default Item.useOn returns PASS. That PASS propagates
        // back as the return of gameMode.useItemOn, bypassing Minecraft.startUseItem's
        // 'if (result == FAIL) return;' guard. The chain then hits gameMode.useItem →
        // BucketItem.use → emptyContents at hit.relative(hit.getDirection()) = adjacent
        // block. Creative mode doesn't decrement the bucket stack, so the dupe repeats
        // on every click. Returning CONSUME (or SUCCESS / sidedSuccess) — anything with
        // consumesAction()==true — short-circuits at the first consumesAction check.
        assertTrue(
                !body.contains("InteractionResult.FAIL"),
                "use must NOT contain 'InteractionResult.FAIL'. FAIL has "
                        + "consumesAction()==false, so returning it from the bucket guard does "
                        + "NOT stop the interaction chain in MultiPlayerGameMode — it falls "
                        + "through to itemstack.useOn (PASS) and then to BucketItem.use, which "
                        + "places fluid in the adjacent block (Bug D-1 dupe in creative mode). "
                        + "Use InteractionResult.CONSUME instead.");
    }
}
