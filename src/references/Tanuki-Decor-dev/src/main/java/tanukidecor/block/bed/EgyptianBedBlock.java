/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EgyptianBedBlock extends SingleBedBlock {

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(0, 5, 0, 16, 8, 16),
            box(1, 8, 1, 15, 10, 16),
            box(0, 0, 0, 2, 2, 3),
            box(0, 2, 0, 2, 10, 2),
            box(14, 0, 0, 16, 2, 3),
            box(14, 2, 0, 16, 10, 2),
            box(0, 2, 1, 16, 4, 3));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(0, 5, 0, 16, 8, 14),
            box(0, 8, 12, 16, 14, 14),
            box(1, 8, 0, 15, 10, 16),
            box(1, 6, 10, 15, 16, 16),
            box(0, 0, 12, 2, 2, 15),
            box(0, 2, 12, 2, 5, 14),
            box(14, 0, 12, 16, 2, 15),
            box(14, 2, 12, 16, 5, 14),
            box(0, 2, 13, 16, 4, 15));


    public EgyptianBedBlock(Properties pProperties) {
        super(SHAPE_NORTH, SHAPE_SOUTH, pProperties);
    }
}
