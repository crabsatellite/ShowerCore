/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SweetsChairBlock extends TallChairBlock {

    public static final VoxelShape UPPER_SHAPE = box(1, 0, 14, 15, 7, 16);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(2, 0, 2, 3, 8, 3),
            box(13, 0, 2, 14, 8, 3),
            box(2, 0, 13, 3, 8, 14),
            box(13, 0, 13, 14, 8, 14),
            box(1, 8, 1, 15, 10, 15),
            box(1, 9, 14, 15, 16, 16));

    public SweetsChairBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, 12.0D / 16.0D, pProperties);
    }
}
