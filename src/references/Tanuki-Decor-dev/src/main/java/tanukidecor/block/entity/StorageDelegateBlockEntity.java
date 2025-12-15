/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tanukidecor.block.storage.IDelegateProvider;

import java.util.Optional;

public class StorageDelegateBlockEntity extends BlockEntity implements Container, MenuProvider {

    protected BlockPos delegatePos;
    protected BlockEntity delegate;

    public StorageDelegateBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        if (pBlockState.getBlock() instanceof IDelegateProvider delegateProvider) {
            this.delegatePos = delegateProvider.getDelegatePos(pBlockState, pPos);
        } else {
            this.delegatePos = null;
        }
    }

    /// / GETTERS ////

    public BlockPos getDelegatePos() {
        return delegatePos;
    }

    public @Nullable BlockEntity getDelegate() {
        if (null == delegatePos) {
            return null;
        }
        if (null == delegate && level != null) {
            this.delegate = level.getBlockEntity(delegatePos);
        }
        return this.delegate;
    }

    public Optional<BlockEntity> getOptionalDelegate() {
        return Optional.ofNullable(getDelegate());
    }

    /// / MENU PROVIDER ////

    @Override
    public Component getDisplayName() {
        final BlockEntity blockEntity = getDelegate();
        if (blockEntity instanceof MenuProvider provider) {
            return provider.getDisplayName();
        }
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        final BlockEntity blockEntity = getDelegate();
        if (blockEntity instanceof MenuProvider provider) {
            return provider.createMenu(pContainerId, pPlayerInventory, pPlayer);
        }
        return null;
    }

    /// / CONTAINER ////

    @Override
    public int getContainerSize() {
        if (getDelegate() instanceof Container container) {
            return container.getContainerSize();
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        if (getDelegate() instanceof Container container) {
            return container.isEmpty();
        }
        return false;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        if (getDelegate() instanceof Container container) {
            return container.getItem(pSlot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        if (getDelegate() instanceof Container container) {
            return container.removeItem(pSlot, pAmount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        if (getDelegate() instanceof Container container) {
            return container.removeItemNoUpdate(pSlot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        if (getDelegate() instanceof Container container) {
            container.setItem(pSlot, pStack);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (getDelegate() instanceof Container container) {
            return container.stillValid(pPlayer);
        }
        return false;
    }

    @Override
    public void clearContent() {
        if (getDelegate() instanceof Container container) {
            container.clearContent();
        }
    }
}
