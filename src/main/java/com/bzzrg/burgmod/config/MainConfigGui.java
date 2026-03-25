package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler;
import com.bzzrg.burgmod.config.basicconfig.InputStatusConfig;
import com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig;
import com.bzzrg.burgmod.config.basicconfig.TrajectoryConfig;
import com.bzzrg.burgmod.features.general.GeneralConfigGui;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.utils.GeneralUtils.chat;

public class MainConfigGui extends GuiScreen {

    private static final int buttonHeight = 25;
    private static final int buttonGap = 5;
    private static final int mainButtonWidth = 150;
    private static final int titleScale = 4;
    private static final int titleGap = 10;

    private final List<Row> rows = new ArrayList<>();

    private int editPositionsId = -1;
    private int commandUsageButtonId = -1;

    private void addFeature(String name, Supplier<Boolean> enabledGetter, Runnable onToggle, Runnable onSettings, Runnable onInfo) {
        rows.add(new Row(name, enabledGetter, onToggle, onSettings, onInfo, false));
    }
    private void addButton(String name, Runnable onClick, Runnable onSettings, Runnable onInfo) {
        rows.add(new Row(name, null, onClick, onSettings, onInfo, true));
    }

    @Override
    public void initGui() {
        buttonList.clear();
        rows.clear();

        this.addButton(
                "General Config",
                () -> mc.displayGuiScreen(new GeneralConfigGui()),
                null,
                null);

        this.addFeature(
                "Input Status",
                () -> InputStatusConfig.enabled,
                () -> InputStatusConfig.enabled = !InputStatusConfig.enabled,
                () -> mc.displayGuiScreen(new InputStatusConfigGui()),
                () -> {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                    bmChat("\u00A7bInput Status Info:");

                    chat("\u00A77-------- General --------");
                    sendInfoBullet("What Does It Do?", "Input Status uses your strategy from the Strategy Editor and checks every tick whether your inputs match those defined in the strategy. "
                            + "The comparison begins once you start moving after a reset. "
                            + "If a mismatch occurs at any tick, the label immediately notifies you that the inputs failed. "
                            + "Input Status requires a strategy to be set in the Strategy Editor.");

                    chat("\u00A77-------- Label Customization --------");
                    sendInfoBullet("Show Fail Tick", "Shows the tick you failed the strategy on. " +
                            "Corresponds to tick numbers inside the strategy editor GUI.");
                    sendInfoBullet("Show Fail Reason", "Shows the inputs that were correct, missing, or extra on the fail tick.\n" +
                            "\u00A77- \u00A7aGreen Input:\u00A7b Input was correct\n" +
                            "\u00A77- \u00A7e\u00A7mDashed Input:\u00A7r\u00A7b Input was missing (not pressed but expected from that tick)\n" +
                            "\u00A77- \u00A7e\u00A7lBolded Input:\u00A7r\u00A7b Input was extra (pressed but not expected from that tick)");
                    sendInfoBullet("Shorten Label", "Uses icons instead of full words for the label. For example, \"Waiting...\" -> \"...\"");
                });

        this.addFeature(
                "Perfect 45 Offset",
                () -> P45OffsetConfig.enabled,
                () -> P45OffsetConfig.enabled = !P45OffsetConfig.enabled,
                () -> mc.displayGuiScreen(new P45OffsetConfigGui()),
                () -> {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                    bmChat("\u00A7bPerfect 45 Offset Info:");

                    chat("\u00A77-------- General --------");
                    sendInfoBullet("What Does It Do?", "Perfect 45 Offset calculates the best landing offset you can still achieve for a 45 jump from your current position if the rest of your timings and angles are absolutely perfect. " +
                            "For example, let's consider a quad 45. " +
                            "The label starts by displaying the best possible offset you can get. " +
                            "But then, after let's say 2 45 strafes, the displayed offset would show the best offset you can still get if you are perfect from that point on, " +
                            "which would naturally be lower than the starting offset because of human error in the first 2 45 strafes. " +
                            "If the calculated offset becomes negative, landing the jump is no longer possible. " +
                            "If it is calculated that you will overshoot the momentum from your current position with perfect timings/angles, the label will indicate this.");

                    chat("\u00A77-------- Required Config --------");
                    sendInfoBullet("# of 45s", "Number of 45 strafes in your jump. Must be less than or equal to the number of jumps in your strategy.");
                    sendInfoBullet("Jump Angle", "The angle you jump at for your 45 strafe jumps (NOT your normal momentum jumps).");
                    sendInfoBullet("Apply JA To First", "Toggles whether your jump angle set in config is applied to the jump tick of your first 45 strafe when simulating offset.");
                    sendInfoBullet("45 Key", "Which strafe key (A or D) you use for 45s. Required for correct offset calculation.");
                    sendInfoBullet("Fix Strat 45s", "This button fixes the 45 jumps of your strategy if they are wrong (You are notified if they are wrong on config GUI close). " +
                            "Note that this fix is based on your # of 45s, 45 key, and the position of the jump ticks in your strategy. " +
                            "If any of those 3 are wrong for your strategy, this button will not work, so fix those before you use this.");

                    chat("\u00A77-------- Label Customization --------");
                    sendInfoBullet("Show Auto Offset", "Displays a single offset that automatically chooses X or Z based on your jump angle in config. " +
                            "If it chooses the wrong axis, use the X or Z labels instead.");
                    sendInfoBullet("Show X Offset", "Always shows the offset along the X axis.");
                    sendInfoBullet("Show Z Offset", "Always shows the offset along the Z axis.");
                    sendInfoBullet("Shorten Label", "Shortens \"Perfect 45 Offset\" to \"P45 Offset\".");
                    sendInfoBullet("E Notation", "Displays offsets in scientific notation (example: -0.0000546 → -5.46e-5).");
                    sendInfoBullet("E Notation Max Exp", "Sets the greatest (least negative) exponent allowed for scientific notation. " +
                            "For example, if this is set to -5, -1.23e-7 is allowed but -1.23e-3 is not.");
                    sendInfoBullet("E Notation Precision", "Sets how many decimal places are shown in the scientific notation value.");

                    chat("\u00A77-------- Other Config --------");
                    sendInfoBullet("Stop On Input Fail", "Stops tracking offset and shows fail message on label when you fail your strategy's inputs. Resets after teleporting back to checkpoint.");
                    sendInfoBullet("Show Overshoot Amount", "Changes the label from just saying \"Overshoot\" to saying \"OS\" followed by how much you would overshoot by.");
                    sendInfoBullet("Show LB", "Highlights the predicted landing block (lime green).");
                    sendInfoBullet("Show Jump Block", "Highlights the block you jump from before landing (light blue).");
                    sendInfoBullet("Show JB/LB Line", "Shows the path that was used for finding your jump block (block you do your last 45 strafe off of) and landing block (first block you collide with horizontally after 45 strafes). " +
                            "This path represents your movement if you performed your strategy without 45 strafing. " +
                            "The path only shows if either the jump block or landing block couldn't be found (if your label is showing \"Can't Find LB/JB\") so you can determine why. ");
                    sendInfoBullet("Show Perfect Line", "Shows the predicted path of your movement if you were to perform your strategy with perfect 45 strafes (displays on reset and lasts 5s).");

                    chat("\u00A77-------- Error Label Meanings --------");
                    sendInfoBullet("What Does Invalid JA Mean?", "The jump angle you have set in config is invalid (must be any valid number, decimals are allowed)");
                    sendInfoBullet("What Does Invalid Strategy Mean?", "Your strategy is invalid due to one of the following 3:\n" +
                            "\u00A77- \u00A7eThe # of 45s you have set is greater than the number of jumps in your strategy\n" +
                            "\u00A77- \u00A7eThe last tick of your strategy is not AIR\n" +
                            "\u00A77- \u00A7eThe 45 jumps from your strategy are inputted wrong (use Fix Strat 45s button)");
                    sendInfoBullet("What Does Can't Find LB Mean?", "Your strategy was simulated without 45 strafing, but no landing block was detected. " +
                            "No horizontal collision occurred within 100 ticks after the last jump. " +
                            "Only blocks landed on top of count (example: top of a ladder counts; ladder side, water, lava do not).");
                    sendInfoBullet("What Does Can't Find JB (T#) Mean?", "Your strategy was simulated without 45 strafing, " +
                            "but no jump block was detected on the tick # displayed (final landing tick before final jump) because you were not on the ground during that tick.");

                    chat("\u00A77-------- Extra --------");
                    sendInfoBullet("Important Things To Know",
                            "\u00A77- \u00A7eStrategy is required for offset calculations.\n" +
                                    "\u00A77- \u00A7eYour # of 45s must be equal to or less than the number of jumps in your strategy.\n" +
                                    "\u00A77- \u00A7eThe last tick of your strategy must be AIR.\n" +
                                    "\u00A77- \u00A7eYour strategy must include the strafing and the A/D tapping you do while 45ing. " +
                                    "If you use Fix Strat 45s, the strafing is included automatically.\n" +
                                    "\u00A77- \u00A7eUsing Trim Strategy works for Perfect 45 Offset. The mod just extends the strategy internally using the last tick's inputs.\n" +
                                    "\u00A77- \u00A7eThe landing block of your jump must be a block that you actually land directly on top of for Perfect 45 Offset to work " +
                                    "(example: top of a ladder counts; ladder side, water, lava do not).");
                });

        this.addFeature(
                "Trajectory",
                () -> TrajectoryConfig.enabled,
                () -> TrajectoryConfig.enabled = !TrajectoryConfig.enabled,
                () -> mc.displayGuiScreen(new TrajectoryConfigGui()),
                () -> {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                    bmChat("\u00A7bTrajectory Info:");

                    chat("\u00A77-------- General --------");
                    sendInfoBullet("What Does It Do?", "Trajectory uses your current rotation and inputs to display a line of trajectory you will follow considering your rotation and inputs stay the same.");
                    sendInfoBullet("How Is This Useful?", "This can be used for 45 strafe jumps or fast turn jumps to see if you will land earlier. " +
                            "It's also useful if you just like to see your player's trajectory at all times.");

                    chat("\u00A77-------- Line Customization --------");
                    sendInfoBullet("Tick Length", "The amount of ticks that the trajectory line should predict into the future.");
                });
        this.addButton(
                "Edit Strategy",
                () -> mc.displayGuiScreen(new StrategyConfigGui()),
                null,
                () -> {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                    bmChat("\u00A7bStrategy Editor Info:");

                    chat("\u00A77-------- General --------");
                    sendInfoBullet("What Is It For?", "The strategy editor is used to define the inputs of a strategy for a jump tick by tick. " +
                            "Features like Input Status and Perfect 45 Offset require you to have your strategy set for the jump you are currently doing.");

                    chat("\u00A77-------- Buttons --------");
                    sendInfoBullet("Add Tick", "Adds an individual tick to the strategy.");
                    sendInfoBullet("Add Jump", "The add jump button just allows to add preset sequences of ticks that are common (like Jam or HH) instead of needing to do it manually through adding individual ticks. " +
                            "You use the Add Jump button by typing a jump type in the field below the button and then clicking the button (all jump types are listed if you try to add an invalid jump type).");
                    sendInfoBullet("Jump Config Buttons",
                            "\u00A77- \u00A7bExtend Button: \u00A7eShows the ticks that actually make up the jump. " +
                                    "These ticks can be modified manually, but changing the config of the jump using its config buttons will override any changes you have made to the jump's ticks manually.\n" +
                                    "\u00A77- \u00A7bW/A/S/D: \u00A7eDefines which movement keys are used for the jump.\n" +
                                    "\u00A77- \u00A7bA/D: \u00A7eDefines the strafe key that is used for the jump.\n" +
                                    "\u00A77- \u00A7bRun 1t: \u00A7eAdds one tick of running after the jump using whatever config is already set for the jump. " +
                                    "For example, Run 1t would add one tick of W+A+SPR if the jump was a Jam with W+A+SPR.\n" +
                                    "\u00A77- \u00A7b1-11t Slider: \u00A7eFor example, if set to 2t, then for HH it would be a 2t HH, for Mark it would be a 2t Mark, etc.");
                    sendInfoBullet("Trim Strategy", "Deletes all duplicate ticks at the end of your strategy except for one of them. " +
                            "This is because more than one of the same tick at the end of your strategy is redundant for all strategy related features.");
                    sendInfoBullet("Mirror Strategy", "Switches all ticks/jumps with A selected to have D selected instead and vice versa. " +
                            "If the tick/jump has both A and D selected or has neither selected, it is unaffected.");
                    sendInfoBullet("Record Strategy", "Records the player's movement so that the user can perform the strategy in game instead of setting ticks manually. " +
                            "To record properly, know that recording starts when you start moving after you have reset. " +
                            "Note that all recorded ticks are cleared when you reset, that way you don't have to re-record every time you fail your inputs while trying to record the strategy. " +
                            "All empty ticks (ticks with no WASD, SPR, SNK, or AIR) are cut off when you stop recording.");
                    sendInfoBullet("Preview Strategy", "Draws a line that shows the trajectory your player would follow from your current position if your strategy was performed perfectly tick by tick.");

                    chat("\u00A77-------- Extra --------");
                    sendInfoBullet("Important Things To Know",
                            "\u00A77- \u00A7eHaving SPR selected for a tick means you are sprinting during that tick, not that you are holding down the sprint key during that tick.\n" +
                                    "\u00A77- \u00A7eHaving SNK selected for a tick means you are sneaking during that tick, not that you are holding down the sneak key during that tick.\n" +
                                    "\u00A77- \u00A7eHaving JMP selected for a tick means you jump on that tick, not that you are holding down the jump key during that tick.\n" +
                                    "\u00A77- \u00A7eFeatures that use strategy might break or warn you if you have a tick with both SPR and SNK selected. " +
                                    "This is because it is impossible to be sprinting and sneaking at the same time.");
                });

        int rowSpacing = buttonHeight + buttonGap;
        int rowsHeight = rows.size() * buttonHeight + Math.max(0, rows.size() - 1) * buttonGap;
        int titleTopY = (height - (getScaledTitleHeight() + titleGap + rowsHeight)) / 2;
        int firstRowY = titleTopY + getScaledTitleHeight() + titleGap;

        int id = 0;

        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);

