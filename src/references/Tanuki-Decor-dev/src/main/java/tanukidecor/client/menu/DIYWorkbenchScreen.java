/*
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 */

package tanukidecor.client.menu;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import tanukidecor.TanukiDecor;
import tanukidecor.client.ClientRecipeCollections;
import tanukidecor.client.menu.widget.DIYRecipeButton;
import tanukidecor.client.menu.widget.ScrollButton;
import tanukidecor.menu.DIYWorkbenchMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DIYWorkbenchScreen extends AbstractContainerScreen<DIYWorkbenchMenu> implements ScrollButton.IScrollListener {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TanukiDecor.MODID, "textures/gui/diy_workbench.png");

    public static final int WIDTH = 182;
    public static final int HEIGHT = 216;

    private static final int SEARCH_X = 57;
    private static final int SEARCH_Y = 35;
    private static final int SEARCH_WIDTH = 96;
    private static final int SEARCH_HEIGHT = 10;

    private static final int RECIPE_BUTTON_COUNT_Y = 4;
    private static final int RECIPE_X = 11;
    private static final int RECIPE_Y = 49;

    private static final int SCROLL_X = 159;
    private static final int SCROLL_Y = RECIPE_Y;
    private static final int SCROLL_HEIGHT = 72;

    private static final Comparator<RecipeCollection> SORT_RECIPES_BY_RESULT_NAME = Comparator.comparing(recipeCollection ->
            recipeCollection.getRecipes().isEmpty()
                    ? ""
                    : recipeCollection.getRecipes().get(0).value().getResultItem(Minecraft.getInstance().level.registryAccess()).getHoverName().getString());

    private final Collection<RecipeCollection> recipes;
    private final List<RecipeCollection> sortedRecipes;

    private final List<DIYRecipeButton> recipeButtons;
    private ScrollButton scrollButton;
    private EditBox editBox;
    private int scrollOffset;
    private Component resultCountText;

    public DIYWorkbenchScreen(DIYWorkbenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        // populate recipe collection
        this.recipes = ImmutableList.sortedCopyOf(SORT_RECIPES_BY_RESULT_NAME, ClientRecipeCollections.DIY_RECIPE_COLLECTIONS);
        // prepare recipe slice
        this.sortedRecipes = new ArrayList<>(this.recipes.size());
        // prepare button lists
        this.recipeButtons = new ArrayList<>(RECIPE_BUTTON_COUNT_Y);
        // prepare screen
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
        this.inventoryLabelX = DIYWorkbenchMenu.PLAYER_INV_X;
        this.inventoryLabelY = DIYWorkbenchMenu.PLAYER_INV_Y - 11;
        this.titleLabelY = 5;
    }

    /// / INIT ////

    @Override
    protected void init() {
        super.init();
        // scroll button
        this.scrollButton = this.addRenderableWidget(new ScrollButton(this.leftPos + SCROLL_X, this.topPos + SCROLL_Y, 12, SCROLL_HEIGHT,
                TEXTURE, 244, 0, 12, 15, 15, true, 1.0F, this));
        this.scrollOffset = 0;
        // edit box
        this.editBox = this.addRenderableWidget(new EditBox(this.font, this.leftPos + SEARCH_X, this.topPos + SEARCH_Y, SEARCH_WIDTH, SEARCH_HEIGHT, Component.translatable("container.search")));
        this.editBox.setCanLoseFocus(false);
        this.editBox.setTextColor(-1);
        this.editBox.setTextColorUneditable(-1);
        this.editBox.setBordered(false);
        this.editBox.setMaxLength(70);
        this.editBox.setValue("");
        this.editBox.setResponder(s -> updateRecipes(s));
        this.setInitialFocus(this.editBox);
        // recipe buttons
        this.recipeButtons.clear();
        final Button.OnPress recipeButtonOnPress = b -> getMenu().selectRecipe(((DIYRecipeButton) b).getRecipeHolder());
        final ItemRenderer itemRenderer = this.getMinecraft().getItemRenderer();
        for (int i = 0, x = this.leftPos + RECIPE_X, y = this.topPos + RECIPE_Y; i < RECIPE_BUTTON_COUNT_Y; i++) {
            this.recipeButtons.add(this.addRenderableWidget(new DIYRecipeButton(x, y + i * DIYRecipeButton.HEIGHT, itemRenderer, this.font, recipeButtonOnPress)));
        }
        updateRecipes("");
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        String s = this.editBox.getValue();
        super.resize(pMinecraft, pWidth, pHeight);
        this.editBox.setValue(s);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            this.getMenu().getInventory().player.closeContainer();
        }
        return this.editBox.keyPressed(pKeyCode, pScanCode, pModifiers) || this.editBox.canConsumeInput() || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    /// / RENDER ////

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableDepthTest();
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // render background
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // render labels and widgets
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // render result count text
        guiGraphics.drawString(this.font, resultCountText.getVisualOrderText(), this.leftPos + SEARCH_X + SEARCH_WIDTH + 4, this.topPos + SEARCH_Y + (SEARCH_HEIGHT - font.lineHeight) / 2, 0x404040, false);
        // render tooltips
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /// / RECIPES ////

    private void updateRecipes(final String filter) {
        this.sortedRecipes.clear();
        if (!(TanukiDecor.CONFIG.isDIYWorkbenchEnabled.get())) {
            return;
        }
        if (filter.isEmpty()) {
            this.sortedRecipes.addAll(this.recipes);
        } else {
            this.sortedRecipes.addAll(ClientRecipeCollections.searchRecipes(filter.toLowerCase(Locale.ROOT)));
        }
        // update scroll bar
        this.scrollButton.setScrollAmountMultiplier(1.0F / Math.max(1, sortedRecipes.size() - RECIPE_BUTTON_COUNT_Y));
        this.scrollButton.active = this.sortedRecipes.size() > RECIPE_BUTTON_COUNT_Y;
        // update count text
        this.resultCountText = Component.literal("" + sortedRecipes.size());
        // update recipe buttons
        this.updateRecipeButtons();
    }

    private void updateRecipeButtons() {
        for (int i = 0, n = recipeButtons.size(); i < n; i++) {
            DIYRecipeButton button = recipeButtons.get(i);
            int index = i + scrollOffset;
            if (index < 0 || index >= sortedRecipes.size() || sortedRecipes.get(index).getRecipes().isEmpty()) {
                button.visible = button.active = false;
                continue;
            }
            button.visible = button.active = true;
            button.setRecipeHolder(sortedRecipes.get(index).getRecipes().get(0));
        }
    }

    /// / SCROLL LISTENER ////

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (isHovering(RECIPE_X, RECIPE_Y, DIYRecipeButton.WIDTH, DIYRecipeButton.HEIGHT * RECIPE_BUTTON_COUNT_Y, pMouseX, pMouseY)) {
            return scrollButton.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && scrollButton != null && scrollButton.isDragging()) {
            scrollButton.onDrag(mouseX, mouseY, dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void onScroll(ScrollButton button, float percent) {
        final int oldScrollOffset = this.scrollOffset;
        this.scrollOffset = Mth.floor(Math.max(0, percent * Math.max(0, sortedRecipes.size() - RECIPE_BUTTON_COUNT_Y)));
        if (oldScrollOffset != scrollOffset) {
            updateRecipeButtons();
        }
    }

    /// / TOOLTIP ////

    public static Tooltip createTooltip(final List<Component> list) {
        MutableComponent component = Component.empty();
        // add each component, separated by a newline
        for (Component c : list) {
            component.getSiblings().add(c);
            component.getSiblings().add(Component.literal("\n"));
        }
        // remove trailing newline
        if (component.getSiblings().size() > 1) {
            component.getSiblings().remove(component.getSiblings().size() - 1);
        }
        // create the tooltip
        return Tooltip.create(component);
    }
}
