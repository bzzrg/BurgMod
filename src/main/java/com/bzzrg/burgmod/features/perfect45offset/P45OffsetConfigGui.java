package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.features.FeatureConfigGui;
import com.bzzrg.burgmod.utils.gui.CustomButton;
import net.minecraft.client.gui.GuiButton;

import static com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig.*;

public class P45OffsetConfigGui extends FeatureConfigGui {
    public P45OffsetConfigGui() {
        this.addStrategyButton();

        this.addIntSetting("# of 45s", () -> numOf45s, i -> numOf45s = i, 1, 10);
        this.addStringSetting("Jump Angle", () -> jumpAngle, s -> jumpAngle = s, "DEFAULT = Reset Angle");
        this.addBooleanSetting("Apply JA To First", () -> applyJAToFirst, b -> applyJAToFirst = b);
        this.addEnumSetting("45 Key", () -> FortyFiveKey.valueOf(fortyFiveKey), v -> fortyFiveKey = v.name());
        this.nextColumn();
        this.addBooleanSetting("Show Auto Offset", () -> showAutoOffset, b -> showAutoOffset = b);
        this.addBooleanSetting("Show X Offset", () -> showXOffset, b -> showXOffset = b);
        this.addBooleanSetting("Show Z Offset", () -> showZOffset, b -> showZOffset = b);
        this.addBooleanSetting("Shorten Label", () -> shortenLabels, b -> shortenLabels = b);
        this.addBooleanSetting("E Notation", () -> eNotation, b -> eNotation = b);
        this.addIntSetting("E Notation Max Exp", () -> eNotationMaxExp, i -> eNotationMaxExp = i, -10, -1);
        this.addIntSetting("E Notation Precision", () -> eNotationPrecision, i -> eNotationPrecision = i, 0, 10);
        this.nextColumn();
        this.addBooleanSetting("Stop On Input Fail", () -> stopOnInputFail, b -> stopOnInputFail = b);
        this.addBooleanSetting("Show Overshoot Amount", () -> showOvershootAmount, b -> showOvershootAmount = b);
        this.addBooleanSetting("Show LB", () -> showLB, b -> showLB = b);
        this.addBooleanSetting("Show Jump Block", () -> showJumpBlock, b -> showJumpBlock = b);
        this.addBooleanSetting("Show JB/LB Line", () -> showJBLBLine, b -> showJBLBLine = b);
        this.addBooleanSetting("Show Perfect Line", () -> showPerfectLine, b -> showPerfectLine = b);
    }

    private static int fixStrat45sButtonID;
    @Override
    public void initGui() {
        super.initGui();

        fixStrat45sButtonID = buttonList.size();
        final int strafingButtonWidth = 80; // 80 = button width, should match strategy button width inside feature config gui
        final int strafingButtonX = width - 1 - borderInline - borderThickness - buttonGap - strafingButtonWidth;
        final int strafingButtonY = height - 1 - borderInline - borderThickness - (buttonGap + buttonHeight)*2;

        buttonList.add(new CustomButton(fixStrat45sButtonID, strafingButtonX, strafingButtonY, strafingButtonWidth, buttonHeight, "Fix Strat 45s"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button.id == fixStrat45sButtonID) {
            new FixStrat45sCommand().processCommand(mc.thePlayer, new String[]{});
        }
    }

}
