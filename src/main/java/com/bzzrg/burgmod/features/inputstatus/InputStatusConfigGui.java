package com.bzzrg.burgmod.features.inputstatus;

import com.bzzrg.burgmod.config.basicconfig.InputStatusConfig;
import com.bzzrg.burgmod.features.FeatureConfigGui;

public class InputStatusConfigGui extends FeatureConfigGui {
    public InputStatusConfigGui() {
        this.addStrategyButton();

        this.addBooleanSetting("Show Fail Tick", () -> InputStatusConfig.showFailTick, b -> InputStatusConfig.showFailTick = b);
        this.addBooleanSetting("Show Fail Reason", () -> InputStatusConfig.showFailReason, b -> InputStatusConfig.showFailReason = b);
        this.addBooleanSetting("Shorten Label", () -> InputStatusConfig.shortenLabel, b -> InputStatusConfig.shortenLabel = b);
    }
}
