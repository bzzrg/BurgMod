package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class DistanceOffsetConfig extends MainConfigSection {

    public static boolean enabled = false;

    public static String axis = "Z";

    public static boolean titleWhenPositive = true;
    public static String titleTextPositive = "&c&lDistance Made";
    public static boolean soundWhenPositive = true;
    public static float soundVolumePositive = 1f;

    public static boolean titleWhenLand = true;
    public static String titleTextLand = "&6&lLanded";
    public static boolean soundWhenLand = true;
    public static float soundVolumeLand = 1f;

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

        addBool("titleWhenLand", () -> titleWhenLand, v -> titleWhenLand = v);
        addString("titleTextPositive", () -> titleTextPositive, v -> titleTextPositive = v);
        addBool("soundWhenPositive", () -> soundWhenPositive, v -> soundWhenPositive = v);
        addDouble("soundVolumePositive", () -> (double) soundVolumePositive, v -> soundVolumePositive = v.floatValue());

        addBool("titleWhenPositive", () -> titleWhenPositive, v -> titleWhenPositive = v);
        addString("titleTextLand", () -> titleTextLand, v -> titleTextLand = v);
        addBool("soundWhenLand", () -> soundWhenLand, v -> soundWhenLand = v);
        addDouble("soundVolumeLand", () -> (double) soundVolumeLand, v -> soundVolumeLand = v.floatValue());

        addInt("labelX", () -> labelX, v -> labelX = v);
        addInt("labelY", () -> labelY, v -> labelY = v);
    }


}