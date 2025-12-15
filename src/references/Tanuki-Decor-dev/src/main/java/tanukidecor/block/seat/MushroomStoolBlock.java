/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.seat;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomStoolBlock extends ChairBlock {

    public static final VoxelShape SHAPE = Shapes.or(
            box(4, 0, 4, 12, 8, 12),
            box(2, 4, 2, 14, 12, 14));

    public MushroomStoolBlock(Properties pProperties) {
        super(SHAPE, 1.0D, pProperties);
    }
}
