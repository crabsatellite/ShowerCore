/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tanukidecor.block.RotatingBlock;

public class GlobeBlockEntity extends BlockEntity {

    public static int USE_DURATION = 60;

    protected long startTime;
    protected boolean active;
    protected Direction targetDirection;

    public GlobeBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.targetDirection = pBlockState.getValue(RotatingBlock.FACING);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, GlobeBlockEntity blockEntity) {
        // verify server side
        if (level.isClientSide()) {
            return;
        }
        if (blockEntity.isActive() && blockEntity.getStartTime() > 0) {
            int timeElapsed = (int) (level.getGameTime() - blockEntity.getStartTime());
            // check if block is active and time has expired
            if (timeElapsed > 20) {
                blockEntity.stop(level);
            }
        }
    }

    public static ItemInteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof GlobeBlockEntity blockEntity) || blockEntity.isActive()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        // activate block entity
        blockEntity.start(level);
        return ItemInteractionResult.SUCCESS;
    }

    /// / ACTIVE ////

    public boolean isActive() {
        return this.active;
    }

    public void start(final Level level) {
        this.active = true;
        if (!level.isClientSide()) {
            this.startTime = level.getGameTime();
            this.targetDirection = Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom());
            setChanged();
            // update blocks
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            // play sound
            level.playSound(null, getBlockPos(), SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.5F, 0.8F + level.getRandom().nextFloat() * 0.4F);
        }
    }

    public void stop(final Level level) {
        this.active = false;
        if (!level.isClientSide()) {
            this.startTime = 0;
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public Direction getTargetDirection() {
        return targetDirection;
    }

    public float getUsePercentage(final float partialTick) {
        if (!this.active) {
            return 1.0F;
        }
        int useTime = (int) (getLevel().getGameTime() - startTime);
        return Mth.lerp(partialTick, useTime - 1, useTime) / (float) 20;
    }

    /// / NBT ////

    private static final String KEY_TIMESTAMP = "StartTime";
    private static final String KEY_TARGET_DIRECTION = "Direction";

    @Override
    protected void loadAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.loadAdditional(pTag, pLookup);
        this.startTime = pTag.getLong(KEY_TIMESTAMP);
        this.active = this.startTime > 0;
        this.targetDirection = Direction.byName(pTag.getString(KEY_TARGET_DIRECTION));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.saveAdditional(pTag, pLookup);
        pTag.putLong(KEY_TIMESTAMP, this.startTime);
        pTag.putString(KEY_TARGET_DIRECTION, this.targetDirection.getSerializedName());
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider pLookup) {
        final CompoundTag tag = super.getUpdateTag(pLookup);
        tag.putLong(KEY_TIMESTAMP, this.startTime);
        tag.putString(KEY_TARGET_DIRECTION, this.targetDirection.getSerializedName());
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
