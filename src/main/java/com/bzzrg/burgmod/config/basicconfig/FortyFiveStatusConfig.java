package com.bzzrg.burgmod.config.basicconfig;

import static com.bzzrg.burgmod.config.ConfigHandler.config;

public class FortyFiveStatusConfig {

    public static boolean enabled = false;

    public static void updateConfigFile() {
        config.get("fortyFiveStatus", "enabled", enabled).setValue(enabled);
    }

    public static void updateFields() {
        enabled = config.get("fortyFiveStatus", "enabled", enabled, "").getBoolean();
    }

}
