package mod.crabmod.showercore.client.gui.widget;

import mod.crabmod.showercore.client.event.CreativeFilterScreenEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

public class CreativeFilterTagButton extends Button {
  private static final int SELECTED_WIDTH = 32;
  private static final int UNSELECTED_WIDTH = 28;
  private static final int TAB_HEIGHT = 28;
  private static final int OUTLINE = 0xFF000000;
  private static final int HIGHLIGHT = 0xFFFFFFFF;
  private static final int SHADOW = 0xFF555555;
  private static final int SELECTED_BACKGROUND = 0xFFC6C6C6;
  private static final int UNSELECTED_BACKGROUND = 0xFF8A8A8A;

  private final CreativeFilterScreenEvents.TagFilter filter;
  private final ItemStack icon;
  private boolean toggled;

  public CreativeFilterTagButton(
      int x, int y, CreativeFilterScreenEvents.TagFilter filter, OnPress onPress) {
    super(x, y, SELECTED_WIDTH, TAB_HEIGHT, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
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
    int tabWidth = this.toggled ? SELECTED_WIDTH : UNSELECTED_WIDTH;
    int background = this.toggled ? SELECTED_BACKGROUND : UNSELECTED_BACKGROUND;
    int x = this.getX();
    int y = this.getY();

    graphics.fill(x, y, x + tabWidth, y + TAB_HEIGHT, OUTLINE);
    graphics.fill(x + 1, y + 1, x + tabWidth - 1, y + TAB_HEIGHT - 1, background);
    graphics.fill(x + 2, y + 2, x + tabWidth - 2, y + 3, HIGHLIGHT);
    graphics.fill(x + 2, y + 2, x + 3, y + TAB_HEIGHT - 2, HIGHLIGHT);
    graphics.fill(x + 3, y + TAB_HEIGHT - 2, x + tabWidth - 1, y + TAB_HEIGHT - 1, SHADOW);
    if (this.toggled) {
      graphics.fill(x + tabWidth - 1, y + 1, x + tabWidth, y + TAB_HEIGHT - 1, background);
    }
    graphics.renderItem(this.icon, x + 8, y + 6);
  }

  public void updateState() {
    this.toggled = this.filter.isEnabled();
  }
}
