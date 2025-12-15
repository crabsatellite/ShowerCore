/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class AntiqueDeskBlock extends WideStorageBlock {

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            box(12, 0, 2, 14, 9, 4),
            box(11, 9, 1, 15, 14, 5),
            box(12, 0, 12, 14, 9, 14),
            box(11, 9, 11, 15, 14, 15),
            box(0, 10, 2, 14, 14, 14),
            box(0, 14, 0, 16, 16, 16));
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            box(2, 0, 2, 4, 9, 4),
            box(1, 9, 1, 5, 14, 5),
            box(2, 0, 12, 4, 9, 14),
            box(1, 9, 11, 5, 14, 15),
            box(2, 10, 2, 16, 14, 14),
            box(0, 14, 0, 16, 16, 16));

    public AntiqueDeskBlock(Properties pProperties) {
        super(TDRegistry.BlockEntityReg.ANTIQUE_DESK, createShapeBuilder(SHAPE_EAST, SHAPE_WEST), pProperties);
    }
}
