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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import tanukidecor.block.misc.IDisplayProvider;

public class DisplayBlockEntity extends SingleSlotBlockEntity implements IDisplayProvider {

    private final IDisplayProvider displayProvider;

    public DisplayBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.displayProvider = (pBlockState.getBlock() instanceof IDisplayProvider p) ? p : null;
    }

    /// / DISPLAY PROVIDER ////

    @Override
    public Vector3f getDisplayRotation(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        if (this.displayProvider != null) {
            return this.displayProvider.getDisplayRotation(level, blockState, blockPos, itemStack, renderPass, partialTick);
        }
        return IDisplayProvider.super.getDisplayRotation(level, blockState, blockPos, itemStack, renderPass, partialTick);
    }

    @Override
    public Vector3f getDisplayTranslation(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        if (this.displayProvider != null) {
            return this.displayProvider.getDisplayTranslation(level, blockState, blockPos, itemStack, renderPass, partialTick);
        }
        return IDisplayProvider.super.getDisplayTranslation(level, blockState, blockPos, itemStack, renderPass, partialTick);
    }

    @Override
    public Vector3f getDisplayScale(Level level, BlockState blockState, BlockPos blockPos, ItemStack itemStack, int renderPass, float partialTick) {
        if (this.displayProvider != null) {
            return this.displayProvider.getDisplayScale(level, blockState, blockPos, itemStack, renderPass, partialTick);
        }
        return IDisplayProvider.super.getDisplayScale(level, blockState, blockPos, itemStack, renderPass, partialTick);
    }

    /// / CONTAINER ////

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {
            this.getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /// / CLIENT SERVER SYNC ////

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider pLookup) {
        // Use saveAdditional to ensure all data is included in the update tag
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, pLookup);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
