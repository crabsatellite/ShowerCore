/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.RotatingTallBlock;

public class MushroomLampBlock extends RotatingTallBlock {

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(6, 0, 7, 8, 11, 9),
            box(3, 8, 4, 11, 14, 12),
            box(4, 14, 5, 10, 16, 11));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(7, 0, 7, 9, 14, 9),
            box(6, 14, 7, 8, 16, 9));

    public MushroomLampBlock(Properties pProperties) {
        super(pProperties, RotatingTallBlock.createShapeBuilder(SHAPE_UPPER, SHAPE_LOWER));
    }
}
