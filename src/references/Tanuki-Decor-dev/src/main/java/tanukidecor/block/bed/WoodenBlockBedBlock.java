/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodenBlockBedBlock extends SingleBedBlock {

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(1, 5, 4, 15, 8, 16),
            box(0, 3, 4, 16, 5, 16),
            box(0, 0, 0, 4, 4, 4),
            box(12, 0, 0, 16, 4, 4),
            box(0, 4, 0, 16, 9, 4));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(1, 5, 0, 15, 8, 12),
            box(0, 3, 0, 16, 5, 12),
            box(0, 0, 0, 4, 6, 16),
            box(12, 0, 12, 16, 6, 16),
            box(0, 6, 12, 16, 12, 16));

    public WoodenBlockBedBlock(Properties pProperties) {
        super(SHAPE_NORTH, SHAPE_SOUTH, pProperties);
    }
}
