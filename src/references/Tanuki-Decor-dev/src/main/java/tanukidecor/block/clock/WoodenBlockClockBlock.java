/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class WoodenBlockClockBlock extends TallClockBlock {

    public static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(0.2D, 0, 10, 15.8D, 2, 16),
            box(2.2D, 2, 10, 13.8D, 4, 16),
            box(4.2D, 4, 10, 11.8D, 6, 16),
            box(6.2D, 6, 10, 9.8D, 8, 16));
    public static final VoxelShape LOWER_SHAPE = box(2, 4, 10, 14, 16, 16);

    public WoodenBlockClockBlock(Properties pProperties) {
        super(TDRegistry.SoundReg.CUCKOO_CLOCK_TICK, ClockBlock.NO_SOUND,
                UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.WOODEN_BLOCK_CLOCK, pProperties);
    }
}
