package com.bzzrg.burgmod.config.basicconfig;

import static com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler.*;

public class InputStatusConfig {

    private static final String CAT = "inputStatus";

    public static boolean enabled = false;

    public static boolean showFailTick = true;
    public static boolean showFailReason = true;
    public static boolean shortenLabel = false;

    public static int labelX = 0;
    public static int labelY = 0;

    public static void updateConfigFile() {
        setBool(CAT, "enabled", enabled);

        setBool(CAT, "showFailTick", showFailTick);
        setBool(CAT, "showFailReason", showFailReason);
        setBool(CAT, "shortenLabel", shortenLabel);

        setInt(CAT, "labelX", labelX);
        setInt(CAT, "labelY", labelY);
    }

    public static void updateFields() {
        enabled = getBool(CAT, "enabled", enabled);

        showFailTick = getBool(CAT, "showFailTick", showFailTick);
        showFailReason = getBool(CAT, "showFailReason", showFailReason);
        shortenLabel = getBool(CAT, "shortenLabel", shortenLabel);

        labelX = getInt(CAT, "labelX", labelX);
        labelY = getInt(CAT, "labelY", labelY);
    }
}