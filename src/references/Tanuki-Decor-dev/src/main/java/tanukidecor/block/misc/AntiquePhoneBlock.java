/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class AntiquePhoneBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(2, 1, 15, 14, 15, 16),
            box(4, 3, 8, 12, 13, 15),
            box(8.5D, 9, 6, 11.5D, 12, 8),
            box(4.5D, 9, 6, 7.5D, 12, 8),
            box(7, 4.5D, 6, 9, 6.5D, 8),
            box(6.5D, 4, 4, 9.5D, 7, 6),
            box(3, 5, 10, 4, 8, 13),
            box(2, 6, 11, 3, 7, 12),
            box(1, 5, 11, 2, 9, 12),
            box(-1, 7.5D, 10.5D, 1, 9.5D, 12.5D),
            box(12, 2, 10, 14, 7, 12),
            box(11.5D, 1, 9.5D, 14.5D, 2, 12.5D));

    public AntiquePhoneBlock(Properties pProperties) {
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
