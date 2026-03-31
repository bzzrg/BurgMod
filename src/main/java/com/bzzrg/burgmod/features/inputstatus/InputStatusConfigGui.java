package com.bzzrg.burgmod.features.inputstatus;

import com.bzzrg.burgmod.config.files.mainconfigsections.InputStatusConfig;
import com.bzzrg.burgmod.modutils.gui.BMConfigGui;

public class InputStatusConfigGui extends BMConfigGui {
    public InputStatusConfigGui() {
        this.addStrategyButton();

        this.addBooleanSetting("Show Fail Tick", () -> InputStatusConfig.showFailTick, b -> InputStatusConfig.showFailTick = b);
        this.addBooleanSetting("Show Fail Reason", () -> InputStatusConfig.showFailReason, b -> InputStatusConfig.showFailReason = b);
        this.addBooleanSetting("Shorten Label", () -> InputStatusConfig.shortenLabel, b -> InputStatusConfig.shortenLabel = b);
    }
}
