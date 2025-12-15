/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingWideBlock;

public class GorgeousCounterBlock extends RotatingWideBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 0, 1, 16, 14, 11),
            box(0, 14, 0, 16, 16, 12));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(6, 0, 5, 16, 14, 13),
            box(6, 14, 4, 16, 16, 14));

    public GorgeousCounterBlock(Properties pProperties) {
        super(pProperties, RotatingWideBlock.createShapeBuilder(SHAPE_EAST, SHAPE_WEST));
    }
}
