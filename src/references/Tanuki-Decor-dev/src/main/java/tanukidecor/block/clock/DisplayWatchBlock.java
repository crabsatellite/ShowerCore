/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class DisplayWatchBlock extends ClockBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3, 0, 3, 13, 2, 13),
            box(4, 2, 4, 12, 12, 12),
            box(6.5D, 12, 6.5D, 9.5D, 16, 9.5D));

    public DisplayWatchBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.POCKET_WATCH_TICK, NO_SOUND,
                SHAPE, TDRegistry.BlockEntityReg.DISPLAY_WATCH, pProperties);
    }

    /// / CHIME PROVIDER ////

    @Override
    public float getTickVolume(BlockState blockState, RandomSource random, long dayTime) {
        return 0.4F;
    }
}
