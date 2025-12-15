/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.TallBlock;

public class MinimalistLampBlock extends TallBlock {

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(3, 8, 3, 13, 16, 13),
            box(7, 0, 7, 9, 14, 9));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(3, 0, 3, 13, 2, 13),
            box(7, 2, 7, 9, 16, 9));

    public MinimalistLampBlock(Properties pProperties) {
        super(SHAPE_UPPER, SHAPE_LOWER, pProperties);
    }
}
