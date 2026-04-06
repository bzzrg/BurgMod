package com.bzzrg.burgmod.modutils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class CustomButton extends GuiButton {

    public CustomButton(int id, int xPosition, int yPosition, int width, int height, String displayString) {
        super(id, xPosition, yPosition, width, height, displayString);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) return;

        int baseColor = 0x00000000;
        int hoverColor = 0x32FFFFFF;
        int disabledColor = 0xFF000000;
        int borderColor = 0xFFFFFFFF;
        int borderThickness = 1;

        this.hovered = mouseX >= xPosition && mouseY >= yPosition &&
                mouseX < xPosition + width && mouseY < yPosition + height;

        int color = this.hovered ? hoverColor : baseColor;

        if (!this.enabled) color = disabledColor;

        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, color);

        drawRect(xPosition, yPosition, xPosition + width, yPosition + borderThickness, borderColor);
        drawRect(xPosition, yPosition + height - borderThickness, xPosition + width, yPosition + height, borderColor);
        drawRect(xPosition, yPosition, xPosition + borderThickness, yPosition + height, borderColor);
        drawRect(xPosition + width - borderThickness, yPosition, xPosition + width, yPosition + height, borderColor);

        int textColor = this.enabled ? 0xFFFFFFFF : 0xFF888888;

        drawCenteredString(mc.fontRendererObj, displayString,
                xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
    }
}
