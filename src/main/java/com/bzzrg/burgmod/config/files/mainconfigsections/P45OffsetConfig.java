package com.bzzrg.burgmod.config.files.mainconfigsections;

import com.bzzrg.burgmod.config.files.utils.MainConfigSection;

public class P45OffsetConfig extends MainConfigSection {

    private static final String CAT = "perfect45Offset";

    public static boolean enabled = false;

    public static int numOf45s = 1;
    public static String jumpAngle = "";
    public static boolean applyJAToFirst = false;
    public static String fortyFiveKey = "A";

    public static boolean showAutoOffset = true;
    public static boolean showXOffset = false;
    public static boolean showZOffset = false;
    public static boolean shortenLabels = false;
    public static boolean eNotation = false;
    public static int eNotationMaxExp = -3;
    public static int eNotationPrecision = 2;

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
    protected void init() {
        addBool(CAT, "enabled", () -> enabled, v -> enabled = v);

        addInt(CAT, "numOf45s", () -> numOf45s, v -> numOf45s = v);
        addString(CAT, "jumpAngle", () -> jumpAngle, v -> jumpAngle = v);
        addBool(CAT, "applyJAToFirst", () -> applyJAToFirst, v -> applyJAToFirst = v);
        addString(CAT, "fortyFiveKey", () -> fortyFiveKey, v -> fortyFiveKey = v);

        addBool(CAT, "showAutoOffset", () -> showAutoOffset, v -> showAutoOffset = v);
        addBool(CAT, "showXOffset", () -> showXOffset, v -> showXOffset = v);
        addBool(CAT, "showZOffset", () -> showZOffset, v -> showZOffset = v);
        addBool(CAT, "shortenLabels", () -> shortenLabels, v -> shortenLabels = v);
        addBool(CAT, "eNotation", () -> eNotation, v -> eNotation = v);
        addInt(CAT, "eNotationMaxExp", () -> eNotationMaxExp, v -> eNotationMaxExp = v);
        addInt(CAT, "eNotationPrecision", () -> eNotationPrecision, v -> eNotationPrecision = v);

        addBool(CAT, "stopOnInputFail", () -> stopOnInputFail, v -> stopOnInputFail = v);
        addBool(CAT, "showOvershootAmount", () -> showOvershootAmount, v -> showOvershootAmount = v);
        addBool(CAT, "showLB", () -> showLB, v -> showLB = v);
        addBool(CAT, "showJumpBlock", () -> showJumpBlock, v -> showJumpBlock = v);
        addBool(CAT, "showJBLBLine", () -> showJBLBLine, v -> showJBLBLine = v);
        addBool(CAT, "showPerfectLine", () -> showPerfectLine, v -> showPerfectLine = v);

        addInt(CAT, "autoLabelX", () -> autoLabelX, v -> autoLabelX = v);
        addInt(CAT, "autoLabelY", () -> autoLabelY, v -> autoLabelY = v);

        addInt(CAT, "xLabelX", () -> xLabelX, v -> xLabelX = v);
        addInt(CAT, "xLabelY", () -> xLabelY, v -> xLabelY = v);

        addInt(CAT, "zLabelX", () -> zLabelX, v -> zLabelX = v);
        addInt(CAT, "zLabelY", () -> zLabelY, v -> zLabelY = v);
    }

    public static Float getJumpAngle() {
        try { return Float.parseFloat(jumpAngle); }
        catch (Exception e) { return null; }
    }
}