/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public interface IDisplayProvider {

    Vector3f VEC_ZERO = new Vector3f(0, 0, 0);
    Vector3f VEC_ONE = new Vector3f(1.0F, 1.0F, 1.0F);

    /**
     * @param level       the level
     * @param blockState  the block state
     * @param blockPos    the block position
     * @param itemStack   the item stack
     * @param renderPass  the render pass
     * @param partialTick the partial tick
     * @return the rotation values for the x, y, and z axes in degrees
     */
    default Vector3f getDisplayRotation(final Level level, final BlockState blockState, final BlockPos blockPos, final ItemStack itemStack, final int renderPass, final float partialTick) {
        return VEC_ZERO;
    }

    /**
     * @param level       the level
     * @param blockState  the block state
     * @param blockPos    the block position
     * @param itemStack   the item stack
     * @param renderPass  the render pass
     * @param partialTick the partial tick
     * @return the translation values for the x, y, and z axes
     */
    default Vector3f getDisplayTranslation(final Level level, final BlockState blockState, final BlockPos blockPos, final ItemStack itemStack, final int renderPass, final float partialTick) {
        return VEC_ZERO;
    }

    /**
     * @param level       the level
     * @param blockState  the block state
     * @param blockPos    the block position
     * @param itemStack   the item stack
     * @param renderPass  the render pass
     * @param partialTick the partial tick
     * @return the scale percentage values for the x, y, and z axes
     */
    default Vector3f getDisplayScale(final Level level, final BlockState blockState, final BlockPos blockPos, final ItemStack itemStack, final int renderPass, final float partialTick) {
        return VEC_ONE;
    }
}
