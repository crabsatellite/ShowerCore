/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tanukidecor.block.misc.HourglassBlock;

public class HourglassBlockEntity extends BlockEntity {

    public static final int TIME = 600;
    private int maxTimer;
    private int timer;

    public HourglassBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, HourglassBlockEntity blockEntity) {
        if (blockEntity.isActive()) {
            blockEntity.updateTimer();
        }
    }

    public int getRedstoneStrength() {
        if (this.isActive()) {
            return Mth.ceil(((float) this.timer / (float) this.maxTimer) * 15.0F);
        }
        return 0;
    }

    /// / TIMER ////

    public float getPercentageComplete(final float partialTick) {
        if (!isActive()) {
            return 1.0F;
        }
        return 1.0F - ((float) this.timer / (float) this.maxTimer);
    }

    public boolean isActive() {
        return this.timer > 0 && this.maxTimer > 0;
    }

    protected void updateTimer() {
        int redstone = getRedstoneStrength();
        // decrement timer
        if (--this.timer <= 0) {
            stopTimer();
            return;
        }
        if (getLevel() != null && !getLevel().isClientSide()) {
            // update redstone
            if (redstone != getRedstoneStrength()) {
                getLevel().updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            }
            // mark dirty
            setChanged();
        }
    }

    public void startTimer(final int timer) {
        this.maxTimer = timer;
        this.timer = timer;
        if (getLevel() != null && !getLevel().isClientSide()) {
            // mark dirty
            setChanged();
            // update block
            BlockState newState = getLevel().getBlockState(getBlockPos()).setValue(HourglassBlock.ACTIVE, true);
            getLevel().setBlock(getBlockPos(), newState, Block.UPDATE_ALL);
            getLevel().sendBlockUpdated(getBlockPos(), newState, newState, Block.UPDATE_ALL);
            // update redstone
            getLevel().updateNeighbourForOutputSignal(getBlockPos(), newState.getBlock());
            // play sound
            getLevel().playSound(null, getBlockPos(), SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * getLevel().getRandom().nextFloat());
            getLevel().playSound(null, getBlockPos(), SoundEvents.SAND_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * getLevel().getRandom().nextFloat());
        }
    }

    public void stopTimer() {
        this.maxTimer = 0;
        this.timer = 0;
        if (getLevel() != null && !getLevel().isClientSide()) {
            // mark dirty
            setChanged();
            // update block
            BlockState newState = getLevel().getBlockState(getBlockPos()).setValue(HourglassBlock.ACTIVE, false);
            getLevel().setBlock(getBlockPos(), newState, Block.UPDATE_ALL);
            getLevel().sendBlockUpdated(getBlockPos(), newState, newState, Block.UPDATE_ALL);
            // update redstone
            getLevel().updateNeighbourForOutputSignal(getBlockPos(), newState.getBlock());
        }
    }

    /// / NBT ////

    private static final String KEY_TIMER = "Timer";
    private static final String KEY_TIMER_MAX = "TimerMax";

    @Override
    protected void loadAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.loadAdditional(pTag, pLookup);
        this.timer = pTag.getInt(KEY_TIMER);
        this.maxTimer = pTag.getInt(KEY_TIMER_MAX);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.saveAdditional(pTag, pLookup);
        pTag.putInt(KEY_TIMER, this.timer);
        pTag.putInt(KEY_TIMER_MAX, this.maxTimer);
    }

    /// / CLIENT SERVER SYNC ////

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider pLookup) {
        final CompoundTag tag = super.getUpdateTag(pLookup);
        tag.putInt(KEY_TIMER, this.timer);
        tag.putInt(KEY_TIMER_MAX, this.maxTimer);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
