package com.bzzrg.burgmod.config.featureconfig;

import static com.bzzrg.burgmod.config.ConfigHandler.config;

public class TrajectoryConfig {

    public static boolean enabled = false;

    public static float colorRed = 0;
    public static float colorGreen = 1;
    public static float colorBlue = 0;
    public static float alpha = 0.3f;
    public static float thickness = 0.05f;
    public static int tickLength = 50;

    public static void updateConfigFromFields() {
        config.get("trajectory", "enabled", enabled).setValue(enabled);

        config.get("trajectory", "colorRed", colorRed).setValue(colorRed);
        config.get("trajectory", "colorGreen", colorGreen).setValue(colorGreen);
        config.get("trajectory", "colorBlue", colorBlue).setValue(colorBlue);
        config.get("trajectory", "alpha", alpha).setValue(alpha);
        config.get("trajectory", "thickness", thickness).setValue(thickness);
        config.get("trajectory", "tickLength", tickLength).setValue(tickLength);

    }

    public static void updateFieldsFromConfig() {
        enabled = config.get("trajectory", "enabled", enabled).getBoolean();

        colorRed = (float) config.get("trajectory", "colorRed", colorRed).getDouble();
        colorGreen = (float) config.get("trajectory", "colorGreen", colorGreen).getDouble();
        colorBlue = (float) config.get("trajectory", "colorBlue", colorBlue).getDouble();
        alpha = (float) config.get("trajectory", "alpha", alpha).getDouble();
        thickness = (float) config.get("trajectory", "thickness", thickness).getDouble();
        tickLength = config.get("trajectory", "tickLength", tickLength).getInt();
    }

}
