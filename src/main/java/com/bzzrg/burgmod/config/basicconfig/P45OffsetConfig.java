package com.bzzrg.burgmod.config.basicconfig;

import static com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler.*;

public class P45OffsetConfig {

    private static final String CAT = "perfect45Offset";

    public static boolean enabled = false;

    public static boolean showAutoOffset = true;
    public static boolean showXOffset = false;
    public static boolean showZOffset = false;
    public static boolean shortenLabels = false;

    public static boolean eNotation = false;
    public static int eNotationMaxExp = -3;
    public static int eNotationPrecision = 2;

    public static int numOf45s = 1;
    public static String jumpAngle = "";
    public static String fortyFiveKey = "A";

    public static boolean stopOnInputFail = true;
    public static boolean showOvershootAmount = false;
    public static boolean applyJAToFirst = false;
    public static boolean highlightLB = false;
    public static boolean trajectoryOnReset = false;

    public static int autoLabelX = 0;
    public static int autoLabelY = 0;

    public static int xLabelX = 0;
    public static int xLabelY = 0;

    public static int zLabelX = 0;
    public static int zLabelY = 0;

    public static void updateConfigFile() {

        setBool(CAT, "enabled", enabled);

        setBool(CAT, "showAutoOffset", showAutoOffset);
        setBool(CAT, "showXOffset", showXOffset);
        setBool(CAT, "showZOffset", showZOffset);
        setBool(CAT, "shortenLabels", shortenLabels);

        setBool(CAT, "eNotation", eNotation);
        setInt(CAT, "eNotationMaxExp", eNotationMaxExp);
        setInt(CAT, "eNotationPrecision", eNotationPrecision);

        setInt(CAT, "numOf45s", numOf45s);
        setString(CAT, "jumpAngle", jumpAngle);
        setString(CAT, "fortyFiveKey", fortyFiveKey);

        setBool(CAT, "stopOnInputFail", stopOnInputFail);
        setBool(CAT, "showOvershootAmount", showOvershootAmount);
        setBool(CAT, "applyJAToFirst", applyJAToFirst);
        setBool(CAT, "highlightLB", highlightLB);
        setBool(CAT, "trajectoryOnReset", trajectoryOnReset);

        setInt(CAT, "autoLabelX", autoLabelX);
        setInt(CAT, "autoLabelY", autoLabelY);

        setInt(CAT, "xLabelX", xLabelX);
        setInt(CAT, "xLabelY", xLabelY);

        setInt(CAT, "zLabelX", zLabelX);
        setInt(CAT, "zLabelY", zLabelY);
    }

    public static void updateFields() {

        enabled = getBool(CAT, "enabled", enabled);

        showAutoOffset = getBool(CAT, "showAutoOffset", showAutoOffset);
        showXOffset = getBool(CAT, "showXOffset", showXOffset);
        showZOffset = getBool(CAT, "showZOffset", showZOffset);
        shortenLabels = getBool(CAT, "shortenLabels", shortenLabels);

        eNotation = getBool(CAT, "eNotation", eNotation);
        eNotationMaxExp = getInt(CAT, "eNotationMaxExp", eNotationMaxExp);
        eNotationPrecision = getInt(CAT, "eNotationPrecision", eNotationPrecision);

        numOf45s = getInt(CAT, "numOf45s", numOf45s);
        jumpAngle = getString(CAT, "jumpAngle", jumpAngle);
        fortyFiveKey = getString(CAT, "fortyFiveKey", fortyFiveKey);

        stopOnInputFail = getBool(CAT, "stopOnInputFail", stopOnInputFail);
        showOvershootAmount = getBool(CAT, "showOvershootAmount", showOvershootAmount);
        applyJAToFirst = getBool(CAT, "applyJAToFirst", applyJAToFirst);
        highlightLB = getBool(CAT, "highlightLB", highlightLB);
        trajectoryOnReset = getBool(CAT, "trajectoryOnReset", trajectoryOnReset);

        autoLabelX = getInt(CAT, "autoLabelX", autoLabelX);
        autoLabelY = getInt(CAT, "autoLabelY", autoLabelY);

        xLabelX = getInt(CAT, "xLabelX", xLabelX);
        xLabelY = getInt(CAT, "xLabelY", xLabelY);

        zLabelX = getInt(CAT, "zLabelX", zLabelX);
        zLabelY = getInt(CAT, "zLabelY", zLabelY);
    }

    public static Float getJumpAngle() {
        try {
            return Float.parseFloat(jumpAngle);
        } catch (Exception e) {
            return null;
        }
    }
}