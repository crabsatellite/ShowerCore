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
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class MinimalistClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = box(2, 2, 13, 14, 14, 16);

    public MinimalistClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.ALARM_CLOCK_TICK, NO_SOUND,
                SHAPE, TDRegistry.BlockEntityReg.MINIMALIST_CLOCK, pProperties);
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
