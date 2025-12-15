/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

public class MinimalistTableBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(13, 0, 1, 15, 12, 3),
            box(13, 0, 13, 15, 12, 15),
            box(0, 12, 0, 16, 16, 16));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(1, 0, 1, 3, 12, 3),
            box(1, 0, 13, 3, 12, 15),
            box(0, 12, 0, 16, 16, 16));

    public MinimalistTableBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }
}
