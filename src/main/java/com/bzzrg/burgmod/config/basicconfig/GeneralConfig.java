package com.bzzrg.burgmod.config.basicconfig;

import static com.bzzrg.burgmod.config.ConfigHandler.config;

public class GeneralConfig {

    public static String color1 = "\u00A76";
    public static String color2 = "\u00A7f";
    public static int decimalPlaces = 5;
    public static boolean autoStrategyLoadOn = false;

    public static void updateConfigFile() {
        config.get("general", "color1", color1).set(color1);
        config.get("general", "color2", color2).set(color2);
        config.get("general", "autoStrategyLoadOn", autoStrategyLoadOn).setValue(autoStrategyLoadOn);
        config.get("general", "decimalPlaces", decimalPlaces).setValue(decimalPlaces);
    }

    public static void updateFields() {
        color1 = config.get("general", "color1", color1).getString();
        color2 = config.get("general", "color2", color2).getString();
        decimalPlaces = config.get("general", "decimalPlaces", decimalPlaces).getInt();
        autoStrategyLoadOn = config.get("general", "autoStrategyLoadOn", autoStrategyLoadOn).getBoolean();
    }

}
