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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class GreenClockBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(4, 0, 14, 12, 16, 16),
            box(0, 4, 14, 16, 12, 16),
            Shapes.join(
                    box(4, 4, 13, 12, 12, 14),
                    box(5, 5, 13, 11, 11, 14),
                    BooleanOp.ONLY_FIRST
            ));

    public GreenClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.ALARM_CLOCK_TICK, NO_SOUND,
                SHAPE, TDRegistry.BlockEntityReg.GREEN_CLOCK, pProperties);
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
