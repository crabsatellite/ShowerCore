/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

public class RegalTableBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 11, 2, 14, 14, 14),
            box(0, 14, 0, 16, 16, 16),
            box(12, 11, 1, 15, 14, 4),
            box(12.5D, 10, 1.5D, 14.5D, 11, 3.5D),
            box(12, 0, 1, 15, 10, 4),
            box(12, 11, 12, 15, 14, 15),
            box(12.5D, 10, 12.5D, 14.5D, 11, 14.5D),
            box(12, 0, 12, 15, 10, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(2, 11, 2, 16, 14, 14),
            box(0, 14, 0, 16, 16, 16),
            box(1, 11, 1, 4, 14, 4),
            box(1.5D, 10, 1.5D, 3.5D, 11, 3.5D),
            box(1, 0, 1, 4, 10, 4),
            box(1, 11, 12, 4, 14, 15),
            box(1.5D, 10, 12.5D, 3.5D, 11, 14.5D),
            box(1, 0, 12, 4, 10, 15));

    public RegalTableBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }
}
