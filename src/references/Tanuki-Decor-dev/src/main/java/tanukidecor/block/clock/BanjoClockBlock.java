/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class BanjoClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(4, 0, 11, 12, 8, 16),
            box(7, 8, 12, 9, 10, 15));
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(2, 0, 10, 14, 8, 16),
            box(5.5D, 8, 12, 10.5D, 16, 15));

    public BanjoClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.MEDIUM_CLOCK_TICK, TDRegistry.SoundReg.MEDIUM_CLOCK_CHIME,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.BANJO_CLOCK, pProperties);
    }
}
