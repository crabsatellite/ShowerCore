package mod.crabmod.showercore.block;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShowerHeadPlacementRegressionTest {
    private static final Path BLOCKS_REGISTER = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "registers", "BlocksRegister.java");
    private static final Path PLACEMENT_ITEM = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "item", "ShowerHeadBlockItem.java");

    @Test
    @DisplayName("Shower heads do not adjust placement from nearby bathtubs")
    void showerHeadsDoNotAdjustPlacementFromNearbyBathtubs() throws IOException {
        String source = Files.readString(BLOCKS_REGISTER);

        assertFalse(Files.exists(PLACEMENT_ITEM),
                "Shower heads should use normal BlockItem placement; players may place the shower head before the bathtub.");
        assertFalse(source.contains("ShowerHeadBlockItem"));
        assertFalse(source.contains("b instanceof ShowerHeadBlock"));
        assertFalse(source.contains("updatePlacementContext"));
        assertFalse(source.contains("BlockPlaceContext.at"));
    }
}
