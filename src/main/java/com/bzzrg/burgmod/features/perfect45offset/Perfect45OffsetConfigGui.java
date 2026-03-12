package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.features.FeatureConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyTick;

import static com.bzzrg.burgmod.config.basicconfig.Perfect45OffsetConfig.*;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.InputType.AIR;
import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;

public class Perfect45OffsetConfigGui extends FeatureConfigGui {
    public Perfect45OffsetConfigGui() {
        this.addStrategyButton();

        this.addEnumSetting("Label(s) Shown", () -> LabelsShown.valueOf(labelsShown), v -> labelsShown = v.name());
        this.addBooleanSetting("Shorten Label", () -> shortenLabel, b -> shortenLabel = b);

        this.addIntSetting("# of 45s", () -> numOf45s, i -> numOf45s = i, 1, 10);
        this.addStringSetting("Jump Angle", () -> jumpAngle, s -> jumpAngle = s, "DEFAULT = Reset Angle");
        this.addEnumSetting("45 Key", () -> FortyFiveKey.valueOf(fortyFiveKey), v -> fortyFiveKey = v.name());
        this.addBooleanSetting("Stop On Input Fail", () -> stopOnInputFail, b -> stopOnInputFail = b);

        this.addBooleanSetting("E Notation", () -> eNotation, b -> eNotation = b);
        this.addIntSetting("E Notation Max Power", () -> eNotationMaxPower, i -> eNotationMaxPower = i, -10, -1);
        this.addIntSetting("E Notation Precision", () -> eNotationPrecision, i -> eNotationPrecision = i, 0, 10);

    }

    @Override
    public void onGuiClosed() {

        if (enabled) {
            if (StrategyTick.getJumpTick(numOf45s - 1) == null) {
                bmChat("\u00A7cWARN: # of 45s inside perfect 45 offset config is more than # of jumps inside your strategy!");
                mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);
            }
            if (!strategyTicks.isEmpty() && !strategyTicks.get(strategyTicks.size()-1).correctInputs.contains(AIR)) { // If the last tick from strat is not air, send invalid msg
                bmChat("\u00A7cWARN: Perfect 45 offset feature requires last tick of strategy to be air!");
                mc.thePlayer.playSound("mob.endermen.portal", 1.0F, 0.5F);
            }
        }

        super.onGuiClosed();

    }

}
