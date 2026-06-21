package mod.crabmod.showercore.entity;

public final class FaucetInteractionGeometry {
    public static final double ENTITY_WIDTH = 0.25D;
    public static final double ENTITY_HEIGHT = 0.32D;

    private static final double[][] LEGACY_HITBOXES = {
            {8.0D / 16.0D, 11.0D / 16.0D, 2.0D / 16.0D}
    };

    private static final double[][] CLAWFOOT_HITBOXES = {
            {14.0D / 16.0D, 15.0D / 16.0D, 7.0D / 16.0D},
            {14.0D / 16.0D, 15.0D / 16.0D, 10.0D / 16.0D},
            {14.0D / 16.0D, 15.0D / 16.0D, 13.0D / 16.0D}
    };

    private FaucetInteractionGeometry() {
    }

    public static double[][] hitboxOrigins(boolean clawfoot) {
        return clawfoot ? CLAWFOOT_HITBOXES : LEGACY_HITBOXES;
    }

    public static double worldX(int blockX, int rightStepX, int towardFootStepX, double localX, double localZ) {
        return blockX + 0.5D
                + rightStepX * (localX - 0.5D)
                + towardFootStepX * (localZ - 0.5D);
    }

    public static double worldY(int blockY, double localY) {
        return blockY + localY;
    }

    public static double worldZ(int blockZ, int rightStepZ, int towardFootStepZ, double localX, double localZ) {
        return blockZ + 0.5D
                + rightStepZ * (localX - 0.5D)
                + towardFootStepZ * (localZ - 0.5D);
    }
}
