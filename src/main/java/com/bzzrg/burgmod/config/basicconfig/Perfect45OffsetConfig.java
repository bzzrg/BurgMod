package com.bzzrg.burgmod.config.basicconfig;

import com.bzzrg.burgmod.features.strategy.StrategyTick;

import static com.bzzrg.burgmod.config.ConfigHandler.config;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.InputType.AIR;

public class Perfect45OffsetConfig {

    public static boolean enabled = false;

    public static String labelsShown = "AUTO";
    public static boolean shortenLabel = false;

    public static int numOf45s = 1;
    public static String jumpAngle = "";
    public static String fortyFiveKey = "A";
    public static boolean stopOnInputFail = false;

    public static boolean eNotation = false;
    public static int eNotationMax = 3;

    public static int autoLabelX = 0;
    public static int autoLabelY = 0;

    public static int xLabelX = 0;
    public static int xLabelY = 0;

    public static int zLabelX = 0;
    public static int zLabelY = 0;

    public static void updateConfigFile() {
        config.get("perfect45Offset", "enabled", enabled).setValue(enabled);

        config.get("perfect45Offset", "labelsShown", labelsShown).set(labelsShown);
        config.get("perfect45Offset", "shortenLabel", shortenLabel).setValue(shortenLabel);

        config.get("perfect45Offset", "numOf45s", numOf45s).setValue(numOf45s);
        config.get("perfect45Offset", "jumpAngle", jumpAngle).set(jumpAngle);
        config.get("perfect45Offset", "fortyFiveKey", fortyFiveKey).set(fortyFiveKey);
        config.get("perfect45Offset", "stopOnInputFail", stopOnInputFail).setValue(stopOnInputFail);

        config.get("perfect45Offset", "eNotation", eNotation).setValue(eNotation);
        config.get("perfect45Offset", "eNotationMax", eNotationMax).setValue(eNotationMax);

        config.get("perfect45Offset", "autoLabelX", autoLabelX).setValue(autoLabelX);
        config.get("perfect45Offset", "autoLabelY", autoLabelY).setValue(autoLabelY);

        config.get("perfect45Offset", "xLabelX", xLabelX).setValue(xLabelX);
        config.get("perfect45Offset", "xLabelY", xLabelY).setValue(xLabelY);

        config.get("perfect45Offset", "zLabelX", zLabelX).setValue(zLabelX);
        config.get("perfect45Offset", "zLabelY", zLabelY).setValue(zLabelY);
    }

    public static void updateFields() {
        enabled = config.get("perfect45Offset", "enabled", enabled).getBoolean();

        labelsShown = config.get("perfect45Offset", "labelsShown", labelsShown).getString();
        shortenLabel = config.get("perfect45Offset", "shortenLabel", shortenLabel).getBoolean();

        numOf45s = config.get("perfect45Offset", "numOf45s", numOf45s).getInt();
        jumpAngle = config.get("perfect45Offset", "jumpAngle", jumpAngle).getString();
        fortyFiveKey = config.get("perfect45Offset", "fortyFiveKey", fortyFiveKey).getString();
        stopOnInputFail = config.get("perfect45Offset", "stopOnInputFail", stopOnInputFail).getBoolean();

        eNotation = config.get("perfect45Offset", "eNotation", eNotation).getBoolean();
        eNotationMax = config.get("perfect45Offset", "eNotationMax", eNotationMax).getInt();

        autoLabelX = config.get("perfect45Offset", "autoLabelX", autoLabelX).getInt();
        autoLabelY = config.get("perfect45Offset", "autoLabelY", autoLabelY).getInt();

        xLabelX = config.get("perfect45Offset", "xLabelX", xLabelX).getInt();
        xLabelY = config.get("perfect45Offset", "xLabelY", xLabelY).getInt();

        zLabelX = config.get("perfect45Offset", "zLabelX", zLabelX).getInt();
        zLabelY = config.get("perfect45Offset", "zLabelY", zLabelY).getInt();
    }

    public static Float getJumpAngle() {
        try {
            return Float.parseFloat(jumpAngle);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isConfigInvalid() {
        return !jumpAngle.isEmpty() && getJumpAngle() == null
                || StrategyTick.getJumpTick(numOf45s - 1) == null
                || !strategyTicks.isEmpty() && !strategyTicks.get(strategyTicks.size()-1).correctInputs.contains(AIR);
    }

}