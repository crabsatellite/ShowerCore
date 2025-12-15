/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

public class GorgeousTableBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(13, 0, 1, 15, 2, 3),
            box(13, 0, 13, 15, 2, 15),
            box(0, 14, 0, 16, 16, 16),
            box(10, 2, 1, 16, 4, 3),
            box(11, 4, 1, 14, 7, 3),
            box(11, 7, 1, 15, 11, 3),
            box(0, 11, 1, 14, 14, 3),
            box(0, 9, 1, 5, 11, 3),
            box(10, 2, 13, 16, 4, 15),
            box(0, 11, 13, 14, 14, 15),
            box(11, 7, 13, 15, 11, 15),
            box(11, 4, 13, 14, 7, 15),
            box(0, 9, 13, 5, 11, 15));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(1, 0, 1, 3, 2, 3),
            box(1, 0, 13, 3, 2, 15),
            box(0, 14, 0, 16, 16, 16),
            box(0, 2, 1, 6, 4, 3),
            box(2, 4, 1, 5, 7, 3),
            box(1, 7, 1, 5, 11, 3),
            box(2, 11, 1, 16, 14, 3),
            box(11, 9, 1, 16, 11, 3),
            box(0, 2, 13, 6, 4, 15),
            box(2, 11, 13, 16, 14, 15),
            box(1, 7, 13, 5, 11, 15),
            box(2, 4, 13, 5, 7, 15),
            box(11, 9, 13, 16, 11, 15));

    public GorgeousTableBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }
}
