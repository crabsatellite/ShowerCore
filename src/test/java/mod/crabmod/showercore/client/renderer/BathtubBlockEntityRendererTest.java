package mod.crabmod.showercore.client.renderer;

import mod.crabmod.showercore.client.renderer.BathtubDropGeometry.FaucetSide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the faucet-drop visual fix in {@link BathtubBlockEntityRenderer}.
 *
 * Bug fix under test: when a bathtub contains a CUSTOM fluid and the faucet is opened
 * (RUNNING=true), a small water-drop cube must be drawn at the faucet spout even though
 * the blockstate picks the "head_empty" model (which has no running-water geometry).
 *
 * These tests exercise the pure geometry + gating + color-resolution helpers extracted
 * into {@link BathtubDropGeometry} so they can run without a Minecraft client.
 */
class BathtubBlockEntityRendererTest {

    private static final float EPS = 1e-6f;
    private static final float EXPECTED_FY1 = 10f / 16f;
    private static final float EXPECTED_FY2 = 11f / 16f;

    // ---- Geometry tests (one per facing) ---------------------------------

    @Test
    @DisplayName("NORTH-facing head: drop sits at x [7,9]/16, z [3,4]/16")
    void dropBounds_north() {
        float[] b = BathtubDropGeometry.computeDropBounds(FaucetSide.NORTH);
        assertBounds(b, 7f / 16f, 9f / 16f, 3f / 16f, 4f / 16f);
    }

    @Test
    @DisplayName("SOUTH-facing head: drop mirrors to z [12,13]/16")
    void dropBounds_south() {
        float[] b = BathtubDropGeometry.computeDropBounds(FaucetSide.SOUTH);
        assertBounds(b, 7f / 16f, 9f / 16f, 12f / 16f, 13f / 16f);
    }

    @Test
    @DisplayName("EAST-facing head: drop rotates to x [12,13]/16, z [7,9]/16")
    void dropBounds_east() {
        float[] b = BathtubDropGeometry.computeDropBounds(FaucetSide.EAST);
        assertBounds(b, 12f / 16f, 13f / 16f, 7f / 16f, 9f / 16f);
    }

    @Test
    @DisplayName("WEST-facing head: drop rotates to x [3,4]/16, z [7,9]/16")
    void dropBounds_west() {
        float[] b = BathtubDropGeometry.computeDropBounds(FaucetSide.WEST);
        assertBounds(b, 3f / 16f, 4f / 16f, 7f / 16f, 9f / 16f);
    }

    @Test
    @DisplayName("Y bounds (10/16..11/16) are identical for all 4 facings")
    void dropBounds_yConstantAcrossFacings() {
        for (FaucetSide s : FaucetSide.values()) {
            float[] b = BathtubDropGeometry.computeDropBounds(s);
            assertEquals(EXPECTED_FY1, b[1], EPS, "fy1 for " + s);
            assertEquals(EXPECTED_FY2, b[4], EPS, "fy2 for " + s);
        }
    }

    @Test
    @DisplayName("Drop footprint is 2x1 (px) in the horizontal plane for every facing")
    void dropBounds_dimensionsInvariant() {
        for (FaucetSide s : FaucetSide.values()) {
            float[] b = BathtubDropGeometry.computeDropBounds(s);
            float dx = b[3] - b[0];
            float dy = b[4] - b[1];
            float dz = b[5] - b[2];
            boolean wideX = Math.abs(dx - 2f / 16f) < EPS && Math.abs(dz - 1f / 16f) < EPS;
            boolean wideZ = Math.abs(dz - 2f / 16f) < EPS && Math.abs(dx - 1f / 16f) < EPS;
            assertTrue(wideX || wideZ, "Unexpected footprint for " + s + ": dx=" + dx + " dz=" + dz);
            assertEquals(1f / 16f, dy, EPS, "height for " + s);
        }
    }

    @Test
    @DisplayName("Drop is at y=10/16..11/16 exactly (matches model element y=[10,11])")
    void dropBounds_yMatchesModelElement() {
        float[] b = BathtubDropGeometry.computeDropBounds(FaucetSide.NORTH);
        // from=[7,10,3] to=[9,11,4] in model space (1/16 units)
        assertEquals(10f / 16f, b[1], EPS);
        assertEquals(11f / 16f, b[4], EPS);
    }

    // ---- Gating tests: no-render for RUNNING=false or FOOT ----------------

    @Test
    @DisplayName("Drop does NOT render when RUNNING=false, even on HEAD")
    void shouldRenderDrop_falseWhenNotRunning() {
        assertFalse(BathtubDropGeometry.shouldRenderDrop(true, false));
    }

