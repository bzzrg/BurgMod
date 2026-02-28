package com.bzzrg.burgmod.config.basicconfig;

import static com.bzzrg.burgmod.config.ConfigHandler.config;

public class InputStatusConfig {

    public static boolean enabled = false;

    public static boolean showFailTick = false;
    public static boolean showFailReason = false;
    public static boolean shortenLabel = false;

    public static int labelX = 0;
    public static int labelY = 0;

    public static void updateConfigFile() {
        config.get("inputStatus", "enabled", enabled).setValue(enabled);

        config.get("inputStatus", "showFailTick", showFailTick).setValue(showFailTick);
        config.get("inputStatus", "showFailReason", showFailReason).setValue(showFailReason);
        config.get("inputStatus", "shortenLabel", shortenLabel).setValue(shortenLabel);

        config.get("inputStatus", "labelX", labelX).setValue(labelX);
        config.get("inputStatus", "labelY", labelY).setValue(labelY);
    }

    public static void updateFields() {
        enabled = config.get("inputStatus", "enabled", enabled).getBoolean();

        showFailTick = config.get("inputStatus", "showFailTick", showFailTick).getBoolean();
        showFailReason = config.get("inputStatus", "showFailReason", showFailReason).getBoolean();
        shortenLabel = config.get("inputStatus", "shortenLabel", shortenLabel).getBoolean();

        labelX = config.get("inputStatus", "labelX", labelX).getInt();
        labelY = config.get("inputStatus", "labelY", labelY).getInt();
    }

}
