/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class ShipInABottleBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(3, 0, 4, 13, 3, 12),
            box(1.5D, 3, 3, 14.5D, 13, 13),
            box(14.5D, 6.5D, 6.5D, 16.5D, 9.5D, 9.5D),
            box(16.5D, 6, 6, 18.5D, 10, 10));

    public ShipInABottleBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }
}
