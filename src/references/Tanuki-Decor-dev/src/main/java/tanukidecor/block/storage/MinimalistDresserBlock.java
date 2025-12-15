/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class MinimalistDresserBlock extends WideStorageBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(0, 2, 1, 16, 16, 15),
            box(13, 0, 2, 15, 2, 4),
            box(13, 0, 12, 15, 2, 14));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(0, 2, 1, 16, 16, 15),
            box(1, 0, 2, 3, 2, 4),
            box(1, 0, 12, 3, 2, 14));

    public MinimalistDresserBlock(Properties pProperties) {
        super(TDRegistry.BlockEntityReg.MINIMALIST_DRESSER, createShapeBuilder(SHAPE_EAST, SHAPE_WEST), pProperties);
    }
}
