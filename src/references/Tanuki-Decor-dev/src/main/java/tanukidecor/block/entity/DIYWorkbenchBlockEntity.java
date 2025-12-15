/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tanukidecor.menu.DIYWorkbenchMenu;

public class DIYWorkbenchBlockEntity extends StorageBlockEntity {

    public static final Ingredient[] INGREDIENTS = new Ingredient[]{
            Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS),
            Ingredient.of(ItemTags.LOGS_THAT_BURN),
            Ingredient.of(Items.CLAY_BALL),
            Ingredient.of(Items.IRON_INGOT)
    };

    public DIYWorkbenchBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState, 1, 4);
    }

    /**
     * Helper method to convert Container to RecipeInput
     */
    public static RecipeInput asRecipeInput(Container container) {
        return new RecipeInput() {
            @Override
            public ItemStack getItem(int slot) {
                return container.getItem(slot);
            }

            @Override
            public int size() {
                return container.getContainerSize();
            }
        };
    }

    /// / MENU PROVIDER ////

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new DIYWorkbenchMenu(pContainerId, pInventory, getBlockPos(), this);
    }

    /// / CONTAINER ////


    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return INGREDIENTS[Mth.clamp(pIndex, 0, INGREDIENTS.length - 1)].test(pStack);
    }
}
