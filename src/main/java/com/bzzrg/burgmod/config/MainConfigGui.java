package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.PosCheckersConfig;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig;
import com.bzzrg.burgmod.config.files.mainconfigsections.*;
import com.bzzrg.burgmod.features.collisionoffset.CollisionOffsetConfigGui;
import com.bzzrg.burgmod.features.distanceoffset.DistanceOffsetConfigGui;
import com.bzzrg.burgmod.features.general.GeneralConfigGui;
import com.bzzrg.burgmod.features.inputstatus.InputStatusConfigGui;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetConfigGui;
import com.bzzrg.burgmod.features.poschecker.PosCheckersListGui;
import com.bzzrg.burgmod.features.strategy.StrategyListGui;
import com.bzzrg.burgmod.features.stratreminders.StratRemindersListGui;
import com.bzzrg.burgmod.features.trajectory.TrajectoryConfigGui;
import com.bzzrg.burgmod.features.turnhelper.TurnHelperListGui;
import com.bzzrg.burgmod.modutils.gui.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MainConfigGui extends GuiScreen {

    private static final int buttonHeight = 25;
    private static final int buttonGap = 5;
    private static final int mainButtonWidth = 150;
    private static final int titleScale = 4;
    private static final int titleGap = 10;

    private static final int columnGap = 20;
    private static int currentColumn = 0;

    public static final List<Option> options = new ArrayList<>();

    private int editPositionsId = -1;
    private int modGuideId = -1;

    private static void addFeature(String name, Supplier<Boolean> enabledGetter, Runnable onToggle, Runnable onSettings) {
        options.add(new Option(name, enabledGetter, onToggle, onSettings, true, currentColumn));
    }
    private static void addButton(String name, Runnable onClick) {
        options.add(new Option(name, null, onClick, null, false, currentColumn));
    }

    public static void nextColumn() {
        currentColumn++;
    }

    public static void initOptions() {
        addButton(
                "General Config",
                () -> Minecraft.getMinecraft().displayGuiScreen(new GeneralConfigGui()));
        addButton(
                "Strat Reminders",
                () -> Minecraft.getMinecraft().displayGuiScreen(new StratRemindersListGui()));
        addButton(
                "Strategy Editor",
                () -> Minecraft.getMinecraft().displayGuiScreen(new StrategyListGui()));

        nextColumn();

        addFeature(
                "Input Status",
                () -> InputStatusConfig.enabled,
                () -> InputStatusConfig.enabled = !InputStatusConfig.enabled,
                () -> Minecraft.getMinecraft().displayGuiScreen(new InputStatusConfigGui()));
        addFeature(
                "Perfect 45 Offset",
                () -> P45OffsetConfig.enabled,
                () -> P45OffsetConfig.enabled = !P45OffsetConfig.enabled,
                () -> Minecraft.getMinecraft().displayGuiScreen(new P45OffsetConfigGui()));
        addFeature(
                "Trajectory",
                () -> TrajectoryConfig.enabled,
                () -> TrajectoryConfig.enabled = !TrajectoryConfig.enabled,
                () -> Minecraft.getMinecraft().displayGuiScreen(new TrajectoryConfigGui()));
        addFeature(
                "Position Checkers",
                () -> PosCheckersConfig.enabled,
                () -> PosCheckersConfig.enabled = !PosCheckersConfig.enabled,
                () -> Minecraft.getMinecraft().displayGuiScreen(new PosCheckersListGui()));
        addFeature(
                "Turn Helper",
                () -> TurnHelperConfig.enabled,
                () -> TurnHelperConfig.enabled = !TurnHelperConfig.enabled,
                () -> Minecraft.getMinecraft().displayGuiScreen(new TurnHelperListGui()));
        addFeature(
                "Distance Offset",
                () -> DistanceOffsetConfig.enabled,
                () -> DistanceOffsetConfig.enabled = !DistanceOffsetConfig.enabled,
                () -> Minecraft.getMinecraft().displayGuiScreen(new DistanceOffsetConfigGui()));
        addFeature(
                "Collision Offset",
                () -> CollisionOffsetConfig.enabled,
                () -> CollisionOffsetConfig.enabled = !CollisionOffsetConfig.enabled,
                () -> Minecraft.getMinecraft().displayGuiScreen(new CollisionOffsetConfigGui()));
    }

    @Override
    public void initGui() {
        buttonList.clear();

        int rowSpacing = buttonHeight + buttonGap;

        // 1. Find how many columns we need (highest column number + 1)
        int maxColumn = 0;
        for (Option opt : options) {
            if (opt.column > maxColumn) maxColumn = opt.column;
        }
        int numColumns = maxColumn + 1;

        // 2. Group options by column and find max width per column
        List<List<Option>> columnOptions = new ArrayList<>();
        int[] columnMaxWidth = new int[numColumns];
        for (int i = 0; i < numColumns; i++) {
            columnOptions.add(new ArrayList<>());
            columnMaxWidth[i] = 0;
        }
        for (Option opt : options) {
            columnOptions.get(opt.column).add(opt);
            int optWidth = getOptionWidth(opt);
            if (optWidth > columnMaxWidth[opt.column]) {
                columnMaxWidth[opt.column] = optWidth;
            }
        }

        // 3. Find tallest column (most rows)
        int maxRows = 0;
        for (List<Option> col : columnOptions) {
            if (col.size() > maxRows) maxRows = col.size();
        }
        int totalRowsHeight = maxRows * buttonHeight + Math.max(0, maxRows - 1) * buttonGap;
        int titleTopY = (height - (getScaledTitleHeight() + titleGap + totalRowsHeight)) / 2;
        int firstRowY = titleTopY + getScaledTitleHeight() + titleGap;

        // 4. Calculate X positions for each column
        int totalColumnsWidth = 0;
        for (int w : columnMaxWidth) totalColumnsWidth += w;
        totalColumnsWidth += (numColumns - 1) * columnGap;
        int leftMargin = (width - totalColumnsWidth) / 2;

        int[] columnX = new int[numColumns];
        int currentX = leftMargin;
        for (int i = 0; i < numColumns; i++) {
            columnX[i] = currentX;
            currentX += columnMaxWidth[i] + columnGap;
        }

        // 5. Create buttons for each option
        int id = 0;
        for (int col = 0; col < numColumns; col++) {
            List<Option> colOpts = columnOptions.get(col);
            int xBase = columnX[col];
            for (int row = 0; row < colOpts.size(); row++) {
                Option option = colOpts.get(row);
                int y = firstRowY + row * rowSpacing;

                // Main button
                option.mainButtonId = id;
                buttonList.add(new CustomButton(id++, xBase, y, mainButtonWidth, buttonHeight, option.getMainLabel()));

                int currentXOffset = xBase + mainButtonWidth;

                // Settings button
                if (option.hasSettings()) {
                    currentXOffset += buttonGap;
                    option.settingsButtonId = id;
                    buttonList.add(new CustomButton(id++, currentXOffset, y, buttonHeight, buttonHeight, "\u2699"));
                }
            }
        }

        // 6. Bottom buttons (unchanged)
        int bottomButtonWidth = 100;
        int bottomY = height - (buttonHeight + buttonGap);

        int rightX = width - buttonGap - bottomButtonWidth;
        editPositionsId = id++;
        buttonList.add(new CustomButton(editPositionsId, rightX, bottomY, bottomButtonWidth, buttonHeight, "Edit Positions"));

        modGuideId = id;
        int guideButtonWidth = 150;
        buttonList.add(new CustomButton(modGuideId, buttonGap, bottomY, guideButtonWidth, buttonHeight, "\u00A7b\u00A7lMod Guide (README)"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        for (Option option : options) {
            if (button.id == option.mainButtonId) {
                option.onMainClick.run();
                if (option.isFeature) initGui();
                return;
            }
            if (button.id == option.settingsButtonId && option.onSettingsClick != null) {
                option.onSettingsClick.run();
                return;
            }
        }

        if (button.id == editPositionsId) {
            Minecraft.getMinecraft().displayGuiScreen(new EditPositionsGui());
        }

        if (button.id == modGuideId) {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/bzzrg/BurgMod/blob/main/README.md"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // Recalculate tallest column (same logic as in initGui)
        int maxColumn = 0;
        for (Option opt : options) {
            if (opt.column > maxColumn) maxColumn = opt.column;
        }
        int numColumns = maxColumn + 1;

        List<List<Option>> columnOptions = new ArrayList<>();
        for (int i = 0; i < numColumns; i++) {
            columnOptions.add(new ArrayList<>());
        }
        for (Option opt : options) {
            columnOptions.get(opt.column).add(opt);
        }

        int maxRows = 0;
        for (List<Option> col : columnOptions) {
            if (col.size() > maxRows) maxRows = col.size();
        }

        int totalRowsHeight = maxRows * buttonHeight + Math.max(0, maxRows - 1) * buttonGap;
        int titleTopY = (height - (getScaledTitleHeight() + titleGap + totalRowsHeight)) / 2;
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

    private int getOptionWidth(Option option) {
        int width = mainButtonWidth;
        if (option.hasSettings()) width += buttonGap + buttonHeight;
        return width;
    }

    private int getScaledTitleHeight() {
        return fontRendererObj.FONT_HEIGHT * titleScale;
    }

    public static class Option {
        public final String name;
        public final Supplier<Boolean> enabledGetter;
        public final Runnable onMainClick;
        public final Runnable onSettingsClick;
        public final boolean isFeature;
        public final int column;

        public int mainButtonId = -1;
        public int settingsButtonId = -1;

        public Option(String name, Supplier<Boolean> enabledGetter, Runnable onMainClick, Runnable onSettingsClick, boolean isFeature, int column) {
            this.name = name;
            this.enabledGetter = enabledGetter;
            this.onMainClick = onMainClick;
            this.onSettingsClick = onSettingsClick;
            this.isFeature = isFeature;
            this.column = column;
        }

        public boolean hasSettings() {
            return onSettingsClick != null;
        }


        public String getMainLabel() {
            if (!isFeature) return "\u00A7f" + name;
            return enabledGetter.get()
                    ? "\u00A7a" + name + ": ON"
                    : "\u00A7c" + name + ": OFF";
        }
    }
}
