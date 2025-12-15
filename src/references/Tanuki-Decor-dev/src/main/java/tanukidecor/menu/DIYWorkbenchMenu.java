/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import tanukidecor.TDRegistry;
import tanukidecor.block.entity.DIYWorkbenchBlockEntity;
import tanukidecor.network.ServerBoundSelectDIYRecipePacket;
import tanukidecor.recipe.DIYRecipe;


public class DIYWorkbenchMenu extends AbstractContainerMenu {

    public static final int PLAYER_INV_X = 11;
    public static final int PLAYER_INV_Y = 134;

    private static final int CONTAINER_SLOTS = 4;
    private static final int RESULT_SLOTS = 1;

    private final BlockPos blockPos;
    private final Inventory inventory;
    private final HolderLookup.Provider registryAccess;
    private final Container container;
    private final ResultContainer resultContainer;

    private int maxCraftCount;

    public DIYWorkbenchMenu(int pContainerId, Inventory inventory, BlockPos blockPos, Container container) {
        super(TDRegistry.MenuReg.DIY_WORKBENCH.get(), pContainerId);
        this.inventory = inventory;
        this.blockPos = blockPos;
        this.registryAccess = inventory.player.level().registryAccess();
        this.container = container;
        this.resultContainer = new ResultContainer();
        checkContainerSize(this.container, CONTAINER_SLOTS);

        // slots
        for (int i = 0; i < 4; i++) {
            this.addSlot(new IngredientSlot(this.container, i, PLAYER_INV_X + i * 33, 15, DIYWorkbenchBlockEntity.INGREDIENTS[i]));
        }
        this.addSlot(new ResultSlot(this.inventory.player, this.resultContainer, 0, 155, 15));
        addPlayerSlots(PLAYER_INV_X, PLAYER_INV_Y);
    }

    /// / MENU ////

    public Inventory getInventory() {
        return inventory;
    }

    public int getMaxCraftCount() {
        return this.maxCraftCount;
    }

    public HolderLookup.Provider getRegistryAccess() {
        return this.registryAccess;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack = slot.getItem();
            copy = itemstack.copy();
            if (index < container.getContainerSize() + resultContainer.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack, resultContainer.getContainerSize() + container.getContainerSize(), this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
                if (this.maxCraftCount > 0 && index == resultContainer.getContainerSize() + container.getContainerSize() - 1) {
                    onTakeResult(player, copy, 1);
                    slotsChanged(this.resultContainer);
                }
            } else if (!this.moveItemStackTo(itemstack, 0, container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return copy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return container.stillValid(pPlayer);
    }

    @Override
    public void slotsChanged(Container pContainer) {
        super.slotsChanged(pContainer);
        if (pContainer == this.container) {
            updateMaxCraftCount();
            if (this.maxCraftCount <= 0) {
                resultContainer.clearContent();
                slotsChanged(this.resultContainer);
            }
        }
        if (pContainer == this.resultContainer) {
            if (this.maxCraftCount > 0 && this.resultContainer.isEmpty()) {
                RecipeHolder<?> recipeHolder = this.resultContainer.getRecipeUsed();
                if (recipeHolder != null && recipeHolder.value() instanceof DIYRecipe recipe) {
                    this.resultContainer.setItem(0, recipe.assemble(DIYWorkbenchBlockEntity.asRecipeInput(this.container), this.registryAccess));
                    slotsChanged(this.resultContainer);
                }
            }
        }
    }

    protected void updateMaxCraftCount() {
        this.maxCraftCount = 127;
        // init at max stack size
        RecipeHolder<?> recipeHolder = this.resultContainer.getRecipeUsed();
        if (recipeHolder != null) {
            this.maxCraftCount = recipeHolder.value().getResultItem(this.registryAccess).getMaxStackSize();
        }
        // reduce to min stack size of any input
        for (int i = 0; i < CONTAINER_SLOTS; i++) {
            ItemStack item = container.getItem(i);
            this.maxCraftCount = Math.min(this.maxCraftCount, item.getCount());
        }
    }

    /// / RECIPE ////

    public void selectRecipe(final RecipeHolder<?> recipeHolder) {
        this.resultContainer.setRecipeUsed(recipeHolder);
        // send packet from client to server
        if (inventory.player.level().isClientSide() && recipeHolder != null) {
            PacketDistributor.sendToServer(new ServerBoundSelectDIYRecipePacket(recipeHolder.id()));
        }
    }

    public void setRecipe(final DIYRecipe recipe) {
        // update recipe - wrap in RecipeHolder
        RecipeHolder<DIYRecipe> recipeHolder = recipe != null ?
                new RecipeHolder<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tanukidecor", "diy_temp"), recipe) : null;
        this.resultContainer.setRecipeUsed(recipeHolder);
        if (null == recipe) {
            this.resultContainer.clearContent();
            this.slotsChanged(this.resultContainer);
            return;
        }
        // update max craft count
        this.updateMaxCraftCount();
        // update result item
        if (maxCraftCount > 0) {
            this.resultContainer.setItem(0, recipe.assemble(DIYWorkbenchBlockEntity.asRecipeInput(this.container), this.registryAccess));
        } else {
            this.resultContainer.clearContent();
        }
        this.slotsChanged(this.resultContainer);
    }

    /// / CONTAINER LISTENER ////

    private void onTakeResult(Player player, ItemStack result, int count) {
        //if(this.inventory.player.level.isClientSide()) {
        //return;
        //}
        // remove items
        for (int i = 0; i < CONTAINER_SLOTS; i++) {
            this.container.removeItem(i, count);
        }
        // crafting trigger
        result.onCraftedBy(player.level(), player, count);
        slotsChanged(this.container);
        slotsChanged(this.resultContainer);
        // play sound
        player.level().playSound(player, blockPos, SoundEvents.VILLAGER_WORK_MASON, SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * player.getRandom().nextFloat());
        player.level().playSound(player, blockPos, SoundEvents.VILLAGER_WORK_TOOLSMITH, SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * player.getRandom().nextFloat());
    }

    /// / PLAYER INVENTORY ////

    private void addPlayerSlots(final int x, final int y) {
        // inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        // hotbar
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(this.inventory, k, x + k * 18, y + 58));
        }
    }

    /// / SLOT ////

    private class IngredientSlot extends Slot {

        private final Ingredient ingredient;

        public IngredientSlot(Container pContainer, int pSlot, int pX, int pY, Ingredient ingredient) {
            super(pContainer, pSlot, pX, pY);
            this.ingredient = ingredient;
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return ingredient.test(pStack);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            DIYWorkbenchMenu.this.slotsChanged(IngredientSlot.this.container);
        }
    }

    private class ResultSlot extends Slot {

        private final Player player;

        public ResultSlot(Player player, Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
            this.player = player;
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }

        @Override
        protected void onQuickCraft(ItemStack pStack, int pAmount) {
            DIYWorkbenchMenu.this.onTakeResult(ResultSlot.this.player, pStack, pAmount);
        }

        @Override
        protected void onSwapCraft(int pNumItemsCrafted) {
            DIYWorkbenchMenu.this.onTakeResult(ResultSlot.this.player, this.getItem(), pNumItemsCrafted);
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            super.onTake(pPlayer, pStack);
            DIYWorkbenchMenu.this.onTakeResult(pPlayer, pStack, 1);
        }
    }
}
