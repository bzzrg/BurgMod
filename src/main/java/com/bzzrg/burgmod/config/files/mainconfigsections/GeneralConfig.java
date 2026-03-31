package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class GeneralConfig extends MainConfigSection {

    private static final String CAT = "general";

    public static String color1 = "\u00A76";
    public static String color2 = "\u00A7f";
    public static int decimalPrecision = 5;

    @Override
    protected void init() {
        addString(CAT, "color1", () -> color1, v -> color1 = v);
        addString(CAT, "color2", () -> color2, v -> color2 = v);
        addInt(CAT, "decimalPrecision", () -> decimalPrecision, v -> decimalPrecision = v);
    }
}