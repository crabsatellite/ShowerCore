/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingTallBlock;

public class GumballMachineBlock extends RotatingTallBlock {

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(3, 1, 3, 13, 11, 13),
            box(4, 11, 4, 12, 12, 12));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(3, 0, 3, 13, 1, 13),
            box(7, 1, 7, 9, 9, 9),
            box(4, 9, 4, 12, 17, 12));

    public GumballMachineBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
    }
}
