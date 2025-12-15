/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

public class EgyptianTableBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(14, 0, 1, 16, 10/*4*/, 3),
            box(14, 0, 13, 16, 10/*4*/, 15),
            box(14, 1, 3, 16, 3, 13),
            box(0, 10, 0, 16, 12, 16),
            box(0, 12, 1, 15, 14, 15),
            box(0, 14, 0, 16, 16, 16),
            box(0, 8, 1, 10, 10, 3),
            box(0, 8, 13, 10, 10, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 0, 1, 2, 10/*4*/, 3),
            box(0, 0, 13, 2, 10/*4*/, 15),
            box(0, 1, 3, 2, 3, 13),
            box(0, 10, 0, 16, 12, 16),
            box(1, 12, 1, 16, 14, 15),
            box(0, 14, 0, 16, 16, 16),
            box(6, 8, 1, 16, 10, 3),
            box(6, 8, 13, 16, 10, 15));

    public EgyptianTableBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }
}
