/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MinimalistBedBlock extends SingleBedBlock {

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(0, 3, 0, 16, 8, 16),
            box(1, 0, 1, 3, 3, 3),
            box(13, 0, 1, 15, 3, 3));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(0, 3, 0, 16, 8, 16),
            box(4, 8, 10, 12, 10, 15),
            box(1, 0, 13, 3, 3, 15),
            box(13, 0, 13, 15, 3, 15));


    public MinimalistBedBlock(Properties pProperties) {
        super(SHAPE_NORTH, SHAPE_SOUTH, pProperties);
    }
}
