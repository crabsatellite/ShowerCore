package mod.crabmod.showercore.client.renderer;

/**
 * Pure-math helpers for the running-faucet drop visual drawn by
 * {@link BathtubBlockEntityRenderer} when a bathtub holds a CUSTOM fluid
 * and has RUNNING=true.
 *
 * Coordinates are block-local (0..1) and mirror the geometry of the
 * bathtub_*_head_running model element at [7,10,3]..[9,11,4] (1/16 units).
 */
public final class BathtubDropGeometry {

    private BathtubDropGeometry() {}

    public enum FaucetSide {
        NORTH, SOUTH, EAST, WEST
    }

    public static boolean shouldRenderDrop(boolean isHead, boolean running) {
        return isHead && running;
    }

    public static float[] computeDropBounds(FaucetSide faucetSide) {
        final float dropHalfWidth = 1f / 16f;
        final float dropDepth = 1f / 16f;
        final float dropOffset = 3f / 16f;
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
}
