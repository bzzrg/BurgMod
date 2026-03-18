package com.bzzrg.burgmod.config.basicconfig;

import static com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler.*;

public class TrajectoryConfig {

    private static final String CAT = "trajectory";

    public static boolean enabled = false;

    public static float colorRed = 0;
    public static float colorGreen = 1;
    public static float colorBlue = 0;
    public static float alpha = 0.3f;
    public static float thickness = 0.05f;
    public static int tickLength = 50;

    public static void updateConfigFile() {
        setBool(CAT, "enabled", enabled);

        setFloat(CAT, "colorRed", colorRed);
        setFloat(CAT, "colorGreen", colorGreen);
        setFloat(CAT, "colorBlue", colorBlue);
        setFloat(CAT, "alpha", alpha);
        setFloat(CAT, "thickness", thickness);
        setInt(CAT, "tickLength", tickLength);
    }

    public static void updateFields() {
        enabled = getBool(CAT, "enabled", enabled);

        colorRed = getFloat(CAT, "colorRed", colorRed);
        colorGreen = getFloat(CAT, "colorGreen", colorGreen);
        colorBlue = getFloat(CAT, "colorBlue", colorBlue);
        alpha = getFloat(CAT, "alpha", alpha);
        thickness = getFloat(CAT, "thickness", thickness);
        tickLength = getInt(CAT, "tickLength", tickLength);
    }
}