/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class GreenDresserBlock extends WideStorageBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 1, 0, 16, 16, 16),
            box(10, 0, 0, 16, 1, 4),
            box(10, 0, 12, 16, 1, 16));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 1, 0, 16, 16, 16),
            box(0, 0, 0, 6, 1, 4),
            box(0, 0, 12, 6, 1, 16));

    public GreenDresserBlock(Properties pProperties) {
        super(TDRegistry.BlockEntityReg.GREEN_DRESSER, createShapeBuilder(SHAPE_EAST, SHAPE_WEST), pProperties);
    }
}