            int totalWidth = getRowWidth(row);
            int startX = (width - totalWidth) / 2;
            int y = firstRowY + i * rowSpacing;

            row.mainButtonId = id;
            buttonList.add(new CustomButton(id++, startX, y, mainButtonWidth, buttonHeight, row.getMainLabel()));

            int currentX = startX + mainButtonWidth;

            if (row.hasSettings()) {
                currentX += buttonGap;
                row.settingsButtonId = id;
                buttonList.add(new CustomButton(id++, currentX, y, buttonHeight, buttonHeight, "\u2699"));
                currentX += buttonHeight;
            }

            if (row.hasInfo()) {
                currentX += buttonGap;
                row.infoButtonId = id;
                buttonList.add(new CustomButton(id++, currentX, y, buttonHeight, buttonHeight, "?"));
            }
        }

        int bottomButtonWidth = 100;
        int bottomY = height - (buttonHeight + buttonGap);

        int rightX = width - buttonGap - bottomButtonWidth;
        editPositionsId = id;
        buttonList.add(new CustomButton(id++, rightX, bottomY, bottomButtonWidth, buttonHeight, "Edit Positions"));

        commandUsageButtonId = id;
        buttonList.add(new CustomButton(id, buttonGap, bottomY, bottomButtonWidth, buttonHeight, "Command Usage"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        for (Row row : rows) {
            if (button.id == row.mainButtonId) {
                row.onMainClick.run();
                if (!row.configOnly) initGui();
                return;
            }
            if (button.id == row.settingsButtonId && row.onSettingsClick != null) {
                row.onSettingsClick.run();
                return;
            }
            if (button.id == row.infoButtonId && row.onInfoClick != null) {
                row.onInfoClick.run();
                return;
            }
        }

        if (button.id == editPositionsId) {
            Minecraft.getMinecraft().displayGuiScreen(new EditPositionsGui());
            return;
        }

        if (button.id == commandUsageButtonId) {
            mc.displayGuiScreen(null);
            ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/bm");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int rowsHeight = rows.size() * buttonHeight + Math.max(0, rows.size() - 1) * buttonGap;
        int titleTopY = (height - (getScaledTitleHeight() + titleGap + rowsHeight)) / 2;
        int titleCenterX = width / 2;

        GL11.glPushMatrix();
        GL11.glScalef(titleScale, titleScale, 1f);
        drawCenteredString(
                fontRendererObj,
                "BurgMod",
                (int) (titleCenterX / (float) titleScale),
                (int) (titleTopY / (float) titleScale),
                0xFFFFFF
        );
        GL11.glPopMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
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

    private int getRowWidth(Row row) {
        int width = mainButtonWidth;
        if (row.hasSettings()) width += buttonGap + buttonHeight;
        if (row.hasInfo()) width += buttonGap + buttonHeight;
        return width;
    }

    private int getScaledTitleHeight() {
        return fontRendererObj.FONT_HEIGHT * titleScale;
    }

    private static class Row {
        private final String name;
        private final Supplier<Boolean> enabledGetter;
        private final Runnable onMainClick;
        private final Runnable onSettingsClick;
        private final Runnable onInfoClick;
        private final boolean configOnly;

        private int mainButtonId = -1;
        private int settingsButtonId = -1;
        private int infoButtonId = -1;

        private Row(
                String name,
                Supplier<Boolean> enabledGetter,
                Runnable onMainClick,
                Runnable onSettingsClick,
                Runnable onInfoClick,
                boolean configOnly
        ) {
            this.name = name;
            this.enabledGetter = enabledGetter;
            this.onMainClick = onMainClick;
            this.onSettingsClick = onSettingsClick;
            this.onInfoClick = onInfoClick;
            this.configOnly = configOnly;
        }

        private boolean hasSettings() {
            return onSettingsClick != null;
        }

        private boolean hasInfo() {
            return onInfoClick != null;
        }

        private String getMainLabel() {
            if (configOnly) return "\u00A7f" + name;
            return enabledGetter.get()
                    ? "\u00A7a" + name + ": ON"
                    : "\u00A7c" + name + ": OFF";
        }
    }
}
