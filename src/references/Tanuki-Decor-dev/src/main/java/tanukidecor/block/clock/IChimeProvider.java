/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.clock;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IChimeProvider {

    long DAWN = 10L;
    long NOON = 6000L;
    long MIDNIGHT = 18000L;
    long MIN_CHIME_INTERVAL = 40L;

    /**
     * @param blockState the block state
     * @return the chime sound, if any
     **/
    @Nullable
    default SoundEvent getChimeSound(BlockState blockState) {
        return null;
    }

    /**
     * @param blockState the block state
     * @return the tick sound, if any
     **/
    @Nullable
    default SoundEvent getTickSound(BlockState blockState) {
        return null;
    }

    /**
     * @param blockState the block state
     * @return the number of ticks to wait before playing the next tick sound
     **/
    default int getTickSoundInterval(BlockState blockState) {
        return 20;
    }

    /**
     * @param blockState the block state
     * @param dayTime    the day time
     * @return true to play a chime sound in this tick
     */
    default boolean isTimeToChime(BlockState blockState, final long dayTime) {
        return dayTime == NOON || dayTime == (NOON + MIN_CHIME_INTERVAL) || dayTime == MIDNIGHT;
    }

    /**
     * @param blockState the block state
     * @param random     the random instance
     * @param dayTime    the day time from 0 to 24000
     * @return the volume of the chime sound
     */
    default float getChimeVolume(BlockState blockState, RandomSource random, long dayTime) {
        return 1.0F;
    }

    /**
     * @param blockState the block state
     * @param random     the random instance
     * @param dayTime    the day time from 0 to 24000
     * @return the pitch of the chime sound
     */
    default float getChimePitch(BlockState blockState, RandomSource random, long dayTime) {
        return 1.0F;
    }

    /**
     * @param blockState the block state
     * @param random     the random instance
     * @param dayTime    the day time from 0 to 24000
     * @return the volume of the tick sound
     */
    default float getTickVolume(BlockState blockState, RandomSource random, long dayTime) {
        return 0.6F;
    }

    /**
     * @param blockState the block state
     * @param random     the random instance
     * @param dayTime    the day time from 0 to 24000
     * @return the pitch of the tick sound
     */
    default float getTickPitch(BlockState blockState, RandomSource random, long dayTime) {
        return 1.0F;
    }
}
