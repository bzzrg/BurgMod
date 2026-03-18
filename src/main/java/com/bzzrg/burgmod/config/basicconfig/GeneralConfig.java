package com.bzzrg.burgmod.config.basicconfig;

import static com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler.*;

public class GeneralConfig {

    private static final String CAT = "general";

    public static String color1 = "\u00A76";
    public static String color2 = "\u00A7f";
    public static int decimalPrecision = 5;
    public static boolean autoStrategyLoadOn = false;

    public static void updateConfigFile() {
        setString(CAT, "color1", color1);
        setString(CAT, "color2", color2);
        setBool(CAT, "autoStrategyLoadOn", autoStrategyLoadOn);
        setInt(CAT, "decimalPrecision", decimalPrecision);
    }

    public static void updateFields() {
        color1 = getString(CAT, "color1", color1);
        color2 = getString(CAT, "color2", color2);
        decimalPrecision = getInt(CAT, "decimalPrecision", decimalPrecision);
        autoStrategyLoadOn = getBool(CAT, "autoStrategyLoadOn", autoStrategyLoadOn);
    }
}