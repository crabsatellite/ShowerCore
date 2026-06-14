package mod.crabmod.showercore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crabmod.showercore.client.event.CreativeFilterScreenEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

public class CreativeFilterTagButton extends Button {
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
    int border = this.toggled ? 0xFFFFFFFF : 0xFF555555;
    int background = this.toggled ? 0xFFD8D8D8 : 0xFF8A8A8A;
    graphics.fill(this.getX(), this.getY(), this.getX() + 26, this.getY() + 26, border);
    graphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + 25, this.getY() + 25, background);
    graphics.renderItem(this.icon, this.getX() + 8, this.getY() + 5);
    RenderSystem.disableBlend();
  }

  public void updateState() {
    this.toggled = this.filter.isEnabled();
  }
}