    @Test
    @DisplayName("Drop does NOT render on FOOT part even when RUNNING=true")
    void shouldRenderDrop_falseForFoot() {
        assertFalse(BathtubDropGeometry.shouldRenderDrop(false, true));
    }

    @Test
    @DisplayName("Drop does NOT render on FOOT when RUNNING=false")
    void shouldRenderDrop_falseForFootNotRunning() {
        assertFalse(BathtubDropGeometry.shouldRenderDrop(false, false));
    }

    @Test
    @DisplayName("Drop renders only when HEAD and RUNNING=true")
    void shouldRenderDrop_trueForHeadRunning() {
        assertTrue(BathtubDropGeometry.shouldRenderDrop(true, true));
    }

    // ---- Color resolution tests -------------------------------------------

    @Test
    @DisplayName("customColor != -1 with alpha=0: result forces 0xFF alpha, preserves RGB")
    void resolveCustomColor_usesCustomWithForcedOpaqueAlpha() {
        int custom = 0x0012_34_56; // alpha=0 (as CustomFluidAPI typically returns)
        int fallback = 0xAA_ABCDEF;
        int result = BathtubDropGeometry.resolveCustomColor(custom, fallback);
        assertEquals(0xFF_12_34_56, result, () -> "got 0x" + Integer.toHexString(result));
    }

    @Test
    @DisplayName("customColor != -1 with non-zero alpha: alpha still forced to 0xFF")
    void resolveCustomColor_forcesAlphaEvenWhenCustomAlphaIsNonZero() {
        int custom = 0x33_AA_BB_CC;
        int result = BathtubDropGeometry.resolveCustomColor(custom, 0);
        assertEquals(0xFF_AA_BB_CC, result);
    }

    @Test
    @DisplayName("customColor == -1: falls back to IClientFluidTypeExtensions tint color (verbatim)")
    void resolveCustomColor_fallsBackWhenNoCustomColor() {
        int fallback = 0x80_11_22_33;
        int result = BathtubDropGeometry.resolveCustomColor(-1, fallback);
        assertEquals(fallback, result);
    }

    @Test
    @DisplayName("customColor == -1: even negative fallback values are returned unchanged")
    void resolveCustomColor_signedFallbackPassthrough() {
        int fallback = 0xFFFFFFFF; // = -1 as int, but since we're in fallback branch that's irrelevant
        int result = BathtubDropGeometry.resolveCustomColor(-1, fallback);
        assertEquals(fallback, result);
    }

    @Test
    @DisplayName("Distinct custom colors yield distinct rendered colors (no per-fluid flattening)")
    void resolveCustomColor_distinctFluidsStayDistinct() {
        // Regression for bug: "all custom fluids in bathtub show same color".
        // When each fluid's CustomFluidAPI.getFluidColor(id) returns a distinct value,
        // the renderer must produce distinct opaque ARGB results - not collapse them to
        // a shared FluidType tint.
        int sharedFallback = 0xFFFFFFFF; // what the shared DYNAMIC_FLUID_STILL tint would be

        int red    = BathtubDropGeometry.resolveCustomColor(0x00FF0000, sharedFallback);
        int green  = BathtubDropGeometry.resolveCustomColor(0x0000FF00, sharedFallback);
        int blue   = BathtubDropGeometry.resolveCustomColor(0x000000FF, sharedFallback);
        int purple = BathtubDropGeometry.resolveCustomColor(0x008844CC, sharedFallback);

        assertEquals(0xFFFF0000, red);
        assertEquals(0xFF00FF00, green);
        assertEquals(0xFF0000FF, blue);
        assertEquals(0xFF8844CC, purple);
        // Sanity: no two fluids collide.
        assertNotEquals(red, green);
        assertNotEquals(green, blue);
        assertNotEquals(red, purple);
        assertNotEquals(blue, purple);
    }

    // ---- Helpers ----------------------------------------------------------

    private static void assertBounds(float[] b, float expectedFx1, float expectedFx2,
                                     float expectedFz1, float expectedFz2) {
        assertEquals(6, b.length, "bounds array must be {fx1,fy1,fz1,fx2,fy2,fz2}");
        float[] expected = {expectedFx1, EXPECTED_FY1, expectedFz1, expectedFx2, EXPECTED_FY2, expectedFz2};
        assertArrayEquals(expected, b, EPS,
                () -> "expected " + java.util.Arrays.toString(expected) + " got " + java.util.Arrays.toString(b));
    }
}
