package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class TrajectoryConfig extends MainConfigSection {

    public static boolean enabled = false;

    public static float colorRed = 0;
    public static float colorGreen = 1;
    public static float colorBlue = 0;
    public static float opacity = 0.3f;
    public static float thickness = 0.05f;
    public static int tickLength = 50;

    @Override
    protected String getCategory() {
        return "trajectory";
    }

    @Override
    protected void init() {
        addBool("enabled", () -> enabled, v -> enabled = v);

        addDouble("colorRed", () -> (double) colorRed, v -> colorRed = v.floatValue());
        addDouble("colorGreen", () -> (double) colorGreen, v -> colorGreen = v.floatValue());
        addDouble("colorBlue", () -> (double) colorBlue, v -> colorBlue = v.floatValue());
        addDouble("opacity", () -> (double) opacity, v -> opacity = v.floatValue());
        addDouble("thickness", () -> (double) thickness, v -> thickness = v.floatValue());

        addInt("tickLength", () -> tickLength, v -> tickLength = v);
    }
}