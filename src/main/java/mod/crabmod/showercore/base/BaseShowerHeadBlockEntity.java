package mod.crabmod.showercore.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseShowerHeadBlockEntity extends BaseContainerBlockEntity {

  // Holds items for the container
  protected NonNullList<ItemStack> items;

  public BaseShowerHeadBlockEntity(
          BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    // Creates a container with a single slot
    this.items = NonNullList.withSize(1, ItemStack.EMPTY);
  }

  @Override
  protected Component getDefaultName() {
    // Sets a custom name for the container
    return Component.translatable("container.single_slot");
  }

  @Override
  protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
    // Opens the container with a custom single-slot menu
    this.startOpen(playerInventory.player);
    return new SingleSlotMenu(id, playerInventory, this);
  }

  @Override
  public int getContainerSize() {
    // Sets the number of slots to 1
    return 1;
  }

  @Override
  public boolean isEmpty() {
    // Checks if the single slot is empty
    return items.get(0).isEmpty();
  }

  @Override
  public ItemStack getItem(int index) {
    // Returns the item in the single slot
    return items.get(0);
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    // Removes a specified number of items from the slot
    return ContainerHelper.removeItem(items, 0, count);
  }

  @Override
  public ItemStack removeItemNoUpdate(int index) {
    // Removes the item without updating the slot
    return ContainerHelper.takeItem(items, 0);
  }

  @Override
  public void setItem(int index, ItemStack stack) {
    // Sets the item in the single slot
    items.set(0, stack);
    // Checks if item stack size exceeds max stack size and adjusts if needed
    if (stack.getCount() > getMaxStackSize()) {
      stack.setCount(getMaxStackSize());
    }
  }

  @Override
  public boolean stillValid(Player player) {
    // Checks if the player is still close enough to interact with the container
    return this.level != null
            && this.level.getBlockEntity(this.worldPosition) == this
            && player.distanceToSqr(
            (double) this.worldPosition.getX() + 0.5D,
            (double) this.worldPosition.getY() + 0.5D,
            (double) this.worldPosition.getZ() + 0.5D)
            <= 64.0D;
  }

  @Override
  public void clearContent() {
    // Clears the content of the slot
    items.clear();
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
      return this.items;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> items) {
      this.items = items;
  }

  @Override
  public void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    ContainerHelper.loadAllItems(tag, this.items, registries);
  }

  @Override
  protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    ContainerHelper.saveAllItems(tag, this.items, registries);
  }

  // Custom menu for a single-slot container
  private static class SingleSlotMenu extends AbstractContainerMenu {
    private final BaseShowerHeadBlockEntity blockEntity;

    public SingleSlotMenu(
            int id, Inventory playerInventory, BaseShowerHeadBlockEntity blockEntity) {
      super(MenuType.GENERIC_9x1, id);
      this.blockEntity = blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
      // Validates if the player can still interact with this container
      return this.blockEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
      // Handles item stack movement between inventory and the single slot
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);

      if (slot != null && slot.hasItem()) {
        ItemStack itemstack1 = slot.getItem();
        itemstack = itemstack1.copy();

        // Move from the container slot to the player's inventory
        if (index == 0) {
          if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
            return ItemStack.EMPTY;
          }
          // Move from the player's inventory to the container slot
        } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
          return ItemStack.EMPTY;
        }

        if (itemstack1.isEmpty()) {
          slot.set(ItemStack.EMPTY);
        } else {
          slot.setChanged();
        }
      }

      return itemstack;
    }
  }
}
