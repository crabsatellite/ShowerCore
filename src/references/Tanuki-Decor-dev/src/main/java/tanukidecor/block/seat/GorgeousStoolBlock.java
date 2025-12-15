/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GorgeousStoolBlock extends ChairBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(5, 0, 5, 11, 2, 11),
            box(7, 2, 7, 9, 9, 9),
            box(2, 9, 2, 14, 12, 14));

    public GorgeousStoolBlock(Properties pProperties) {
        super(SHAPE, 14.0D / 16.0D, pProperties);
    }
}
