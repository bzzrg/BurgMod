package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class TrajectoryConfig extends MainConfigSection {

    private static final String CAT = "trajectory";

    public static boolean enabled = false;

    public static float colorRed = 0;
    public static float colorGreen = 1;
    public static float colorBlue = 0;
    public static float alpha = 0.3f;
    public static float thickness = 0.05f;
    public static int tickLength = 50;

    @Override
    protected void init() {
        addBool(CAT, "enabled", () -> enabled, v -> enabled = v);

        addDouble(CAT, "colorRed", () -> (double) colorRed, v -> colorRed = v.floatValue());
        addDouble(CAT, "colorGreen", () -> (double) colorGreen, v -> colorGreen = v.floatValue());
        addDouble(CAT, "colorBlue", () -> (double) colorBlue, v -> colorBlue = v.floatValue());
        addDouble(CAT, "alpha", () -> (double) alpha, v -> alpha = v.floatValue());
        addDouble(CAT, "thickness", () -> (double) thickness, v -> thickness = v.floatValue());

        addInt(CAT, "tickLength", () -> tickLength, v -> tickLength = v);
    }
}