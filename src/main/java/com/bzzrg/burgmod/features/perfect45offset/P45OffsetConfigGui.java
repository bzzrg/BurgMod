package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.specialconfig.StrategyConfig;
import com.bzzrg.burgmod.features.FeatureConfigGui;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.utils.gui.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig.*;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.InputType.AIR;
import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;

public class P45OffsetConfigGui extends FeatureConfigGui {
    public P45OffsetConfigGui() {
        this.addStrategyButton();

        this.addBooleanSetting("Show Auto Offset", () -> showAutoOffset, b -> showAutoOffset = b);
        this.addBooleanSetting("Show X Offset", () -> showXOffset, b -> showXOffset = b);
        this.addBooleanSetting("Show Z Offset", () -> showZOffset, b -> showZOffset = b);
        this.addBooleanSetting("Shorten Label", () -> shortenLabels, b -> shortenLabels = b);
        this.addBooleanSetting("E Notation", () -> eNotation, b -> eNotation = b);
        this.addIntSetting("E Notation Max Exp.", () -> eNotationMaxExp, i -> eNotationMaxExp = i, -10, -1);
        this.addIntSetting("E Notation Precision", () -> eNotationPrecision, i -> eNotationPrecision = i, 0, 10);
        this.nextColumn();
        this.addIntSetting("# of 45s", () -> numOf45s, i -> numOf45s = i, 1, 10);
        this.addStringSetting("Jump Angle", () -> jumpAngle, s -> jumpAngle = s, "DEFAULT = Reset Angle");
        this.addEnumSetting("45 Key", () -> FortyFiveKey.valueOf(fortyFiveKey), v -> fortyFiveKey = v.name());
        this.nextColumn();
        this.addBooleanSetting("Stop On Input Fail", () -> stopOnInputFail, b -> stopOnInputFail = b);
        this.addBooleanSetting("Show Overshoot Amount", () -> showOvershootAmount, b -> showOvershootAmount = b);
        this.addBooleanSetting("Apply JA To First", () -> applyJAToFirst, b -> applyJAToFirst = b);
        this.addBooleanSetting("Highlight LB", () -> highlightLB, b -> highlightLB = b);
        this.addBooleanSetting("Trajectory On Reset", () -> trajectoryOnReset, b -> trajectoryOnReset = b);
    }

    private static int fixStrafingButtonID;
    @Override
    public void initGui() {
        super.initGui();

        fixStrafingButtonID = buttonList.size();
        final int strafingButtonWidth = 80; // 80 = button width, should match strategy button width inside feature config gui
        final int strafingButtonX = width - 1 - borderInline - borderThickness - buttonGap - strafingButtonWidth;
        final int strafingButtonY = height - 1 - borderInline - borderThickness - (buttonGap + buttonHeight)*2;

        buttonList.add(new CustomButton(fixStrafingButtonID, strafingButtonX, strafingButtonY, strafingButtonWidth, buttonHeight, "Fix Strafing"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == fixStrafingButtonID) {
            if (P45OffsetLabel.isConfigInvalid()) {
                bmChat("\u00A7cYour Perfect 45 Offset/Strategy config must be valid to fix the strafing of your strategy to match your perfect 45 offset config!");
                mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);

            } else {

                List<Integer> jumpTickIndices = new ArrayList<>();
                for (int i = 0;; i++) {
                    StrategyTick jumpTick = StrategyTick.getJumpTick(i);
                    if (jumpTick == null) break;
                    jumpTickIndices.add(jumpTick.getTickNum());
                }
                Collections.reverse(jumpTickIndices);
                List<Integer> jumpTick45Indices = jumpTickIndices.subList(jumpTickIndices.size() - numOf45s, jumpTickIndices.size());

                // if the last jump tick is the last tick, duplicate the last tick
                if (jumpTickIndices.get(jumpTickIndices.size()-1) == strategyTicks.size()-1) {
                    StrategyTick lastTick = strategyTicks.get(strategyTicks.size()-1);
                    if (lastTick.jump == null) {
                        StrategyTick.addLoneTick(strategyTicks.size(), new HashSet<>(lastTick.correctInputs));
                    } else {
                        StrategyTick.addJumpTick(strategyTicks.size(), new HashSet<>(lastTick.correctInputs), lastTick.jump);
                    }
                }

                for (StrategyTick tick : strategyTicks) {

                    int tickNum = tick.getTickNum();

                    if (jumpTick45Indices.contains(tickNum) && tickNum != jumpTick45Indices.get(0)) {
                        tick.correctInputs.remove(InputType.A); // remove before adding to avoid duplicates
                        tick.correctInputs.remove(InputType.D);
                        tick.correctInputs.add(InputType.A);
                        tick.correctInputs.add(InputType.D);
                    }
                    if (tickNum > jumpTick45Indices.get(0) && !jumpTick45Indices.contains(tickNum)) {
                        if (fortyFiveKey.equals("A")) {
                            tick.correctInputs.remove(InputType.A);
                            tick.correctInputs.add(InputType.A);
                        } else if (fortyFiveKey.equals("D")) {
                            tick.correctInputs.remove(InputType.D);
                            tick.correctInputs.add(InputType.D);
                        }
                    }
                }

                StrategyConfig.updateStrategyJson();
                Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (enabled) {
            if (!jumpAngle.isEmpty() && getJumpAngle() == null) {
                bmChat("\u00A7cWARN: Your jump angle is invalid! Either leave it blank or input a valid number.");
                mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);
            }
            if (StrategyTick.getJumpTick(numOf45s - 1) == null) {
                bmChat("\u00A7cWARN: # of 45s inside perfect 45 offset config is more than # of jumps inside your strategy!");
                mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);
            }
            if (!strategyTicks.isEmpty() && !strategyTicks.get(strategyTicks.size()-1).correctInputs.contains(AIR)) { // If the last tick from strat is not air, send invalid msg
                bmChat("\u00A7cWARN: Perfect 45 offset feature requires last tick of strategy to be air!");
                mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);
            }
        }


    }

}
