/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlueBedBlock extends SingleBedBlock {

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(0, 0, 0, 2, 12, 2),
            box(14, 0, 0, 16, 12, 2),
            box(2, 1, 0, 14, 11, 2),
            box(0, 2, 2, 16, 8, 16));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(0, 0, 14, 2, 12, 16),
            box(14, 0, 14, 16, 12, 16),
            box(2, 1, 14, 14, 11, 16),
            box(0, 2, 0, 16, 8, 14),
            box(3, 8, 7, 13, 10, 12));


    public BlueBedBlock(Properties pProperties) {
        super(SHAPE_NORTH, SHAPE_SOUTH, pProperties);
    }
}
