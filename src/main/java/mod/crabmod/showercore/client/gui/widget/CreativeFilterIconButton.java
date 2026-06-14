package mod.crabmod.showercore.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class CreativeFilterIconButton extends Button {
  private static final int ICON_COLOR = 0xFFFFFFFF;
  private static final int GRID_COLOR = 0xFFE8E8E8;
  private static final int ENABLE_MARK_COLOR = 0xFF55FF55;
  private static final int DISABLE_MARK_COLOR = 0xFFFF5555;
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
      case ENABLE_ALL ->
          drawAllCategories(
              graphics, x + 4, y + 5, this.active ? GRID_COLOR : color, this.active ? ENABLE_MARK_COLOR : color);
      case DISABLE_ALL ->
          drawNoCategories(
              graphics, x + 4, y + 5, this.active ? GRID_COLOR : color, this.active ? DISABLE_MARK_COLOR : color);
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

  private static void drawAllCategories(GuiGraphics graphics, int x, int y, int gridColor, int markColor) {
    drawCategoryGrid(graphics, x, y, gridColor);
    drawCheckMark(graphics, x + 7, y + 6, markColor);
  }

  private static void drawNoCategories(GuiGraphics graphics, int x, int y, int gridColor, int markColor) {
    drawCategoryGrid(graphics, x, y, gridColor);
    drawXMark(graphics, x + 7, y + 3, markColor);
  }

  private static void drawCategoryGrid(GuiGraphics graphics, int x, int y, int color) {
    graphics.fill(x, y, x + 4, y + 4, color);
    graphics.fill(x + 5, y, x + 9, y + 4, color);
    graphics.fill(x, y + 5, x + 4, y + 9, color);
    graphics.fill(x + 5, y + 5, x + 9, y + 9, color);
  }

  private static void drawCheckMark(GuiGraphics graphics, int x, int y, int color) {
    graphics.fill(x, y + 4, x + 2, y + 6, color);
    graphics.fill(x + 2, y + 6, x + 4, y + 8, color);
    graphics.fill(x + 4, y + 4, x + 6, y + 6, color);
    graphics.fill(x + 6, y + 2, x + 8, y + 4, color);
    graphics.fill(x + 8, y, x + 10, y + 2, color);
  }

  private static void drawXMark(GuiGraphics graphics, int x, int y, int color) {
    for (int offset = 0; offset < 8; offset += 2) {
      graphics.fill(x + offset, y + offset, x + offset + 2, y + offset + 2, color);
      graphics.fill(x + 6 - offset, y + offset, x + 8 - offset, y + offset + 2, color);
    }
  }

  public enum Icon {
    SCROLL_UP,
    SCROLL_DOWN,
    ENABLE_ALL,
    DISABLE_ALL
  }
}
