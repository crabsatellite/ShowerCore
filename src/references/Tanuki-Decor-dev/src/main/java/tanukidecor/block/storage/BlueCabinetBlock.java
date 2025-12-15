/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class BlueCabinetBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = box(0, 0, 1, 16, 16, 15);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 0, 1, 2, 2, 3),
            box(14, 0, 1, 16, 2, 3),
            box(0, 0, 13, 2, 2, 15),
            box(14, 0, 13, 16, 2, 15),
            box(0, 2, 1, 16, 16, 15));

    public BlueCabinetBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.BLUE_CABINET, pProperties);
    }
}
