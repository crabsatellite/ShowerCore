/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class StorageBlockEntity extends RandomizableContainerBlockEntity {

    protected NonNullList<ItemStack> inventory;
    protected final int rows;
    protected final int slots;

    public StorageBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int rows) {
        this(pType, pPos, pBlockState, rows, rows * 9);
    }

    public StorageBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int rows, int slots) {
        super(pType, pPos, pBlockState);
        this.rows = rows;
        this.slots = slots;
        this.inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
    }

    //// METHODS ////

    /**
     * Attempts to open the storage menu, anger piglins, and play a sound
     *
     * @param blockState    the block state
     * @param level         the level
     * @param pos           the block position
     * @param player        the player
     * @param hitResult     the hit result
     * @param openMenuSound the sound to play when opening the menu
     * @return if the menu was opened
     */
    public static InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult hitResult, SoundEvent openMenuSound) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof MenuProvider blockEntity) {
            player.openMenu(blockEntity);
            PiglinAi.angerNearbyPiglins(player, true);
            level.playSound(null, pos, openMenuSound, SoundSource.BLOCKS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.4F);
        }
        return InteractionResult.CONSUME;
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
        if (level.getBlockEntity(pos) instanceof StorageBlockEntity blockEntity) {
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

    public int getRows() {
        return rows;
    }

    public int getSlots() {
        return slots;
    }

    /// / CONTAINER ////

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> pItemStacks) {
        this.inventory.clear();
        this.inventory.addAll(pItemStacks);
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return getSlots();
    }

    /// / MENU PROVIDER ////

    @Override
    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        switch (rows) {
            case 1:
                return new ChestMenu(MenuType.GENERIC_9x1, pContainerId, pInventory, this, rows);
            case 2:
                return new ChestMenu(MenuType.GENERIC_9x2, pContainerId, pInventory, this, rows);
            case 3:
                return ChestMenu.threeRows(pContainerId, pInventory, this);
            case 4:
                return new ChestMenu(MenuType.GENERIC_9x4, pContainerId, pInventory, this, rows);
            case 5:
                return new ChestMenu(MenuType.GENERIC_9x5, pContainerId, pInventory, this, rows);
            case 6:
                return ChestMenu.sixRows(pContainerId, pInventory, this);
            default:
                return null;
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.level.getBlockEntity(this.getBlockPos()) == this
                && pPlayer.position().closerThan(Vec3.atCenterOf(this.getBlockPos()), 8.0D);
    }

    /// / NBT ////

    @Override
    protected void saveAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.saveAdditional(pTag, pLookup);
        if (!this.trySaveLootTable(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.inventory, pLookup);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pLookup) {
        super.loadAdditional(pTag, pLookup);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(pTag)) {
            ContainerHelper.loadAllItems(pTag, this.inventory, pLookup);
        }
    }
}
