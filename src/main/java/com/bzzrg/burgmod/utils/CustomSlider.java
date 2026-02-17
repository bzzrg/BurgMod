package com.bzzrg.burgmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiSlider;

public class CustomSlider extends GuiSlider {

    public CustomSlider(int id, int x, int y, int width, int height,
                        String prefix, String suffix,
                        double min, double max, double current,
                        boolean showDecimal, boolean drawString,
                        ISlider handler) {
        super(id, x, y, width, height, prefix, suffix, min, max, current, showDecimal, drawString, handler);
    }
    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float)(par2 - (this.xPosition + 4)) / (float)(this.width - 8);
                this.updateSlider();
            }
        }

    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) return;

        // IMPORTANT: this is what makes dragging update like normal GuiSlider
        this.mouseDragged(mc, mouseX, mouseY);

        int baseColor = 0x00000000;
        int hoverColor = 0x32FFFFFF;
        int borderColor = 0xFFFFFFFF;
        int borderThickness = 1;

        this.hovered = mouseX >= xPosition && mouseY >= yPosition
                && mouseX < xPosition + width && mouseY < yPosition + height;

        int bg = this.hovered ? hoverColor : baseColor;

        // Background
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, bg);

        // Border
        drawRect(xPosition, yPosition, xPosition + width, yPosition + borderThickness, borderColor);
        drawRect(xPosition, yPosition + height - borderThickness, xPosition + width, yPosition + height, borderColor);
        drawRect(xPosition, yPosition, xPosition + borderThickness, yPosition + height, borderColor);
        drawRect(xPosition + width - borderThickness, yPosition, xPosition + width, yPosition + height, borderColor);

        // Knob position (use sliderValue maintained by GuiSlider)
        int pad = 4;
        int left = xPosition + pad;
        int right = xPosition + width - pad;

        int knobX = (int) (left + (right - left) * this.sliderValue);

        // Knob (transparent center, full outline)
        int knobW = 8;
        int k1 = knobX - knobW / 2;
        int k2 = knobX + knobW / 2;

        int knobTop = yPosition + 2;
        int knobBot = yPosition + height - 2;

        int knobBorder = 0xFFFFFFFF;

        drawRect(k1, knobTop, k2, knobTop + 1, knobBorder);
        drawRect(k1, knobBot - 1, k2, knobBot, knobBorder);
        drawRect(k1, knobTop, k1 + 1, knobBot, knobBorder);
        drawRect(k2 - 1, knobTop, k2, knobBot, knobBorder);


        // Text
        if (this.displayString != null && !this.displayString.isEmpty()) {
            drawCenteredString(mc.fontRendererObj, this.displayString,
                    xPosition + width / 2, yPosition + (height - 8) / 2, 0xFF00FF00);
        }
    }
}
