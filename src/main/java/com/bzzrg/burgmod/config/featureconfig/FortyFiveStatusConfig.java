package com.bzzrg.burgmod.config.featureconfig;

import static com.bzzrg.burgmod.config.ConfigHandler.config;

public class FortyFiveStatusConfig {

    public static boolean enabled = false;

    public static void updateConfigFromFields() {
        config.get("fortyFiveStatus", "enabled", enabled).setValue(enabled);
    }

    public static void updateFieldsFromConfig() {
        enabled = config.get("fortyFiveStatus", "enabled", enabled, "").getBoolean();
    }

}
