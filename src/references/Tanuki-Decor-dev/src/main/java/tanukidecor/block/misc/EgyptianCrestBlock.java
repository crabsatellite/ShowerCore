/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

import java.util.Optional;

public class EgyptianCrestBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 5, 13, 5, 15, 16),
            box(1, 2, 14, 16, 12, 16));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(11, 5, 13, 16, 15, 16),
            box(0, 2, 14, 15, 12, 16));

    public EgyptianCrestBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockState = super.getStateForPlacement(pContext);
        if (blockState != null) {
            // check this block has support
            Optional<Direction> direction = NarrowStreamerBlock.getSupportingDirection(blockState, pContext.getLevel(), pContext.getClickedPos());
            if (direction.isEmpty()) {
                return null;
            }
            // check other side has support
            direction = NarrowStreamerBlock.getSupportingDirection(blockState.setValue(SIDE, blockState.getValue(SIDE).getOpposite()), pContext.getLevel(), getOppositeSide(blockState, pContext.getClickedPos()));
            if (direction.isEmpty()) {
                return null;
            }
        }
        return blockState;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return NarrowStreamerBlock.getSupportingDirection(pState, pLevel, pPos).isPresent() && super.canSurvive(pState, pLevel, pPos);
    }
}
