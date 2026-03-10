package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.config.basicconfig.InputStatusConfig;
import com.bzzrg.burgmod.config.basicconfig.Perfect45OffsetConfig;
import com.bzzrg.burgmod.config.basicconfig.TrajectoryConfig;
import com.bzzrg.burgmod.features.inputstatus.InputStatusConfigGui;
import com.bzzrg.burgmod.features.perfect45offset.Perfect45OffsetConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.features.trajectory.TrajectoryConfigGui;
import com.bzzrg.burgmod.utils.gui.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.opengl.GL11;

public class MainConfigGui extends GuiScreen {

    private static final int buttonHeight = 25;
    private static final int buttonGap = 5;

    @Override
    public void initGui() {

        int centerX = width / 2;

        int longButtonWidth = 150;
        int squareButtonSize = buttonHeight;
        int rowTotalWidth = longButtonWidth + buttonGap + squareButtonSize + buttonGap + squareButtonSize;
        int rowStartX = centerX - rowTotalWidth / 2;

        int startY = (height - 84) / 2;
        int rowSpacing = 30;

        for (int i = 0; i < 3; i++) {
            int y = startY + i * rowSpacing;

            String buttonName = "";

            switch (i) {
                case 0: buttonName = InputStatusConfig.enabled ? "\u00A7aInput Status: ON" : "\u00A7cInput Status: OFF"; break;
                case 1: buttonName = Perfect45OffsetConfig.enabled ? "\u00A7a45 Status: ON" : "\u00A7c45 Status: OFF"; break;
                case 2: buttonName = TrajectoryConfig.enabled ? "\u00A7aTrajectory: ON" : "\u00A7cTrajectory: OFF"; break;
            }

            buttonList.add(new CustomButton(
                    i * 3,
                    rowStartX,
                    y,
                    longButtonWidth,
                    buttonHeight,
                    buttonName));

            buttonList.add(new CustomButton(
                    i * 3 + 1,
                    rowStartX + longButtonWidth + buttonGap,
                    y,
                    squareButtonSize,
                    squareButtonSize,
                    "\u2699"
            ));

            buttonList.add(new CustomButton(
                    i * 3 + 2,
                    rowStartX + longButtonWidth + buttonGap + squareButtonSize + buttonGap,
                    y,
                    squareButtonSize,
                    squareButtonSize,
                    "?"
            ));
        }

        final int realWidth = width - 1;
        final int realHeight = height - 1;
        final int cornerButtonWidth = 80;
        final int cornerButtonX = realWidth - buttonGap - cornerButtonWidth;

        buttonList.add(new CustomButton(9, cornerButtonX, realHeight - (buttonHeight + buttonGap), cornerButtonWidth, buttonHeight, "Edit Positions"));

        buttonList.add(new CustomButton(10, cornerButtonX, realHeight - (buttonHeight + buttonGap) * 2, cornerButtonWidth, buttonHeight, "Edit Strategy"));

        buttonList.add(new CustomButton(11, cornerButtonX - buttonHeight - buttonGap, realHeight - (buttonHeight + buttonGap) * 2, buttonHeight, buttonHeight, "?"));

        buttonList.add(new CustomButton(12, 6, realHeight - (buttonHeight + buttonGap), 110, buttonHeight, "Command Usage"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

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

        int cmdButtonX = 6;

        fontRendererObj.drawString(
                "Use /bm for extra settings",
                cmdButtonX,
                height - (buttonHeight + buttonGap) - fontRendererObj.FONT_HEIGHT - 6,
                0xFFFFFF
        );

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
                break;
            case 3:
                Perfect45OffsetConfig.enabled = !Perfect45OffsetConfig.enabled;
                button.displayString = Perfect45OffsetConfig.enabled ? "\u00A7aPerfect 45 Offset: ON" : "\u00A7cPerfect 45 Offset: OFF";
                break;
            case 4:
                Minecraft.getMinecraft().displayGuiScreen(new Perfect45OffsetConfigGui());
                break;
            case 5:
                break;
            case 6:
                TrajectoryConfig.enabled = !TrajectoryConfig.enabled;
                button.displayString = TrajectoryConfig.enabled ? "\u00A7aTrajectory: ON" : "\u00A7cTrajectory: OFF";
                break;
            case 7:
                Minecraft.getMinecraft().displayGuiScreen(new TrajectoryConfigGui());
                break;
            case 8:
                break;
            case 9:
                Minecraft.getMinecraft().displayGuiScreen(new EditPositionsGui());
                break;
            case 10:
                Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());
                break;
            case 11:
                break;
            case 12:
                mc.displayGuiScreen(null);
                ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/bm");
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        ConfigHandler.updateConfigFile();
        super.onGuiClosed();
    }
}