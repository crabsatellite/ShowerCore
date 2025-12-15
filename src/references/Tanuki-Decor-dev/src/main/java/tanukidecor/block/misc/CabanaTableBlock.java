/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

public class CabanaTableBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(14, 0, 0, 16, 14, 2),
            box(14, 0, 14, 16, 14, 16),
            box(0, 14, 0, 16, 16, 16));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 0, 0, 2, 14, 2),
            box(0, 0, 14, 2, 14, 16),
            box(0, 14, 0, 16, 16, 16));

    public CabanaTableBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }
}
