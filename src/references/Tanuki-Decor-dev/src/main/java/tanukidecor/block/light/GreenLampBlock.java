/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class GreenLampBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(6.5D, 0, 6.5D, 9.5D, 2, 9.5D),
            box(7, 2, 7, 9, 5, 9),
            box(6.5D, 5, 6.5D, 9.5D, 8, 9.5D),
            box(3, 8, 3, 13, 16, 13));

    public GreenLampBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }
}
