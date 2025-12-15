/*
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import tanukidecor.TDRegistry;

import java.util.ArrayList;
import java.util.List;

public final class ClientRecipeCollections {

    public static final List<RecipeCollection> DIY_RECIPE_COLLECTIONS = new ArrayList<>();
    private static SearchTree<RecipeCollection> searchTree;

    private ClientRecipeCollections() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ClientRecipeCollections::onUpdateRecipes);
        NeoForge.EVENT_BUS.addListener(ClientRecipeCollections::onUpdateTags);
    }

    public static void registerSearchTrees() {
        // create a search tree (copied from Minecraft#createSearchTrees)
        searchTree = new FullTextSearchTree<>(
                (recipeCollection) -> recipeCollection.getRecipes().stream()
                        .flatMap((recipe) -> recipe.value().getResultItem(recipeCollection.registryAccess())
                                .getTooltipLines(Item.TooltipContext.EMPTY, null, TooltipFlag.Default.NORMAL).stream())
                        .map((component) -> ChatFormatting.stripFormatting(component.getString()).trim())
                        .filter((s) -> !s.isEmpty()),
                (collection) -> collection.getRecipes().stream()
                        .map((recipe) -> BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(collection.registryAccess()).getItem())),
                DIY_RECIPE_COLLECTIONS);
    }

    public static List<RecipeCollection> searchRecipes(String query) {
        return searchTree != null ? searchTree.search(query) : DIY_RECIPE_COLLECTIONS;
    }

    /**
     * Populates the recipe cache and search tree with {@link RecipeCollection}s that contain
     * one {@link tanukidecor.recipe.DIYRecipe} each
     *
     * @param event the recipe update event
     */
    private static void onUpdateRecipes(final RecipesUpdatedEvent event) {
        // clear old recipes
        DIY_RECIPE_COLLECTIONS.clear();
        // add updated recipes to collections, one recipe per RecipeCollection
        final RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        var diyRecipes = event.getRecipeManager().getAllRecipesFor(TDRegistry.RecipeReg.DIY.get());
        diyRecipes.stream().map(recipe -> new RecipeCollection(registryAccess, ImmutableList.of(recipe))).forEach(DIY_RECIPE_COLLECTIONS::add);
        // refresh search tree
        registerSearchTrees();
    }

    /**
     * Filters the recipe cache and search tree to remove any blacklisted items
     *
     * @param event the tag update event
     */
    private static void onUpdateTags(final TagsUpdatedEvent event) {
        // clear old recipes
        final List<RecipeCollection> recipes = ImmutableList.copyOf(DIY_RECIPE_COLLECTIONS);
        DIY_RECIPE_COLLECTIONS.clear();
        // add all recipes that are not blacklisted back into the list and search tree
        for (RecipeCollection recipeCollection : recipes) {
            if (recipeCollection.getRecipes().size() == 1 && !recipeCollection.getRecipes().get(0).value().getResultItem(event.getRegistryAccess()).is(TDRegistry.DIY_BLACKLIST_TAG_KEY)) {
                DIY_RECIPE_COLLECTIONS.add(recipeCollection);
            }
        }
        // refresh search tree
        registerSearchTrees();
    }
}
