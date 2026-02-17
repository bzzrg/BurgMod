package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.config.featureconfig.InputStatusConfig;
import com.bzzrg.burgmod.utils.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class EditPositionsGui extends GuiScreen {

    private static final int buttonHeight = 23;
    private static final int buttonGap = 5;

    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    private final String labelText = ConfigHandler.color1 + "Input Status: " + ConfigHandler.color2 + "Relocating...";

    @Override
    public void initGui() {
        buttonList.add(new CustomButton(0, buttonGap, this.height - buttonGap - buttonHeight, buttonHeight, buttonHeight, "<"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int x = InputStatusConfig.labelX;
        int y = InputStatusConfig.labelY;

        int width = fontRendererObj.getStringWidth(labelText);
        int height = fontRendererObj.FONT_HEIGHT;

        // Outline so you can see what you're dragging
        drawRect(x - 2, y - 2, x + width + 2, y + height + 2, 0x40FFFFFF);

        fontRendererObj.drawStringWithShadow(labelText, x, y, 0xFFFFFF);

        // Mouse handling
        if (Mouse.isButtonDown(0)) {
            if (!dragging &&
                    mouseX >= x && mouseX <= x + width &&
                    mouseY >= y && mouseY <= y + height) {

                dragging = true;
                dragOffsetX = mouseX - x;
                dragOffsetY = mouseY - y;
            }

        } else {
            dragging = false;
        }

        if (dragging) {
            InputStatusConfig.labelX = mouseX - dragOffsetX;
            InputStatusConfig.labelY = mouseY - dragOffsetY;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        }
    }

    @Override
    public void onGuiClosed() {
        ConfigHandler.updateConfigFromFields();
        super.onGuiClosed();
    }

}
