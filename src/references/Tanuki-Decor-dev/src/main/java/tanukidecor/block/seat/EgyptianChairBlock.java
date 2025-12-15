/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EgyptianChairBlock extends TallChairBlock {

    public static final VoxelShape UPPER_SHAPE = box(2, 0, 11, 14, 8, 14);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(2, 7, 2, 14, 10, 14),
            box(2, 10, 11, 14, 16, 14),
            box(2, 0, 2, 4, 2, 6),
            box(2, 10, 2, 4, 12, 4),
            box(12, 0, 2, 14, 2, 6),
            box(12, 10, 2, 14, 12, 4),
            box(2, 0, 12, 4, 2, 16),
            box(12, 0, 12, 14, 2, 16),
            box(2, 2, 4, 4, 7, 6),
            box(12, 2, 4, 14, 7, 6),
            box(2, 2, 12, 4, 7, 14),
            box(12, 2, 12, 14, 7, 14));

    public EgyptianChairBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, 12.0D / 16.0D, pProperties);
    }
}
