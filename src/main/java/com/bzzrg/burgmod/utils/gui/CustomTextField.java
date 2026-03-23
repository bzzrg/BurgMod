package com.bzzrg.burgmod.utils.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

import static com.bzzrg.burgmod.BurgMod.mc;

public class CustomTextField {

    public GuiTextField field;

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public final String label;
    public final String emptyMessage;

    public CustomTextField(int id, int x, int y, int width, int height, String label, String emptyMsg) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.label = label;
        this.emptyMessage = emptyMsg;

        boolean generateTitle = label != null;

        int labelDrawY = y + 2; // 1 px gap below top border
        int fieldHeight = 12;

        int fieldY;

        if (generateTitle) {

            int titleBottom = labelDrawY + mc.fontRendererObj.FONT_HEIGHT - 1;
            int minFieldY = titleBottom + 2;
            int maxFieldY = y + height - fieldHeight + 1;

            if (minFieldY > maxFieldY) {
                throw new IllegalArgumentException("Height too small for labeled CustomTextField.");
            }

            fieldY = maxFieldY;

        } else {

            fieldY = y + (height - fieldHeight) / 2 + 2;

        }

        field = new GuiTextField(id, mc.fontRendererObj, x + 4, fieldY, width - 8, fieldHeight);

        field.setEnableBackgroundDrawing(false);
        field.setTextColor(0xFFFFFFFF);
    }

    public void draw(int mouseX, int mouseY) {

        int baseColor = 0x00000000;
        int hoverColor = 0x32FFFFFF;
        int borderColor = 0xFFFFFFFF;
        int borderThickness = 1;

        boolean hovered = mouseX >= x && mouseY >= y &&
                mouseX < x + width && mouseY < y + height;

        int color = hovered ? hoverColor : baseColor;

        // Background
        Gui.drawRect(x, y, x + width, y + height, color);

        // Border
        Gui.drawRect(x, y, x + width, y + borderThickness, borderColor);
        Gui.drawRect(x, y + height - borderThickness, x + width, y + height, borderColor);
        Gui.drawRect(x, y, x + borderThickness, y + height, borderColor);
        Gui.drawRect(x + width - borderThickness, y, x + width, y + height, borderColor);

        if (label != null) {
            mc.fontRendererObj.drawStringWithShadow(label, x + 4, y + 3, -1);
        }

        field.drawTextBox();

        if (field.getText().isEmpty() && !field.isFocused()) {
            mc.fontRendererObj.drawStringWithShadow("\u00A77" + emptyMessage, field.xPosition, field.yPosition, -1);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        field.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void keyTyped(char typedChar, int keyCode) {
        field.textboxKeyTyped(typedChar, keyCode);
    }
}