package com.bzzrg.burgmod.features.inputstatus;

import com.bzzrg.burgmod.config.featureconfig.InputStatusConfig;
import com.bzzrg.burgmod.features.FeatureConfigGui;

public class InputStatusConfigGui extends FeatureConfigGui {
    public InputStatusConfigGui() {
        this.addStrategyButton = true;
        this.addBooleanSetting("Shorten Label", () -> InputStatusConfig.shortenLabel, b -> InputStatusConfig.shortenLabel = b);
    }
}
