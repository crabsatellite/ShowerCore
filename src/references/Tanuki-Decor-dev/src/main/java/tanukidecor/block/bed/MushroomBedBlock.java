/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBedBlock extends SingleBedBlock {

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(1, 0, 2, 15, 8, 16),
            box(0, 0, 0, 16, 10, 2),
            box(2, 10, 0, 14, 12, 2));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(1, 0, 0, 15, 8, 14),
            box(0, 0, 14, 16, 12, 16),
            box(2, 12, 14, 14, 14, 16));


    public MushroomBedBlock(Properties pProperties) {
        super(SHAPE_NORTH, SHAPE_SOUTH, pProperties);
    }
}
