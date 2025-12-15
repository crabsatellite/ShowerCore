/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class SingleSlotBlockEntity extends BlockEntity implements ContainerSingleItem, Clearable, Nameable {

    protected NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public SingleSlotBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    /// / METHODS ////

    public static ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // validate not sneaking
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        // validate side
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        // validate block entity
        if (!(level.getBlockEntity(pos) instanceof SingleSlotBlockEntity blockEntity)) {
            return ItemInteractionResult.FAIL;
        }
        ItemStack heldItem = itemStack;
        ItemStack storedItem = blockEntity.getItem(0);
        // remove item from block
        if (heldItem.isEmpty() && !storedItem.isEmpty()) {
            // remove item
            blockEntity.clearContent();
            // give to player
            if (heldItem.isEmpty()) {
                player.setItemInHand(hand, storedItem);
            } else if (!player.getInventory().add(storedItem)) {
                player.drop(storedItem, false);
            }
            // play sound
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * player.getRandom().nextFloat());
            return ItemInteractionResult.SUCCESS;
        }
        // insert item, if any
        else if (!heldItem.isEmpty() &&
                (storedItem.isEmpty() || (storedItem.getCount() < storedItem.getMaxStackSize()
                        && ItemStack.isSameItemSameComponents(heldItem, storedItem)))
                && blockEntity.canPlaceItem(0, heldItem)) {
            // split item stack
            ItemStack splitStack = heldItem.split(Math.min(storedItem.getMaxStackSize() - storedItem.getCount(), heldItem.getCount()));
            splitStack.grow(storedItem.getCount());
            // insert item
            blockEntity.setItem(0, splitStack);
            // play sound
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * player.getRandom().nextFloat());
            return ItemInteractionResult.SUCCESS;
        } else if (!heldItem.isEmpty()) {
            // play sound
            //level.playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * player.getRandom().nextFloat());
            return ItemInteractionResult.FAIL;
        }
        return ItemInteractionResult.CONSUME;
    }

    /**
     * Drops the contents of the block entity inventory
     *
     * @param blockState the block state
     * @param level      the level
     * @param pos        the block position
     * @param newState   the block state that replaced this one
     * @param isMoving   true if the block is moving
     */
    public static void onRemove(BlockState blockState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof SingleSlotBlockEntity blockEntity) {
            blockEntity.dropContents();
            level.updateNeighbourForOutputSignal(pos, blockState.getBlock());
        }
    }

    /**
     * Drops the contents of the block entity
     *
     * @see Containers#dropContents(Level, BlockPos, NonNullList)
     */
    public void dropContents() {
        if (this.level != null && !this.level.isClientSide()) {
            Containers.dropContents(this.level, this.getBlockPos(), this.inventory);
            this.setChanged();
        }
    }

    /// / GETTERS ////

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    protected IItemHandler createUnSidedHandler() {
        return new InvWrapper(this);
    }

    /// / CONTAINER ////

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.inventory.get(pSlot);
    }

    @Override
    public ItemStack getTheItem() {
        return this.getItem(0);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.getInventory(), pSlot, pAmount);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.getInventory(), pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        this.getInventory().set(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public void setTheItem(ItemStack pStack) {
        this.setItem(0, pStack);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return pPlayer.position().closerThan(Vec3.atCenterOf(this.getBlockPos()), 8.0D);
    }

    /// / CLEARABLE ////

    @Override
    public void clearContent() {
        this.inventory.clear();
        this.setChanged();
    }

    /// / NBT ////

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pLookup) {
        super.loadAdditional(pTag, pLookup);
        if (pTag.contains(getItemNbtKey(), Tag.TAG_COMPOUND)) {
            this.setItem(0, ItemStack.parseOptional(pLookup, pTag.getCompound(getItemNbtKey())));
        } else {
            this.setItem(0, ItemStack.EMPTY);
        }

    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pLookup) {
        super.saveAdditional(pTag, pLookup);
        ItemStack stack = getInventory().get(0);
        if (!stack.isEmpty()) {
            pTag.put(getItemNbtKey(), stack.save(pLookup));
        }
    }

    protected String getItemNbtKey() {
        return "Item";
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        this.loadAdditional(pkt.getTag(), lookupProvider);
    }

    /// / NAMEABLE ////

    @Override
    public Component getName() {
        return getItem(0).getHoverName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        ItemStack itemStack = getItem(0);
        if (itemStack.isEmpty() || !itemStack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)) {
            return null;
        }
        return itemStack.getHoverName();
    }
}
