/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodenBlockStoolBlock extends ChairBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 3, 4, 5, 13),
            box(12, 0, 3, 16, 5, 13),
            box(0, 5, 3, 16, 10, 13));

    public WoodenBlockStoolBlock(Properties pProperties) {
        super(SHAPE, 12.0D / 16.0D, pProperties);
    }
}
