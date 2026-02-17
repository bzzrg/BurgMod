package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.config.featureconfig.FortyFiveStatusConfig;
import com.bzzrg.burgmod.config.featureconfig.InputStatusConfig;
import com.bzzrg.burgmod.config.featureconfig.TrajectoryConfig;
import com.bzzrg.burgmod.features.inputstatus.InputStatusConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.features.trajectory.TrajectoryConfigGui;
import com.bzzrg.burgmod.utils.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import static com.bzzrg.burgmod.utils.PluginUtils.sendMessage;

public class MainConfigGui extends GuiScreen {

    private static final int buttonHeight = 23;
    private static final int buttonGap = 5;

    @Override
    public void initGui() {

        int centerX = width / 2;

        int longButtonWidth = 150;
        int squareButtonSize = buttonHeight;
        int rowTotalWidth = longButtonWidth + buttonGap + squareButtonSize;
        int rowStartX = centerX - rowTotalWidth / 2;

        int startY = (height - 84) / 2;
        int rowSpacing = 30;

        for (int i = 0; i < 3; i++) {
            int y = startY + i * rowSpacing;

            String buttonName = "";

            switch (i) {
                case 0: buttonName = InputStatusConfig.enabled ? "\u00A7aInput Status: ON" : "\u00A7cInput Status: OFF"; break;
                case 1: buttonName = FortyFiveStatusConfig.enabled ? "\u00A7a45 Status: ON" : "\u00A7c45 Status: OFF"; break;
                case 2: buttonName = TrajectoryConfig.enabled ? "\u00A7aTrajectory: ON" : "\u00A7cTrajectory: OFF"; break;
            }

            // Toggle button for feature
            buttonList.add(new CustomButton(
                    i * 2,
                    rowStartX,
                    y,
                    longButtonWidth,
                    buttonHeight,
                    buttonName));

            // Config button for feature
            buttonList.add(new CustomButton(
                    i * 2 + 1,
                    rowStartX + longButtonWidth + buttonGap,
                    y,
                    squareButtonSize,
                    squareButtonSize,
                    "\u2699"
            ));

        }
        final int realWidth = width - 1;
        final int realHeight = height - 1;
        final int cornerButtonWidth = 80;
        final int cornerButtonX = realWidth - buttonGap - cornerButtonWidth;
        buttonList.add(new CustomButton(6, cornerButtonX, realHeight - (buttonHeight + buttonGap), cornerButtonWidth, buttonHeight, "Edit Positions"));
        buttonList.add(new CustomButton(7, cornerButtonX, realHeight - (buttonHeight + buttonGap) * 2, cornerButtonWidth, buttonHeight, "Edit Strategy"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Big BurgMod title
        GL11.glPushMatrix();
        GL11.glScalef(4f, 4f, 1f);
        drawCenteredString(
                fontRendererObj,
                "BurgMod",
                (int) (width / 2 / 4f),
                (int) (((height - 84) / 2 - 45) / 4f),
                0xFFFFFF
        );
        GL11.glPopMatrix();

        // Info
        fontRendererObj.drawString("\u00A77Use /bm for extra settings", 6, height - 7 - fontRendererObj.FONT_HEIGHT, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                InputStatusConfig.enabled = !InputStatusConfig.enabled;
                button.displayString = InputStatusConfig.enabled ? "\u00A7aInput Status: ON" : "\u00A7cInput Status: OFF";
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(new InputStatusConfigGui());
                break;
            case 2:
                sendMessage("\u00A7cThis feature is currently being worked on and will come out in a future update.");
                //FortyFiveStatusConfig.enabled = !FortyFiveStatusConfig.enabled;
                //button.displayString = FortyFiveStatusConfig.enabled ? "\u00A7a45 Status: ON" : "\u00A7c45 Status: OFF";
                break;
            case 3:
                sendMessage("\u00A7cThis feature is currently being worked on and will come out in a future update.");
                //Minecraft.getMinecraft().displayGuiScreen(new FortyFiveStatusConfigGui());
                break;
            case 4:
                TrajectoryConfig.enabled = !TrajectoryConfig.enabled;
                button.displayString = TrajectoryConfig.enabled ? "\u00A7aTrajectory: ON" : "\u00A7cTrajectory: OFF";
                break;
            case 5:
                Minecraft.getMinecraft().displayGuiScreen(new TrajectoryConfigGui());
                break;
            case 6:
                Minecraft.getMinecraft().displayGuiScreen(new EditPositionsGui());
                break;
            case 7:
                Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());
                break;
        }
    }
    @Override
    public void onGuiClosed() {
        ConfigHandler.updateConfigFromFields();
        super.onGuiClosed();
    }
}
