/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class SweetsWallLampBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(5, 3, 14, 11, 9, 16),
            box(7, 0, 10, 9, 3, 12),
            box(6, 3, 9, 10, 10, 13),
            box(5.5D, 10, 8.5D, 10.5D, 13, 13.5D),
            box(6.5D, 13, 8.5D, 11.5D, 15, 13.5D));

    public SweetsWallLampBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }

    /// / PLACEMENT ////

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getStateForWallPlacement(pContext);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return canSurviveOnWall(pState, pLevel, pPos);
    }
}
