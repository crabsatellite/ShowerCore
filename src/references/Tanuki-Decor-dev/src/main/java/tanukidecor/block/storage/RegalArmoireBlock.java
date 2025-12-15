/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class RegalArmoireBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = box(0, 0, 1, 16, 16, 15);
    public static final VoxelShape LOWER_SHAPE = box(0, 0, 1, 16, 16, 15);

    public RegalArmoireBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.REGAL_ARMOIRE, pProperties);
    }
}
