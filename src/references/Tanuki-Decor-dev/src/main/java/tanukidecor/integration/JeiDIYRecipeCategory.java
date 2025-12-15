/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.integration;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import tanukidecor.TDRegistry;
import tanukidecor.TanukiDecor;
import tanukidecor.block.entity.DIYWorkbenchBlockEntity;
import tanukidecor.recipe.DIYRecipe;

import java.util.List;
import java.util.Optional;

public class JeiDIYRecipeCategory implements IRecipeCategory<DIYRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "diy");
    public static final RecipeType<DIYRecipe> RECIPE_TYPE = new RecipeType<>(UID, DIYRecipe.class);

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "textures/jei/diy_recipe.png");
    private static final int PADDING = 10;
    private static final int INPUT_SPACING = 33;
    private static final int SLOT_SIZE = 18;
    private static final int ARROW_HEIGHT = 26;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public JeiDIYRecipeCategory(final IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(TEXTURE, 0, 0, 117, 62)
                .addPadding(PADDING, PADDING, PADDING, PADDING)
                .setTextureSize(128, 64)
                .build();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(TDRegistry.BlockReg.DIY_WORKBENCH.get()));
        this.title = Component.translatable(Util.makeDescriptionId("jei.recipe.category", UID));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DIYRecipe recipe, IFocusGroup focuses) {
        final Optional<HolderLookup.Provider> oRegistryAccess = TDJeiPlugin.getClientRegistryAccess();
        if (oRegistryAccess.isEmpty()) {
            return;
        }
        // input
        int x = PADDING + 1;
        int y = PADDING + 1;
        for (int i = 0; i < 4; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, x + INPUT_SPACING * i, y)
                    .setSlotName("input" + i)
                    .addItemStacks(List.of(DIYWorkbenchBlockEntity.INGREDIENTS[i].getItems()));
        }
        // output
        x += (this.background.getWidth() - SLOT_SIZE - PADDING * 2) / 2;
        y += SLOT_SIZE + ARROW_HEIGHT;
        builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                .setSlotName("output")
                .addItemStack(recipe.getResultItem(oRegistryAccess.get()));
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public void draw(DIYRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public RecipeType<DIYRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
}
