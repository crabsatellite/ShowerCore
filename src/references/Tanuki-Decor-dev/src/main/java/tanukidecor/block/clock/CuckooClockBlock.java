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

public class CuckooClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3, 1, 7.5D, 13, 13, 15.5D),
            box(6.5D, 13, 7.5D, 9.5D, 14, 15.5D),
            box(2.5D, 0, 7, 4.5D, 11, 9),
            box(11.5D, 0, 7, 13.5D, 11, 9),
            box(2.5D, 0, 14, 4.5D, 11, 16),
            box(11.5D, 0, 14, 13.5D, 11, 16),
            box(5.5D, -8.0D, 10.5D, 7.5D, -5.0D, 12.5D),
            box(8.5D, -5, 10.5D, 10.5D, -2, 12.5D));

    public CuckooClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.CUCKOO_CLOCK_TICK, TDRegistry.SoundReg.CUCKOO_CLOCK_CHIME,
                SHAPE, TDRegistry.BlockEntityReg.CUCKOO_CLOCK, pProperties);
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
