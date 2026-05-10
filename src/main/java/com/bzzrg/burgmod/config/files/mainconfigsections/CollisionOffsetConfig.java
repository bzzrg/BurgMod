package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class CollisionOffsetConfig extends MainConfigSection {

    public static boolean enabled = false;

    public static boolean showXLabel = true;
    public static boolean showZLabel = true;

    public static int xLabelX = 0;
    public static int xLabelY = 0;

    public static int zLabelX = 0;
    public static int zLabelY = 0;

    @Override
    protected String getCategory() {
        return "collisionOffset";
    }

    @Override
    protected void init() {
        addBool("enabled", () -> enabled, v -> enabled = v);

        addBool("showXLabel", () -> showXLabel, v -> showXLabel = v);
        addBool("showZLabel", () -> showZLabel, v -> showZLabel = v);

        addInt("xLabelX", () -> xLabelX, v -> xLabelX = v);
        addInt("xLabelY", () -> xLabelY, v -> xLabelY = v);

        addInt("zLabelX", () -> zLabelX, v -> zLabelX = v);
        addInt("zLabelY", () -> zLabelY, v -> zLabelY = v);
    }


}