package com.bzzrg.burgmod.features.inputstatus;

import com.bzzrg.burgmod.config.basicconfig.InputStatusConfig;
import com.bzzrg.burgmod.features.FeatureConfigGui;

import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.utils.GeneralUtils.chat;

public class InputStatusConfigGui extends FeatureConfigGui {
    public InputStatusConfigGui() {
        this.addStrategyButton = true;
        this.addBooleanSetting("Show Fail Tick", () -> InputStatusConfig.showFailTick, b -> InputStatusConfig.showFailTick = b);
        this.addBooleanSetting("Show Fail Reason", () -> InputStatusConfig.showFailReason, b -> {
            InputStatusConfig.showFailReason = b;
            if (b) { // If setting show fail reason to true, add guide msgs in chat
                bmChat("\u00A7bFail Reason Guide:");
                chat("\u00A77- \u00A7eGreen Input: Input was correct");
                chat("\u00A77- \u00A7eDashed Input: Input was missing (not pressed but expected from that tick)");
                chat("\u00A77- \u00A7eBolded Input: Input was extra (pressed but not expected from that tick)");
            }
        });
        this.addBooleanSetting("Shorten Label", () -> InputStatusConfig.shortenLabel, b -> InputStatusConfig.shortenLabel = b);
    }
}
