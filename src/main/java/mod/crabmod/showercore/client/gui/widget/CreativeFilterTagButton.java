package mod.crabmod.showercore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crabmod.showercore.client.event.CreativeFilterScreenEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CreativeFilterTagButton extends Button {
  private static final ResourceLocation SELECTED_SPRITE =
      ResourceLocation.fromNamespaceAndPath(
          "minecraft", "container/creative_inventory/tab_top_selected_2");
  private static final ResourceLocation UNSELECTED_SPRITE =
      ResourceLocation.fromNamespaceAndPath(
          "minecraft", "container/creative_inventory/tab_top_unselected_2");

  private final CreativeFilterScreenEvents.TagFilter filter;
  private final ItemStack icon;
  private boolean toggled;

  public CreativeFilterTagButton(
      int x, int y, CreativeFilterScreenEvents.TagFilter filter, OnPress onPress) {
    super(x, y, 32, 26, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
    this.filter = filter;
    this.icon = filter.getIcon();
    this.toggled = filter.isEnabled();
    this.setTooltip(Tooltip.create(filter.getName()));
  }

  @Override
  public void onPress() {
    this.toggled = !this.toggled;
    this.filter.setEnabled(this.toggled);
    super.onPress();
  }

  @Override
  public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    RenderSystem.enableBlend();
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
    ResourceLocation sprite = this.toggled ? SELECTED_SPRITE : UNSELECTED_SPRITE;
    graphics.blitSprite(sprite, this.getX(), this.getY(), 26, this.toggled ? 32 : 28);
    graphics.renderItem(this.icon, this.getX() + 8, this.getY() + 5);
    RenderSystem.disableBlend();
  }

  public void updateState() {
    this.toggled = this.filter.isEnabled();
  }
}
