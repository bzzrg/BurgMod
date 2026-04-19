package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class P45OffsetConfig extends MainConfigSection {

    public static boolean enabled = false;

    public static int numOf45s = 1;
    public static String jumpAngle = "";
    public static boolean applyJAToFirst = false;
    public static String fortyFiveKey = "A";

    public static boolean showAutoOffset = true;
    public static boolean showXOffset = false;
    public static boolean showZOffset = false;
    public static boolean shortenLabels = false;

    public static boolean stopOnInputFail = true;
    public static boolean showOvershootAmount = false;
    public static boolean showLB = false;
    public static boolean showJumpBlock = false;
    public static boolean showJBLBLine = false;
    public static boolean showPerfectLine = false;

    public static int autoLabelX = 0;
    public static int autoLabelY = 0;

    public static int xLabelX = 0;
    public static int xLabelY = 0;

    public static int zLabelX = 0;
    public static int zLabelY = 0;

    @Override
    protected String getCategory() {
        return "perfect45Offset";
    }

    @Override
    protected void init() {
        addBool("enabled", () -> enabled, v -> enabled = v);

        addInt("numOf45s", () -> numOf45s, v -> numOf45s = v);
        addString("jumpAngle", () -> jumpAngle, v -> jumpAngle = v);
        addBool("applyJAToFirst", () -> applyJAToFirst, v -> applyJAToFirst = v);
        addString("fortyFiveKey", () -> fortyFiveKey, v -> fortyFiveKey = v);

        addBool("showAutoOffset", () -> showAutoOffset, v -> showAutoOffset = v);
        addBool("showXOffset", () -> showXOffset, v -> showXOffset = v);
        addBool("showZOffset", () -> showZOffset, v -> showZOffset = v);
        addBool("shortenLabels", () -> shortenLabels, v -> shortenLabels = v);

        addBool("stopOnInputFail", () -> stopOnInputFail, v -> stopOnInputFail = v);
        addBool("showOvershootAmount", () -> showOvershootAmount, v -> showOvershootAmount = v);
        addBool("showLB", () -> showLB, v -> showLB = v);
        addBool("showJumpBlock", () -> showJumpBlock, v -> showJumpBlock = v);
        addBool("showJBLBLine", () -> showJBLBLine, v -> showJBLBLine = v);
        addBool("showPerfectLine", () -> showPerfectLine, v -> showPerfectLine = v);

        addInt("autoLabelX", () -> autoLabelX, v -> autoLabelX = v);
        addInt("autoLabelY", () -> autoLabelY, v -> autoLabelY = v);

        addInt("xLabelX", () -> xLabelX, v -> xLabelX = v);
        addInt("xLabelY", () -> xLabelY, v -> xLabelY = v);

        addInt("zLabelX", () -> zLabelX, v -> zLabelX = v);
        addInt("zLabelY", () -> zLabelY, v -> zLabelY = v);
    }

    public static Float getJumpAngle() {
        try { return Float.parseFloat(jumpAngle); }
        catch (Exception e) { return null; }
    }
}