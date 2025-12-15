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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class RegalWallLampBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3, 3, 14, 13, 13, 16),
            box(7, 6, 13, 9, 10, 14),
            box(7.5D, 7.5D, 10, 8.5D, 8.5D, 13),
            Shapes.join(
                    box(2.5D, 7.5D, 9, 13.5D, 8.5D, 9),
                    box(3.5D, 7.5D, 7, 12.5D, 8.5D, 8),
                    BooleanOp.ONLY_FIRST
            ),
            box(2, 5.5D, 5, 4, 9.5D, 7),
            box(1, 9.5D, 4, 5, 15.5D, 8),
            box(12, 5.5D, 5, 14, 9.5D, 7),
            box(11, 9.5D, 4, 15, 15.5D, 8));

    public RegalWallLampBlock(Properties pProperties) {
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
