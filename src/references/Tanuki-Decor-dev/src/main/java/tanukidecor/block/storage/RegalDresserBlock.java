/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class RegalDresserBlock extends WideStorageBlock {

    public static final VoxelShape SHAPE_EAST = box(0, 0, 1, 16, 16, 15);
    public static final VoxelShape SHAPE_WEST = box(0, 0, 1, 16, 16, 15);

    public RegalDresserBlock(Properties pProperties) {
        super(TDRegistry.BlockEntityReg.REGAL_DRESSER, createShapeBuilder(SHAPE_EAST, SHAPE_WEST), pProperties);
    }
}
