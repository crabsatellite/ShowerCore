/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IDelegateProvider {

    /**
     * @param blockState the block state
     * @param blockPos   the block position
     * @return the block position to delegate to
     */
    BlockPos getDelegatePos(final BlockState blockState, final BlockPos blockPos);
}
