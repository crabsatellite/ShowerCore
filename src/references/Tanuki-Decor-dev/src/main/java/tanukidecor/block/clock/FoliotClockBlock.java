/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class FoliotClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(1.5D, 12, 12, 14.5D, 13, 13),
            box(2.5D, 9, 12.5D, 4.5D, 12, 12.5D),
            box(11.5D, 9, 12.5D, 13.5D, 12, 12.5D),
            box(7.5D, 11, 12, 8.5D, 12, 13),
            box(7.5D, 13, 12, 8.5D, 14, 15),
            box(7.5D, 10, 11, 8.5D, 11, 15),
            box(7.5D, 1, 11, 8.5D, 2, 15),
            box(7, 0, 10, 9, 12, 11),
            box(9, 0, 15, 12, 2, 16),
            box(4, 0, 15, 7, 2, 16),
            box(7, 0, 15, 9, 16, 16),
            box(4, 2, 9, 12, 10, 10),
            box(7, 3, 13, 9, 5, 14),
            box(7.5D, 3.5D, 11, 8.5D, 4.5D, 15),
            box(8.5D, 5.5D, 11, 9.5D, 6.5D, 15),
            box(7.5D, 7.5D, 11, 8.5D, 8.5D, 15),
            box(7, 14, 15, 9, 16, 16),
            box(6, -6, 12.5D, 8, -2, 14.5D),
            box(8.5D, -3, 13, 9.5D, 0, 14));

    public FoliotClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.FOLIOT_CLOCK_TICK, NO_SOUND,
                SHAPE, TDRegistry.BlockEntityReg.FOLIOT_CLOCK, pProperties);
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
