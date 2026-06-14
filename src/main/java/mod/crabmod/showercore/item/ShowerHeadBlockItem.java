package mod.crabmod.showercore.item;

import mod.crabmod.showercore.block.BathtubBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class ShowerHeadBlockItem extends BlockItem {
    public ShowerHeadBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        BlockPlaceContext updated = super.updatePlacementContext(context);
        if (updated == null || !shouldRaiseForClawfoot(updated)) {
            return updated;
        }

        BlockPos raisedPos = updated.getClickedPos().above();
        BlockPlaceContext raisedContext = BlockPlaceContext.at(updated, raisedPos, updated.getClickedFace());
        if (!canPlaceAt(raisedContext, raisedPos)) {
            return updated;
        }
        return raisedContext;
    }

    private static boolean canPlaceAt(BlockPlaceContext raisedContext, BlockPos raisedPos) {
        Level level = raisedContext.getLevel();
        return level.getWorldBorder().isWithinBounds(raisedPos)
                && level.getBlockState(raisedPos).canBeReplaced(raisedContext);
    }

    private static boolean shouldRaiseForClawfoot(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos supportPos = context.getClickedPos().below();
        if (isClawfootBathtub(level.getBlockState(supportPos))) {
            return true;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (isClawfootBathtub(level.getBlockState(supportPos.relative(direction)))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isClawfootBathtub(BlockState state) {
        if (!(state.getBlock() instanceof BathtubBlock)) {
            return false;
        }
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return blockId != null && blockId.getPath().startsWith("bathtub_clawfoot_");
    }
}
