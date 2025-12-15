/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TrainSetBlockEntity extends BlockEntity {

    protected boolean silent;

    public TrainSetBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TrainSetBlockEntity blockEntity) {
        // verify server side
        if (level.isClientSide()) {
            return;
        }
        // play sounds
        final int time = (int) ((level.getGameTime() + blockPos.asLong()) % 24000L);
        if (!blockEntity.isSilent()) {
            // play ambient sound
            if (time % 12 == 0) {
                level.playSound(null, blockPos, SoundEvents.GRASS_HIT, SoundSource.BLOCKS, 0.25F, 1.5F + level.getRandom().nextFloat() * 0.5F);
            }
            // play whistle
            if (level.getRandom().nextInt(600) == 0) {
                level.playSound(null, blockPos, SoundEvents.NOTE_BLOCK_FLUTE.value(), SoundSource.BLOCKS, 0.5F, 0.95F + level.getRandom().nextFloat() * 0.1F);
            }
        }
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
        this.setChanged();
    }

    /// / NBT ////

    private static final String KEY_SILENT = "Silent";

    @Override
    protected void loadAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.loadAdditional(pTag, pLookup);
        this.silent = pTag.getBoolean(KEY_SILENT);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.saveAdditional(pTag, pLookup);
        pTag.putBoolean(KEY_SILENT, this.silent);
    }
}
