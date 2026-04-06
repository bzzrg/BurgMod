package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class GeneralConfig extends MainConfigSection {

    public static String color1 = "\u00A76";
    public static String color2 = "\u00A7f";
    public static int decimalPrecision = 5;

    @Override
    protected String getCategory() {
        return "general";
    }

    @Override
    protected void init() {
        addString("color1", () -> color1, v -> color1 = v);
        addString("color2", () -> color2, v -> color2 = v);
        addInt("decimalPrecision", () -> decimalPrecision, v -> decimalPrecision = v);
    }


}