package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class InputStatusConfig extends MainConfigSection {

    public static boolean enabled = false;

    public static boolean showFailTick = true;
    public static boolean showFailReason = true;
    public static boolean shortenLabel = false;

    public static int labelX = 0;
    public static int labelY = 0;

    @Override
    protected String getCategory() {
        return "inputStatus";
    }

    @Override
    protected void init() {
        addBool("enabled", () -> enabled, v -> enabled = v);

        addBool("showFailTick", () -> showFailTick, v -> showFailTick = v);
        addBool("showFailReason", () -> showFailReason, v -> showFailReason = v);
        addBool("shortenLabel", () -> shortenLabel, v -> shortenLabel = v);

        addInt("labelX", () -> labelX, v -> labelX = v);
        addInt("labelY", () -> labelY, v -> labelY = v);
    }
}