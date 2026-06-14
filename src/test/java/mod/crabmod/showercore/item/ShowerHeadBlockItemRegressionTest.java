package mod.crabmod.showercore.item;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShowerHeadBlockItemRegressionTest {
    private static final Path ITEM_SOURCE = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "item", "ShowerHeadBlockItem.java");
    private static final Path BLOCKS_REGISTER = Paths.get(
            "src", "main", "java", "mod", "crabmod", "showercore", "registers", "BlocksRegister.java");

    @Test
    @DisplayName("Shower head blocks register the placement-aware block item")
    void showerHeadsUsePlacementAwareBlockItem() throws IOException {
        String source = Files.readString(BLOCKS_REGISTER);

        assertTrue(source.contains("import mod.crabmod.showercore.item.ShowerHeadBlockItem;"));
        assertTrue(source.contains("b instanceof ShowerHeadBlock"),
                "ShowerHeadBlock registrations must use the placement-aware item.");
        assertTrue(source.contains("return new ShowerHeadBlockItem(b, new Item.Properties());"));
    }

    @Test
    @DisplayName("Clawfoot height adjustment is gated to replaceable raised targets")
    void clawfootHeightAdjustmentIsGated() throws IOException {
        String source = Files.readString(ITEM_SOURCE);

        assertTrue(source.contains("updatePlacementContext"));
        assertTrue(source.contains("BlockPlaceContext.at(updated, raisedPos, updated.getClickedFace())"));
        assertTrue(source.contains("updated.getClickedPos().above()"));
        assertTrue(source.contains("getWorldBorder().isWithinBounds(raisedPos)"));
        assertTrue(source.contains("canBeReplaced(raisedContext)"));
        assertTrue(source.contains("supportPos = context.getClickedPos().below()"));
        assertTrue(source.contains("Direction.Plane.HORIZONTAL"));
        assertTrue(source.contains("startsWith(\"bathtub_clawfoot_\")"));
        assertFalse(source.contains("below(2)"),
                "Already-raised manual placement should not be raised a second time.");
    }
}
