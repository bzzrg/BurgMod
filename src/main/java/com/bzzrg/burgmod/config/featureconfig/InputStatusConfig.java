package com.bzzrg.burgmod.config.featureconfig;

import static com.bzzrg.burgmod.config.ConfigHandler.config;

public class InputStatusConfig {

    public static boolean enabled = false;

    public static int labelX = 0;
    public static int labelY = 0;
    public static boolean shortenLabel = false;

    public static void updateConfigFromFields() {
        config.get("inputStatus", "enabled", enabled).setValue(enabled);
        config.get("inputStatus", "labelX", labelX).setValue(labelX);
        config.get("inputStatus", "labelY", labelY).setValue(labelY);
        config.get("inputStatus", "shortenLabel", shortenLabel).setValue(shortenLabel);
    }

    public static void updateFieldsFromConfig() {
        enabled = config.get("inputStatus", "enabled", enabled).getBoolean();
        labelX = config.get("inputStatus", "labelX", labelX).getInt();
        labelY = config.get("inputStatus", "labelY", labelY).getInt();
        shortenLabel = config.get("inputStatus", "shortenLabel", shortenLabel).getBoolean();
    }

}
