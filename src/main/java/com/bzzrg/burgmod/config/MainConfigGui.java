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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.opengl.GL11;

import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;

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
                case 1: buttonName = Perfect45OffsetConfig.enabled ? "\u00A7aPerfect 45 Offset: ON" : "\u00A7cPerfect 45 Offset: OFF"; break;
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
                Minecraft.getMinecraft().displayGuiScreen(null);
                bmChat("\u00A7bInput Status Info:");
                sendInfoBullet("What Does It Do?", "Input Status uses your strategy from the Strategy Editor and checks every tick whether your inputs match those defined in the strategy. "
                                + "The comparison begins once you start moving after a reset. "
                                + "If a mismatch occurs at any tick, the label immediately notifies you that the inputs failed. "
                                + "Input Status requires a strategy to be set in the Strategy Editor.");
                sendInfoBullet("Show Fail Tick", "Shows the tick you failed the strategy on. " +
                        "Corresponds to tick numbers inside the strategy editor GUI.");
                sendInfoBullet("Show Fail Reason", "Shows the inputs that were correct, missing, or extra on the fail tick.\n" +
                        "\u00A77- \u00A7aGreen Input:\u00A7b Input was correct\n" +
                        "\u00A77- \u00A7e\u00A7mDashed Input:\u00A7r\u00A7b Input was missing (not pressed but expected from that tick)\n" +
                        "\u00A77- \u00A7e\u00A7lBolded Input:\u00A7r\u00A7b Input was extra (pressed but not expected from that tick)");
                sendInfoBullet("Shorten Label", "Uses icons instead of full words for the label. For example, \"Waiting...\" -> \"...\"");

                break;
            case 3:
                Perfect45OffsetConfig.enabled = !Perfect45OffsetConfig.enabled;
                button.displayString = Perfect45OffsetConfig.enabled ? "\u00A7aPerfect 45 Offset: ON" : "\u00A7cPerfect 45 Offset: OFF";
                break;
            case 4:
                Minecraft.getMinecraft().displayGuiScreen(new Perfect45OffsetConfigGui());
                break;
            case 5:
                Minecraft.getMinecraft().displayGuiScreen(null);
                bmChat("\u00A7bPerfect 45 Offset Info:");
                sendInfoBullet("What Does It Do?", "Perfect 45 Offset calculates the best landing offset you can still achieve for a 45 jump if the rest of your timings and angles are absolutely perfect. " +
                        "For example, in a quad 45, the label would be displaying the best possible offset for the quad 45 right after you reset. " +
                        "But after let's say 2 45s, now the best possible offset you can get is lower because of human error in the 45s you have done. " +
                        "If the calculated offset becomes negative, that would indicate that landing the jump is no longer possible. ");
                sendInfoBullet("Label(s) Shown",
                        "\u00A77- \u00A7aAuto: \u00A76Shows just one label that automatically determines if offset should be X or Z. Useful for non-diagonal 45 strafe jumps.\n" +
                                "\u00A77- \u00A7aX & Z: \u00A76Shows 2 labels. One is for X offset, and one is for Z offset. Useful for diagonal 45 strafe jumps.");
                sendInfoBullet("Shorten Label", "Changes \"Perfect 45 Offset\" to \"P45 Offset\"");
                sendInfoBullet("# of 45s", "The amount of 45 strafes your jump contains. " +
                        "For example, if you are doing a triple 45, set # of 45s to 3. Your # of 45s must be equal to or less than the number of jumps in your strategy.");
                sendInfoBullet("Jump Angle", "The angle you jump at FOR YOUR 45 JUMPS, NOT YOUR NORMAL JUMPS. " +
                        "For most cases, this would just be the same angle as your angle after you reset. " +
                        "But in some cases, your reset angle is different than your jump angles for your 45 jumps, like for example a jump where you need to over/under turn on your first 45.");
                sendInfoBullet("45 Key", "The strafe key you use to 45. Either A or D. Needed for offset calculation.");
                sendInfoBullet("Stop On Input Fail", "Shows fail message on label and stops tracking offset when you fail your strategy's inputs. Resets after teleporting back to checkpoint.");
                sendInfoBullet("E Notation", "Uses E Notation for displayed offsets on the label. For example, \"-0.0000546\" -> \"-5.46e-5\"");
                sendInfoBullet("E Notation Max Power", "Sets a maximum power that E Notation can be for it to display as E Notation. " +
                        "For example, let's say this setting is set to -5. " +
                        "\"-0.000000546\" would be \"-5.46e-7\", but \"-0.00546\" would still be  \"-0.00546\" because if that offset was in E Notation, it would be a power of -3 which would exceed the maximum power of -5.");
                sendInfoBullet("E Notation Precision", "Sets # of decimal places for the number part of the E Notation. " +
                        "For example, let's say this setting is set to 1. Then \"-0.000054689\" would be \"-5.5e-5\". This is independent from the decimal precision set in /bm dp.");
                sendInfoBullet("Important Things To Know",
                        "\u00A77- \u00A76Strategy is required for offset calculations.\n" +
                                "\u00A77- \u00A76The last tick of your strategy must be an air tick.\n" +
                                "\u00A77- \u00A76Your # of 45s must be equal to or less than the number of jumps in your strategy.\n" +
                                "\u00A77- \u00A76Using Trim Strategy works for Perfect 45 Offset. The mod just extends the strategy internally using the last tick's inputs.");
                break;
            case 6:
                TrajectoryConfig.enabled = !TrajectoryConfig.enabled;
                button.displayString = TrajectoryConfig.enabled ? "\u00A7aTrajectory: ON" : "\u00A7cTrajectory: OFF";
                break;
            case 7:
                Minecraft.getMinecraft().displayGuiScreen(new TrajectoryConfigGui());
                break;
            case 8:
                Minecraft.getMinecraft().displayGuiScreen(null);
                bmChat("\u00A7bTrajectory Info:");
                sendInfoBullet("What Does It Do?", "Trajectory uses your current rotation and inputs to display a line of trajectory you will follow considering your rotation and inputs stay the same.");
                sendInfoBullet("How Is This Useful?", "This can be used for 45 strafe jumps or fast turn jumps to see if you will land earlier. " +
                        "It's also useful if you just like to see your player's trajectory at all times.");
                sendInfoBullet("Tick Length", "The amount of ticks that the trajectory line should predict into the future.");

                break;
            case 9:
                Minecraft.getMinecraft().displayGuiScreen(new EditPositionsGui());
                break;
            case 10:
                Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());
                break;
            case 11:


                // add strategy info here 

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

    private static void sendInfoBullet(String bullet, String info) {

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        IChatComponent nonInfoComp = new ChatComponentText("\u00A77- \u00A7e" + bullet + " ");
        IChatComponent infoComp = new ChatComponentText("\u00A76\u00A7l[INFO]");
        infoComp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("\u00A76" + info)));
        player.addChatMessage(nonInfoComp.appendSibling(infoComp));

    }

}