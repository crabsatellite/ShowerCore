package mod.crabmod.showercore.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class CreativeFilterIconButton extends Button {
  private static final int ICON_COLOR = 0xFFFFFFFF;
  private static final int DISABLED_ICON_COLOR = 0xFF808080;

  private final Icon icon;

  public CreativeFilterIconButton(
      int x, int y, Icon icon, Component tooltip, OnPress onPress) {
    super(x, y, 20, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
    this.icon = icon;
    this.setTooltip(Tooltip.create(tooltip));
  }

  @Override
  public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    int color = this.active ? ICON_COLOR : DISABLED_ICON_COLOR;
    int x = this.getX();
    int y = this.getY();
    switch (this.icon) {
      case SCROLL_UP -> drawChevronUp(graphics, x + 5, y + 6, color);
      case SCROLL_DOWN -> drawChevronDown(graphics, x + 5, y + 7, color);
      case ENABLE_ALL -> drawPlus(graphics, x + 6, y + 6, color);
      case DISABLE_ALL -> drawMinus(graphics, x + 6, y + 9, color);
    }
  }

  private static void drawChevronUp(GuiGraphics graphics, int x, int y, int color) {
    graphics.fill(x + 4, y, x + 6, y + 2, color);
    graphics.fill(x + 3, y + 2, x + 7, y + 4, color);
    graphics.fill(x + 2, y + 4, x + 4, y + 6, color);
    graphics.fill(x + 6, y + 4, x + 8, y + 6, color);
  }

  private static void drawChevronDown(GuiGraphics graphics, int x, int y, int color) {
    graphics.fill(x + 2, y, x + 4, y + 2, color);
    graphics.fill(x + 6, y, x + 8, y + 2, color);
    graphics.fill(x + 3, y + 2, x + 7, y + 4, color);
    graphics.fill(x + 4, y + 4, x + 6, y + 6, color);
  }

  private static void drawPlus(GuiGraphics graphics, int x, int y, int color) {
    graphics.fill(x + 3, y, x + 5, y + 8, color);
    graphics.fill(x, y + 3, x + 8, y + 5, color);
  }

  private static void drawMinus(GuiGraphics graphics, int x, int y, int color) {
    graphics.fill(x, y, x + 8, y + 2, color);
  }

  public enum Icon {
    SCROLL_UP,
    SCROLL_DOWN,
    ENABLE_ALL,
    DISABLE_ALL
  }
}
