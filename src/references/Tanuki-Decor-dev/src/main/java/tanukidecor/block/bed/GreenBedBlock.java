/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.bed;


import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GreenBedBlock extends SingleBedBlock {

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(0, 0, 0, 16, 12, 3),
            box(0, 2, 3, 16, 8, 16));
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Shapes.join(
                    box(0, 0, 13, 16, 16, 16),
                    box(1, 10, 13, 15, 15, 15.88D),
                    BooleanOp.ONLY_FIRST),
            box(0, 2, 0, 16, 8, 13),
            box(4, 8, 7, 12, 10, 12));


    public GreenBedBlock(Properties pProperties) {
        super(SHAPE_NORTH, SHAPE_SOUTH, pProperties);
    }
}
