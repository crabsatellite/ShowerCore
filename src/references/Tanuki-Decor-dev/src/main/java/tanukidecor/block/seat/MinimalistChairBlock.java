/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MinimalistChairBlock extends TallChairBlock {

    public static final VoxelShape UPPER_SHAPE = box(1, 0, 12, 15, 8, 16);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(2, 0, 1, 4, 6, 3),
            box(12, 0, 1, 14, 6, 3),
            box(2, 0, 13, 4, 6, 15),
            box(12, 0, 13, 14, 6, 15),
            box(1, 6, 0, 15, 12, 16),
            box(1, 12, 12, 15, 16, 16));

    public MinimalistChairBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, 14.0D / 16.0D, pProperties);
    }
}
