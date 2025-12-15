/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodenBlockChairBlock extends ChairBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(4, 0, 4, 6, 6, 6),
            box(10, 0, 4, 12, 6, 6),
            box(4, 0, 10, 6, 6, 12),
            box(10, 0, 10, 12, 6, 12),
            box(3, 6, 3, 13, 8, 13),
            box(4, 8, 11, 6, 10, 13),
            box(10, 8, 11, 12, 10, 13),
            box(3, 10, 11, 13, 16, 13));

    public WoodenBlockChairBlock(Properties pProperties) {
        super(SHAPE, 10.0D / 16.0D, pProperties);
    }
}
