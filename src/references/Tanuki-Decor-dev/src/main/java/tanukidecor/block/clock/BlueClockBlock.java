/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class BlueClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(2, 0, 2, 14, 14, 14),
            box(4, 14, 2, 12, 16, 14));
    public static final VoxelShape LOWER_SHAPE = box(2, 0, 2, 14, 16, 14);

    public BlueClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.GRANDFATHER_CLOCK_TICK, TDRegistry.SoundReg.GRANDFATHER_CLOCK_CHIME,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.BLUE_CLOCK, pProperties);
    }

    /// / CHIME PROVIDER ////

    @Override
    public int getTickSoundInterval(BlockState blockState) {
        return 40;
    }
}
