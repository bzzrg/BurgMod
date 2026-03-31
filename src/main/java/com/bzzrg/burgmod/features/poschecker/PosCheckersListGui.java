package com.bzzrg.burgmod.features.poschecker;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.PosCheckersConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.modutils.gui.BMListGui;
import com.bzzrg.burgmod.modutils.gui.CustomButton;
import com.bzzrg.burgmod.modutils.gui.CustomSlider;
import com.bzzrg.burgmod.modutils.gui.Row;
import com.bzzrg.burgmod.modutils.simulation.PlayerSim;
import com.bzzrg.burgmod.modutils.simulation.UpdateSimOptions;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.PosCheckersConfig.*;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.modutils.GeneralUtils.playErrorSound;
import static com.bzzrg.burgmod.modutils.simulation.SimUtils.createSim;
import static com.bzzrg.burgmod.modutils.simulation.SimUtils.updateSim;

public class PosCheckersListGui extends BMListGui {

    private static final int axisWidth = 40;
    private static final int airtimeWidth = 150;
    private static final int removeWidth = buttonHeight;

    public PosCheckersListGui() {

        this.setSettingWidth(130);
        this.setListWidth(axisWidth + airtimeWidth + removeWidth + buttonGap * 4);

        posCheckers.forEach(this::addCheckerRow);

        this.addActionButton("Add Checker", b -> {
            PosChecker checker = new PosChecker(Axis.X, 1);
            posCheckers.add(checker);
            addCheckerRow(checker);
        });
        this.addActionButton("Clear Checkers", b -> {
            posCheckers.clear();
            this.rows.clear();
        });
        this.nextColumn();
        NumberTextSetting<Double> xMinSetting = this.addDoubleSetting("Min X", () -> xMin, d -> { if (d != null) xMin = d; }, "Coordinate");
        NumberTextSetting<Double> xMaxSetting = this.addDoubleSetting("Max X", () -> xMax, d -> { if (d != null) xMax = d; }, "Coordinate");
        NumberTextSetting<Double> zMinSetting = this.addDoubleSetting("Min Z", () -> zMin, d -> { if (d != null) zMin = d; }, "Coordinate");
        NumberTextSetting<Double> zMaxSetting = this.addDoubleSetting("Max Z", () -> zMax, d -> { if (d != null) zMax = d; }, "Coordinate");

        this.addStringSetting("Min/Max From Pos", () -> "", s -> {

            EntityPlayerSP player = mc.thePlayer;

            double blockRange;
            try {
                blockRange = Double.parseDouble(s);
            } catch (Exception e) {
                bmChat("\u00A7cPlease input a valid number for your block range.");
                playErrorSound();
                return;
            }

            xMin = player.posX - blockRange;
            xMax = player.posX + blockRange;
            zMin = player.posZ - blockRange;
            zMax = player.posZ + blockRange;
            xMinSetting.field.field.setText(String.valueOf(xMin));
            xMaxSetting.field.field.setText(String.valueOf(xMax));
            zMinSetting.field.field.setText(String.valueOf(zMin));
            zMaxSetting.field.field.setText(String.valueOf(zMax));

        }, "Block Range");
        this.addStringSetting("Min/Max From Strat", () -> "", s -> {

            double blockRange;
            try {
                blockRange = Double.parseDouble(s);
            } catch (Exception e) {
                bmChat("\u00A7cPlease input a valid number for your block range.");
                playErrorSound();
                return;
            }

            Integer lastJumpIndex = StrategyTick.getLastJumpIndex();

            if (lastJumpIndex == null) {
                bmChat("\u00A7cYour strategy doesn't contain any jumps! (Read this command's info for more explanation)");
                playErrorSound();
                return;
            }

            PlayerSim sim = createSim();

            for (StrategyTick tick : strategyTicks) {
                updateSim(sim, new UpdateSimOptions(
                        tick.correctInputs.contains(InputType.W),
                        tick.correctInputs.contains(InputType.A),
                        tick.correctInputs.contains(InputType.S),
                        tick.correctInputs.contains(InputType.D),
                        tick.correctInputs.contains(InputType.SPR),
                        tick.correctInputs.contains(InputType.SNK),
                        tick.correctInputs.contains(InputType.JMP),
                        null));
                if (tick.getIndex() == lastJumpIndex) break;

            }

            xMin = sim.posX - blockRange;
            xMax = sim.posX + blockRange;
            zMin = sim.posZ - blockRange;
            zMax = sim.posZ + blockRange;
            xMinSetting.field.field.setText(String.valueOf(xMin));
            xMaxSetting.field.field.setText(String.valueOf(xMax));
            zMinSetting.field.field.setText(String.valueOf(zMin));
            zMaxSetting.field.field.setText(String.valueOf(zMax));

        }, "Block Range");

        this.addActionButton("Preview Min/Max Box", b -> {
            mc.displayGuiScreen(null);
            PosCheckersDrawer.drawFor4Seconds();
        });

    }

    @Override
    public void onGuiClosed() {
        PosCheckersConfig.instance.updateFile();
        super.onGuiClosed();
    }

    public void addCheckerRow(PosChecker checker) {

        this.rows.add(new Row() {
            final PosChecker posChecker = checker;

            GuiButton axisButton;
            GuiButton removeButton;

            @Override
            public void init() {


                axisButton = new CustomButton(buttonList.size(), listLeft + buttonGap, getCenteredY(buttonHeight), axisWidth, buttonHeight, posChecker.axis.name());
                buttons.add(axisButton);

                buttons.add(new CustomSlider(buttonList.size(), listLeft + axisWidth + buttonGap * 2, getCenteredY(buttonHeight), airtimeWidth, buttonHeight, "Airtime: ", "t", 1, 100, posChecker.airtime, false, true,
                        s -> posChecker.airtime = s.getValueInt()));

                removeButton = new CustomButton(buttonList.size(), listLeft + axisWidth + airtimeWidth + buttonGap * 3, getCenteredY(buttonHeight), removeWidth, buttonHeight, "\u00A74\u2716");
                buttons.add(removeButton);
            }

            @Override
            public void click(GuiButton button) {
                if (button == axisButton) {
                    Axis next = Axis.values()[(posChecker.axis.ordinal() + 1) % Axis.values().length];
                    posChecker.axis = next;
                    axisButton.displayString = next.name();
                } else if (button == removeButton) {
                    posCheckers.remove(posChecker);
                    rows.remove(this);
                }
            }
        });
    }

}
