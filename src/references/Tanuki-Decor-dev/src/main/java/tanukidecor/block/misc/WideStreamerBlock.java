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
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;
import tanukidecor.block.RotatingWideBlock;

import java.util.Optional;

public class WideStreamerBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE = box(0, 0, 15, 16, 16, 16);

    public WideStreamerBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
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
