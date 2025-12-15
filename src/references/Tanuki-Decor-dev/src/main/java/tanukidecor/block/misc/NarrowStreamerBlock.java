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

import java.util.Optional;

public class NarrowStreamerBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = box(0, 0, 15, 16, 16, 16);

    public NarrowStreamerBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockState = super.getStateForPlacement(pContext);
        if (blockState != null) {
            // check this block has support
            Optional<Direction> direction = getSupportingDirection(blockState, pContext.getLevel(), pContext.getClickedPos());
            if (direction.isEmpty()) {
                return null;
            }
        }
        return blockState;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return getSupportingDirection(pState, pLevel, pPos).isPresent() && super.canSurvive(pState, pLevel, pPos);
    }

    /**
     * @param blockState the block state
     * @param level      the level
     * @param pos        the block position
     * @return the direction to the block that is supporting this one, if any
     */
    public static Optional<Direction> getSupportingDirection(BlockState blockState, LevelReader level, BlockPos pos) {
        final Direction facing = blockState.getValue(FACING);
        final BlockPos.MutableBlockPos mutablePos = pos.mutable();
        for (Direction direction : Direction.values()) {
            // skip position ahead and position below
            if (direction == facing || direction == Direction.DOWN) continue;
            // check if block has solid face
            BlockState supportingState = level.getBlockState(mutablePos.setWithOffset(pos, direction));
            if (supportingState.isFaceSturdy(level, mutablePos, direction.getOpposite())) {
                return Optional.of(direction);
            }
        }
        // no supporting face found
        return Optional.empty();
    }
}
