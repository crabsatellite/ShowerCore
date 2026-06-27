package mod.crabmod.showercore.block;

public final class BathtubWaterGeometry {
    public static final float DEFAULT_WATER_LEVEL = 0.6f;
    public static final float CLAWFOOT_WATER_LEVEL = 0.74f;
    public static final float DUCK_FLOAT_CLEARANCE = 0.05f;
    public static final double DUCK_SAFE_LOCAL_MIN = 6.0D / 16.0D;
    public static final double DUCK_SAFE_LOCAL_MAX = 10.0D / 16.0D;

    private BathtubWaterGeometry() {
    }

    public static float waterLevel(boolean clawfoot) {
        return clawfoot ? CLAWFOOT_WATER_LEVEL : DEFAULT_WATER_LEVEL;
    }

    public static float duckFloatSurface(boolean clawfoot) {
        return waterLevel(clawfoot) + DUCK_FLOAT_CLEARANCE;
    }

    public static double duckSafeLocalCoordinate(double coordinate) {
        return Math.max(DUCK_SAFE_LOCAL_MIN, Math.min(DUCK_SAFE_LOCAL_MAX, coordinate));
    }
}
