/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingBlock;

public class GorgeousLampBlock extends RotatingBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(5, 0, 5, 11, 5, 11),
            box(7, 5, 7, 9, 14, 9),
            box(3, 6, 3, 13, 16, 13));

    public GorgeousLampBlock(Properties pProperties) {
        super(pProperties, RotatingBlock.createShapeBuilder(SHAPE));
    }
}
