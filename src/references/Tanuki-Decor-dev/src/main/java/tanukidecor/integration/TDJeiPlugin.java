/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import tanukidecor.TDRegistry;
import tanukidecor.TanukiDecor;
import tanukidecor.recipe.DIYRecipe;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JeiPlugin
public class TDJeiPlugin implements IModPlugin {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "jei");

    private static final TagKey<Item> DIY_BLACKLIST_TAG_KEY = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "diy_blacklist"));

    public static Optional<HolderLookup.Provider> getClientRegistryAccess() {
        return Optional.ofNullable(Minecraft.getInstance().level).map(Level::registryAccess);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new JeiDIYRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        final Optional<HolderLookup.Provider> oRegistryAccess = getClientRegistryAccess();
        if (oRegistryAccess.isEmpty()) {
            return;
        }
        final List<DIYRecipe> recipes = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(TDRegistry.RecipeReg.DIY.get())
                .stream()
                .filter(recipeHolder -> !recipeHolder.value().getResultItem(oRegistryAccess.get()).is(DIY_BLACKLIST_TAG_KEY))
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(JeiDIYRecipeCategory.RECIPE_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TDRegistry.BlockReg.DIY_WORKBENCH.get()), JeiDIYRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        final Collection<ItemStack> blacklistItems = BuiltInRegistries.ITEM.getTag(DIY_BLACKLIST_TAG_KEY)
                .map(holders -> holders.stream()
                        .map(holder -> new ItemStack(holder.value()))
                        .collect(Collectors.toSet()))
                .orElse(java.util.Collections.emptySet());
        if (!blacklistItems.isEmpty()) {
            jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, blacklistItems);
        }
    }
}
