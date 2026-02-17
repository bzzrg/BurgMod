package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.featureconfig.FortyFiveStatusConfig;
import com.bzzrg.burgmod.config.featureconfig.InputStatusConfig;
import com.bzzrg.burgmod.config.featureconfig.TrajectoryConfig;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static String color1 = "\u00A76";
    public static String color2 = "\u00A7f";
    public static boolean autoStrategyLoadOn = false;

    public static Configuration config;

    public static void updateConfigFromFields() { // fields are generally updated through guis, so this should be called on gui close
        try {
            config.get("general", "color1", color1).set(color1);
            config.get("general", "color2", color2).set(color2);
            config.get("general", "autoStrategyLoadOn", autoStrategyLoadOn).setValue(autoStrategyLoadOn);

            InputStatusConfig.updateConfigFromFields();
            FortyFiveStatusConfig.updateConfigFromFields();
            TrajectoryConfig.updateConfigFromFields();

            if (config.hasChanged()) {
                config.save();
            }

        } catch (Exception e) {
            BurgMod.logger.error("Error updating config from fields for BurgMod", e);
        }
    }

    public static void updateFieldsFromConfig() { // calls once on startup to prep fields
        try {
            config = new Configuration(BurgMod.modConfigFile);
            config.load();

            color1 = config.get("general", "color1", color1, "").getString();
            color2 = config.get("general", "color2", color2, "").getString();
            autoStrategyLoadOn = config.get("general", "autoStrategyLoadOn", autoStrategyLoadOn, "").getBoolean();

            InputStatusConfig.updateFieldsFromConfig();
            FortyFiveStatusConfig.updateFieldsFromConfig();
            TrajectoryConfig.updateFieldsFromConfig();

        } catch (Exception e) {
            BurgMod.logger.error("Error updating fields from config for BurgMod", e);
        }
    }




}
