/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MinimalistStoolBlock extends ChairBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 3, 6, 3),
            box(13, 0, 1, 15, 6, 3),
            box(1, 0, 13, 3, 6, 15),
            box(13, 0, 13, 15, 6, 15),
            box(0, 6, 0, 16, 12, 16));

    public MinimalistStoolBlock(Properties pProperties) {
        super(SHAPE, 14.0D / 16.0D, pProperties);
    }
}
