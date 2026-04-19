package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class DistanceOffsetConfig extends MainConfigSection {

    public static boolean enabled = false;

    public static String axis = "Z";
    public static String titleWhenPositive = "&c&lDistance Made";
    public static boolean soundWhenPositive = true;
    public static float volumeOfSound = 1f;

    public static int labelX = 0;
    public static int labelY = 0;

    @Override
    protected String getCategory() {
        return "distanceOffset";
    }

    @Override
    protected void init() {
        addBool("enabled", () -> enabled, v -> enabled = v);

        addString("axis", () -> axis, v -> axis = v);
        addString("titleWhenPositive", () -> titleWhenPositive, v -> titleWhenPositive = v);
        addBool("soundWhenPositive", () -> soundWhenPositive, v -> soundWhenPositive = v);
        addDouble("volumeOfSound", () -> (double) volumeOfSound, v -> volumeOfSound = v.floatValue());

        addInt("labelX", () -> labelX, v -> labelX = v);
        addInt("labelY", () -> labelY, v -> labelY = v);
    }


}