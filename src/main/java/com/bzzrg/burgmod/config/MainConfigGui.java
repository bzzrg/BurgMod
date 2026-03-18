package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler;
import com.bzzrg.burgmod.config.basicconfig.InputStatusConfig;
import com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig;
import com.bzzrg.burgmod.config.basicconfig.TrajectoryConfig;
import com.bzzrg.burgmod.features.inputstatus.InputStatusConfigGui;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetConfigGui;
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
                case 1: buttonName = P45OffsetConfig.enabled ? "\u00A7aPerfect 45 Offset: ON" : "\u00A7cPerfect 45 Offset: OFF"; break;
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
                P45OffsetConfig.enabled = !P45OffsetConfig.enabled;
                button.displayString = P45OffsetConfig.enabled ? "\u00A7aPerfect 45 Offset: ON" : "\u00A7cPerfect 45 Offset: OFF";
                break;
            case 4:
                Minecraft.getMinecraft().displayGuiScreen(new P45OffsetConfigGui());
                break;
            case 5:
                Minecraft.getMinecraft().displayGuiScreen(null);
                bmChat("\u00A7bPerfect 45 Offset Info:");
                sendInfoBullet("What Does It Do?", "Perfect 45 Offset calculates the best landing offset you can still achieve for a 45 jump if the rest of your timings and angles are absolutely perfect. " +
                        "For example, in a quad 45, the label would be displaying the best possible offset for the quad 45 right after you reset. " +
                        "But after let's say 2 45s, now the best possible offset you can get is lower because of human error in the 45s you have done. " +
                        "If the calculated offset becomes negative, landing the jump is no longer possible.");
                sendInfoBullet("Show Auto Offset", "Shows offset label that automatically determines if offset should be X or Z. " +
                        "It works by taking your yaw when you reset and determining whether you are facing X or facing Z. " +
                        "Therefore, it might determine the wrong direction, which in that case you can just use the specific X/Z offset labels.");
                sendInfoBullet("Show X Offset", "Shows offset label that is always for the X direction.");
                sendInfoBullet("Show Z Offset", "Shows offset label that is always for the Z direction.");
                sendInfoBullet("Shorten Label", "Changes \"Perfect 45 Offset\" to \"P45 Offset\"");
                sendInfoBullet("# of 45s", "The amount of 45 strafes your jump contains. " +
                        "For example, if you are doing a triple 45, set # of 45s to 3. Your # of 45s must be equal to or less than the number of jumps in your strategy.");
                sendInfoBullet("Jump Angle", "The angle you jump at FOR YOUR 45 JUMPS, NOT YOUR NORMAL JUMPS. " +
                        "For most cases, this would just be the same angle as your angle after you reset. " +
                        "But in some cases, your reset angle is different than your jump angles for your 45 jumps, like for example a jump where you need to over/under turn on your first 45.");
                sendInfoBullet("45 Key", "The strafe key you use to 45. Either A or D. Needed for offset calculation.");
                sendInfoBullet("Stop On Input Fail", "Shows fail message on label and stops tracking offset when you fail your strategy's inputs. Resets after teleporting back to checkpoint.");
                sendInfoBullet("E Notation", "Uses E Notation for displayed offsets on the label. For example, \"-0.0000546\" -> \"-5.46e-5\"");
                sendInfoBullet("E Notation Max Exp.", "Sets a maximum exponent that E Notation can have for it to display as E Notation. " +
                        "For example, let's say this setting is set to -5. " +
                        "\"-0.000000546\" would be \"-5.46e-7\", but \"-0.00546\" would still be \"-0.00546\" because if that offset was in E Notation, it would be a power of -3 which would exceed the maximum power of -5.");
                sendInfoBullet("E Notation Precision", "Sets # of decimal places for the number part of the E Notation. " +
                        "For example, let's say this setting is set to 1. Then \"-0.000054689\" would be \"-5.5e-5\". This is independent from the decimal precision set in /bm dp.");
                sendInfoBullet("Fix Strafing", "For Perfect 45 Offset to work, your strategy must include the strafing and the A/D tapping you do while 45ing. " +
                        "You would usually need to add the strafing manually but this button serves as a shortcut to fix your strategy's strafing based on your # of 45s set in config. " +
                        "This may not work in some rare cases for some strategies, like for example 1bm 5-1. " +
                        "This is because for that jump, you jump with W+strafe instead of just W for your first 45, so it is abnormal. " +
                        "In those cases, just set the strafing yourself inside the strategy editor.");
                sendInfoBullet("What Does Invalid Config Mean?", "If your label says Invalid Config, it means one of the following 3:\n" +
                        "\u00A77- \u00A7eThe jump angle you have set is invalid (must be any valid number, decimals are allowed)\n" +
                        "\u00A77- \u00A7eThe # of 45s you have set is greater than the number of jumps in your strategy\n" +
                        "\u00A77- \u00A7eThe last tick of your strategy does not have AIR selected");
                sendInfoBullet("What Does Can't Find LB Mean?",
                        "If your label says Can't Find LB, it means that your strategy was simulated with perfect 45s, but no landing block within 200 ticks of the start of your strategy (Simulation starts at reset location). " +
                                "Note that only landing blocks that you actually land ON TOP OF are allowed/detected. " +
                                "For example, landing on top of a ladder is detected but grabbing the side of the ladder is not detected, and neither is water, lava, etc.");
                sendInfoBullet("Important Things To Know",
                        "\u00A77- \u00A7eStrategy is required for offset calculations.\n" +
                                "\u00A77- \u00A7eThe last tick of your strategy must be an air tick.\n" +
                                "\u00A77- \u00A7eYour # of 45s must be equal to or less than the number of jumps in your strategy.\n" +
                                "\u00A77- \u00A7eUsing Trim Strategy works for Perfect 45 Offset. The mod just extends the strategy internally using the last tick's inputs.\n" +
                                "\u00A77- \u00A7eOnly landing blocks that you actually land ON TOP OF are allowed/detected. " +
                                "For example, landing on top of a ladder is detected but grabbing the side of the ladder is not detected, and neither is water, lava, etc.\n" +
                                "\u00A77- \u00A7eYour strategy must include the strafing and the A/D tapping you do while 45ing. " +
                                "You can do this manually in the strategy editor or automatically with the Fix Strafing button (more info about this button inside its info block).");
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
                Minecraft.getMinecraft().displayGuiScreen(null);
                bmChat("\u00A7bStrategy Editor Info:");
                sendInfoBullet("What Is It For?", "The strategy editor is used to define the inputs of a strategy for a jump tick by tick. " +
                        "Features like Input Status and Perfect 45 Offset require you to have your strategy set for the jump you are currently doing.");
                sendInfoBullet("Add Tick", "Adds an individual tick to the strategy.");
                sendInfoBullet("Add Jump", "The add jump button just allows to add preset sequences of ticks that are common (like Jam or HH) instead of needing to do it manually through adding individual ticks. " +
                        "You use the Add Jump button by typing a jump type in the field below the button and then clicking the button (all jump types are listed if you try to add an invalid jump type).");
                sendInfoBullet("Jump Config Buttons",
                        "\u00A77- \u00A7bExtend Button: \u00A7eShows the ticks that actually make up the jump. " +
                                "These ticks can be modified manually, but changing the config of the jump using its config buttons will override any changes you have made to the jump's ticks manually.\n" +
                                "\u00A77- \u00A7bW/A/S/D: \u00A7eDefines the movement keys that are used for the jump.\n" +
                                "\u00A77- \u00A7bA/D: \u00A7eDefines the strafe key that is used for the jump.\n" +
                                "\u00A77- \u00A7bRun 1t: \u00A7eAdds one tick of running after the jump using whatever config is already set for the jump. " +
                                "For example, Run 1t would add one tick of W+A+SPR if the jump was a Jam with W+A+SPR.\n" +
                                "\u00A77- \u00A7b1-11t Slider: \u00A7eFor example, if set to 2t, then for HH it would be a 2t HH, for Mark it would be a 2t Mark, etc.");
                sendInfoBullet("Trim Strategy", "Cuts off all duplicate ticks at the end of your strategy except for one of them. " +
                        "This is because more than one of the same tick at the end of your strategy is redundant for all strategy related features.");
                sendInfoBullet("Mirror Strategy", "Switches all ticks/jumps with A selected to have D selected instead and vice versa. " +
                        "If the tick/jump has both A and D selected or has neither selected, it is unaffected.");
                sendInfoBullet("Record Strategy", "Records the player's movement so that the user can perform the strategy in game instead of setting ticks manually. " +
                        "To record properly, know that recording starts when you start moving after you have reset. " +
                        "Note that all recorded ticks are cleared when you reset, that way you don't have to re-record everytime you fail your inputs while trying to record the strategy. " +
                        "All empty ticks (ticks with no WASD, SPR, SNK, or AIR) are cut off when you stop recording.");
                sendInfoBullet("Preview Strategy", "Draws a line that shows the trajectory your player would follow from your current position if your strategy was performed perfectly tick by tick.");
                sendInfoBullet("Important Things To Know",
                        "\u00A77- \u00A7eHaving SPR selected for a tick means you are sprinting during that tick, not that you are holding down the sprint key during that tick.\n" +
                                "\u00A77- \u00A7eHaving SNK selected for a tick means you are sneaking during that tick, not that you are holding down the sneak key during that tick.\n" +
                                "\u00A77- \u00A7eHaving AIR selected for a tick means you are in the air during that tick, not that you are holding down the jump key during that tick.\n" +
                                "\u00A77- \u00A7eFeatures that use strategy might break or warn you if you have a tick with both SPR and SNK selected. " +
                                        "This is because it is impossible to be sprinting and sneaking at the same time.");

                break;
            case 12:
                mc.displayGuiScreen(null);
                ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/bm");
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        BasicConfigHandler.updateConfigFile();
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