/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class AntiqueBureauBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = box(0, 0, 4, 16, 8, 14);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 2, 2, 16, 16, 14),
            box(0, 0, 2, 2, 2, 4),
            box(14, 0, 2, 16, 2, 4),
            box(0, 0, 12, 2, 2, 14),
            box(14, 0, 12, 16, 2, 14));

    public AntiqueBureauBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.ANTIQUE_BUREAU, pProperties);
    }
}
