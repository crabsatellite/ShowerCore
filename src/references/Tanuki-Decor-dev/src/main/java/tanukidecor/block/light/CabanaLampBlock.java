/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.light;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.block.TallBlock;

public class CabanaLampBlock extends TallBlock {

    public static final VoxelShape SHAPE_UPPER = Shapes.or(
            box(7, 0, 7, 9, 13, 9),
            box(4, 6, 4, 12, 16, 12));
    public static final VoxelShape SHAPE_LOWER = Shapes.or(
            box(7, 0, 7, 9, 16, 9),
            box(6, 0, 6, 10, 4, 10));

    public CabanaLampBlock(Properties pProperties) {
        super(SHAPE_UPPER, SHAPE_LOWER, pProperties);
    }
}
