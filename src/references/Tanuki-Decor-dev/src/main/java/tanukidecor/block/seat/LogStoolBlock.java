/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.VoxelShape;

public class LogStoolBlock extends ChairBlock {

    public static final VoxelShape SHAPE = box(2, 0, 2, 14, 14, 14);

    public LogStoolBlock(Properties pProperties) {
        super(SHAPE, 1.0D, pProperties);
    }
}
