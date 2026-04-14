package mod.crabmod.showercore.client.renderer;

/**
 * Pure-math helpers for the running-faucet drop visual drawn by
 * {@link BathtubBlockEntityRenderer} when a bathtub holds a CUSTOM fluid
 * and has RUNNING=true.
 *
 * This class has no Minecraft dependencies so its logic can be unit-tested
 * without a live client. The renderer adapts {@code net.minecraft.core.Direction}
 * and {@code BedPart} values to the simple enum/boolean inputs here.
 *
 * Coordinates are block-local (0..1) and mirror the geometry of the
 * bathtub_*_head_running model element at [7,10,3]..[9,11,4] (1/16 units).
 */
public final class BathtubDropGeometry {

    private BathtubDropGeometry() {}

    /** Cardinal horizontal direction of the faucet face on the bathtub HEAD. */
    public enum FaucetSide {
        NORTH, SOUTH, EAST, WEST
    }

    /**
     * Gating predicate: the drop visual only renders on the HEAD half
     * (the half with the faucet) while the faucet is actively running.
     */
    public static boolean shouldRenderDrop(boolean isHead, boolean running) {
        return isHead && running;
    }

    /**
     * Compute the AABB {fx1, fy1, fz1, fx2, fy2, fz2} of the drop cube
     * for the given faucet side, mirroring the head_running model element.
     */
    public static float[] computeDropBounds(FaucetSide faucetSide) {
        final float dropHalfWidth = 1f / 16f;   // 2px wide centered on the spout (model: x 7..9)
        final float dropDepth = 1f / 16f;       // 1px deep (model: z 3..4)
        final float dropOffset = 3f / 16f;      // spout is 3px in from the wall
        final float fy1 = 10f / 16f;
        final float fy2 = 11f / 16f;
        float fx1, fx2, fz1, fz2;
        switch (faucetSide) {
            case NORTH:
                fx1 = 0.5f - dropHalfWidth; fx2 = 0.5f + dropHalfWidth;
                fz1 = dropOffset;          fz2 = dropOffset + dropDepth;
                break;
            case SOUTH:
                fx1 = 0.5f - dropHalfWidth; fx2 = 0.5f + dropHalfWidth;
                fz1 = 1f - dropOffset - dropDepth; fz2 = 1f - dropOffset;
                break;
            case WEST:
                fx1 = dropOffset;          fx2 = dropOffset + dropDepth;
                fz1 = 0.5f - dropHalfWidth; fz2 = 0.5f + dropHalfWidth;
                break;
            case EAST:
            default:
                fx1 = 1f - dropOffset - dropDepth; fx2 = 1f - dropOffset;
                fz1 = 0.5f - dropHalfWidth; fz2 = 0.5f + dropHalfWidth;
                break;
        }
        return new float[] { fx1, fy1, fz1, fx2, fy2, fz2 };
    }

    /**
     * Resolve the tint color. When {@code customColor != -1} (from CustomFluidAPI),
     * force alpha=0xFF and use that color; otherwise return {@code fallbackTint}.
     */
    public static int resolveCustomColor(int customColor, int fallbackTint) {
        if (customColor != -1) {
            return 0xFF000000 | (customColor & 0x00FFFFFF);
        }
        return fallbackTint;
    }
}
