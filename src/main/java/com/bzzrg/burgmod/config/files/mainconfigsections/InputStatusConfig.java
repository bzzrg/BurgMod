package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class InputStatusConfig extends MainConfigSection {

    private static final String CAT = "inputStatus";

    public static boolean enabled = false;

    public static boolean showFailTick = true;
    public static boolean showFailReason = true;
    public static boolean shortenLabel = false;

    public static int labelX = 0;
    public static int labelY = 0;

    @Override
    protected void init() {
        addBool(CAT, "enabled", () -> enabled, v -> enabled = v);
        addBool(CAT, "showFailTick", () -> showFailTick, v -> showFailTick = v);
        addBool(CAT, "showFailReason", () -> showFailReason, v -> showFailReason = v);
        addBool(CAT, "shortenLabel", () -> shortenLabel, v -> shortenLabel = v);
        addInt(CAT, "labelX", () -> labelX, v -> labelX = v);
        addInt(CAT, "labelY", () -> labelY, v -> labelY = v);
    }
}