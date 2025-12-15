/**
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package tanukidecor.client.menu.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScrollButton extends Button {

    protected final ResourceLocation resourceLocation;
    protected final int iconU;
    protected final int iconV;
    protected final int iconWidth;
    protected final int iconHeight;
    protected final int iconDeltaV;

    protected final boolean isVertical;
    protected final IScrollListener listener;

    /**
     * The percent of scroll progress
     **/
    protected float scrollPercent;
    protected float scrollAmountMultiplier;
    protected boolean dragging;

    public ScrollButton(int x, int y, int width, int height, ResourceLocation resourceLocation,
                        int iconU, int iconV, int iconWidth, int iconHeight, int iconDeltaV, boolean isVertical,
                        final float scrollAmountMultiplier, final IScrollListener listener) {
        super(x, y, Math.max(1, width), Math.max(1, height), Component.empty(), b -> {
        }, Button.DEFAULT_NARRATION);
        this.resourceLocation = resourceLocation;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.iconDeltaV = iconDeltaV;
        this.isVertical = isVertical;
        this.scrollAmountMultiplier = scrollAmountMultiplier;
        this.listener = listener;
        this.scrollPercent = 0.0F;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, resourceLocation);
        // determine v coordinate
        int v = iconV;
        if (!isActive()) {
            v += iconDeltaV;
        }
        // determine icon position
        int renderX = this.getX();
        int renderY = this.getY();
        if (isVertical) {
            renderY += Mth.floor((float) (height - iconHeight) * scrollPercent);
        } else {
            renderX += Mth.floor((float) (width - iconWidth) * scrollPercent);
        }
        // draw button icon
        guiGraphics.blit(this.resourceLocation, renderX, renderY, iconU, v, iconWidth, iconHeight, 256, 256);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (isActive()) {
            this.dragging = true;
            setValueFromMouse(mouseX, mouseY);
            super.onClick(mouseX, mouseY);
        }
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (isActive()) {
            this.dragging = true;
            setValueFromMouse(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isActive()) {
            float scrollPercent = Mth.clamp(this.scrollPercent - (float) scrollY * scrollAmountMultiplier, 0.0F, 1.0F);
            setScrollPercent(scrollPercent);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean isDragging() {
        return dragging;
    }

    protected void setValueFromMouse(final double mouseX, final double mouseY) {
        float scrollPercent = (float) ((isVertical ? (mouseY - this.getY()) : (mouseX - this.getX())) / (float) height);
        this.setScrollPercent(Mth.clamp(scrollPercent, 0.0F, 1.0F));
    }

    public void setScrollPercent(final float scrollPercent) {
        this.scrollPercent = scrollPercent;
        listener.onScroll(this, scrollPercent);
    }

    public double getScrollPercent() {
        return this.scrollPercent;
    }

    public void setScrollAmountMultiplier(float scrollAmountMultiplier) {
        this.scrollAmountMultiplier = scrollAmountMultiplier;
        setScrollPercent(0);
    }

    public float getScrollAmountMultiplier() {
        return scrollAmountMultiplier;
    }

    @FunctionalInterface
    public interface IScrollListener {
        /**
         * Called when the scroll percent changes
         *
         * @param button  the scroll button
         * @param percent the updated scroll amount
         */
        void onScroll(final ScrollButton button, final float percent);
    }
}
