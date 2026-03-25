package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.features.BMConfigGui;

import static com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig.*;

public class P45OffsetConfigGui extends BMConfigGui {
    public P45OffsetConfigGui() {
        this.addStrategyButton();

        this.addIntSetting("# of 45s", () -> numOf45s, i -> numOf45s = i, 1, 10);
        this.addFloatSetting("Jump Angle", () -> jumpAngle.isEmpty() ? null : Float.valueOf(jumpAngle),
                f -> jumpAngle = f == null ? "" : f.toString(), "DEFAULT = Reset Angle");
        this.addBooleanSetting("Apply JA To First", () -> applyJAToFirst, b -> applyJAToFirst = b);
        this.addEnumSetting("45 Key", () -> FortyFiveKey.valueOf(fortyFiveKey), v -> fortyFiveKey = v.name());
        this.addActionButton("Fix Strat 45s", () -> new FixStrat45sCommand().processCommand(mc.thePlayer, new String[]{}));
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
        this.addStringSetting(
                "Username",
                () -> username,
                s -> username = s,
                "Enter username"
        );
        this.addStringSetting(
                "Command",
                () -> command,
                s -> command = s,
                "e.g. /warp spawn"
        );
    }
    public static String username = "", command = "";
}
