package mod.crabmod.showercore.block;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BathtubWaterGeometryTest {
    private static final float EPS = 0.0001f;

    @Test
    @DisplayName("Legacy and clawfoot water surfaces keep their visual heights")
    void waterLevelsMatchRendererExpectations() {
        assertEquals(0.6f, BathtubWaterGeometry.waterLevel(false), EPS);
        assertEquals(0.74f, BathtubWaterGeometry.waterLevel(true), EPS);
    }

    @Test
    @DisplayName("Rubber Duck floats just above the rendered bathtub water surface")
    void duckFloatSurfaceTracksWaterLevel() {
        assertEquals(0.65f, BathtubWaterGeometry.duckFloatSurface(false), EPS);
        assertEquals(0.79f, BathtubWaterGeometry.duckFloatSurface(true), EPS);
    }
}
