package com.bzzrg.burgmod.utils;

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
        int borderColor = 0xFFFFFFFF;
        int disabledColor = 0xFFFFFF00;
        int borderThickness = 1;

        // Hover detection
        this.hovered = mouseX >= xPosition && mouseY >= yPosition &&
                mouseX < xPosition + width && mouseY < yPosition + height;

        int color = this.hovered ? hoverColor : baseColor;

        if (!this.enabled) color = disabledColor;

        // Background
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, color);

        // Border
        drawRect(xPosition, yPosition, xPosition + width, yPosition + borderThickness, borderColor); // top
        drawRect(xPosition, yPosition + height - borderThickness, xPosition + width, yPosition + height, borderColor); // bottom
        drawRect(xPosition, yPosition, xPosition + borderThickness, yPosition + height, borderColor); // left
        drawRect(xPosition + width - borderThickness, yPosition, xPosition + width, yPosition + height, borderColor); // right

        // === Draw text ===
        drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xFFFFFFFF);
    }
}
