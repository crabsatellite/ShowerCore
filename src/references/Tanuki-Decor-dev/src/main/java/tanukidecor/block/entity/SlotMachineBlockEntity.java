/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tanukidecor.TanukiDecor;

public class SlotMachineBlockEntity extends BlockEntity {

    public static int USE_DURATION = 120;

    protected long startTime;
    protected boolean active;
    protected Vec3i slotRotations = new Vec3i(0, 2, 1);

    public SlotMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SlotMachineBlockEntity blockEntity) {
        // verify server side
        if (level.isClientSide()) {
            return;
        }
        if (blockEntity.isActive() && blockEntity.getStartTime() > 0) {
            int timeElapsed = (int) (level.getGameTime() - blockEntity.getStartTime());
            // play sound
            if ((timeElapsed % (2 + timeElapsed / 16)) == 0) {
                level.playSound(null, blockPos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.25F, 0.9F);
            }
            // check if block is active and time has expired
            if (timeElapsed > USE_DURATION) {
                blockEntity.stop(level);
            }
        }
    }

    public static InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity) || blockEntity.isActive()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // activate block entity
        blockEntity.start(level);
        return InteractionResult.SUCCESS;
    }

    /// / ACTIVE ////

    public boolean isActive() {
        return this.active;
    }

    public void start(final Level level) {
        this.active = true;
        if (!level.isClientSide()) {
            this.startTime = level.getGameTime();
            this.slotRotations = createSlotRotations(level.getRandom());
            setChanged();
            // update blocks
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            level.updateNeighbourForOutputSignal(getBlockPos().below(), getBlockState().getBlock());
            // play sound
            level.playSound(null, getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, 0.8F + level.getRandom().nextFloat() * 0.4F);
        }
    }

    public void stop(final Level level) {
        this.active = false;
        if (!level.isClientSide()) {
            this.startTime = 0;
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            level.updateNeighbourForOutputSignal(getBlockPos().below(), getBlockState().getBlock());
            // check for jackpot
            if (isJackpot()) {
                // play sound
                level.playSound(null, getBlockPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
            }
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public float getUsePercentage(final float partialTick) {
        if (!this.active) {
            return 1.0F;
        }
        int useTime = (int) (getLevel().getGameTime() - startTime);
        return Mth.lerp(partialTick, useTime - 1, useTime) / (float) USE_DURATION;
    }

    /// / SLOT ////

    public Vec3i createSlotRotations(final RandomSource random) {
        // customizable chance of jackpot
        final double jackboxChance = TanukiDecor.CONFIG.slotMachineJackboxChance.get();
        if ((random.nextDouble() * 100.0D) < jackboxChance) {
            int n = random.nextInt(4);
            return new Vec3i(n, n, n);
        }
        // completely random rotations
        // there is a 6.25% chance of a natural jackbox
        return new Vec3i(
                random.nextInt(4),
                random.nextInt(4),
                random.nextInt(4)
        );
    }

    public Vec3i getSlotRotations() {
        return this.slotRotations;
    }

    public void setSlotRotations(final Vec3i rotations) {
        this.slotRotations = new Vec3i(
                Mth.clamp(rotations.getX(), 0, 3),
                Mth.clamp(rotations.getY(), 0, 3),
                Mth.clamp(rotations.getZ(), 0, 3)
        );
    }

    public boolean isJackpot() {
        return slotRotations.getX() == slotRotations.getY() && slotRotations.getY() == slotRotations.getZ();
    }

    /// / NBT ////

    private static final String KEY_TIMESTAMP = "StartTime";
    private static final String KEY_SLOT_ROTATIONS = "SlotRotations";

    @Override
    protected void loadAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.loadAdditional(pTag, pLookup);
        this.startTime = pTag.getLong(KEY_TIMESTAMP);
        this.active = this.startTime > 0;
        if (pTag.contains(KEY_SLOT_ROTATIONS, Tag.TAG_COMPOUND)) {
            CompoundTag slotTag = pTag.getCompound(KEY_SLOT_ROTATIONS);
            this.slotRotations = new Vec3i(slotTag.getInt("x"), slotTag.getInt("y"), slotTag.getInt("z"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.saveAdditional(pTag, pLookup);
        pTag.putLong(KEY_TIMESTAMP, this.startTime);
        writeSlotRotations(pTag);
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider pLookup) {
        final CompoundTag tag = super.getUpdateTag(pLookup);
        tag.putLong(KEY_TIMESTAMP, this.startTime);
        writeSlotRotations(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void writeSlotRotations(final CompoundTag tag) {
        CompoundTag slotTag = new CompoundTag();
        slotTag.putInt("x", slotRotations.getX());
        slotTag.putInt("y", slotRotations.getY());
        slotTag.putInt("z", slotRotations.getZ());
        tag.put(KEY_SLOT_ROTATIONS, slotTag);
    }
}
