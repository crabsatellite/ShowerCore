/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tanukidecor.TDRegistry;

public class MinimalistMirrorBlock extends TallStorageBlock {

    public static final VoxelShape UPPER_SHAPE = box(0, 0, 13, 16, 12, 15);
    public static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(1, 0, 2, 3, 10, 4),
            box(13, 0, 2, 15, 10, 4),
            box(1, 0, 12, 3, 10, 14),
            box(13, 0, 12, 15, 10, 14),
            box(0, 10, 1, 16, 16, 15));

    public MinimalistMirrorBlock(Properties pProperties) {
        super(UPPER_SHAPE, LOWER_SHAPE, TDRegistry.BlockEntityReg.MINIMALIST_MIRROR, pProperties);
    }
}
