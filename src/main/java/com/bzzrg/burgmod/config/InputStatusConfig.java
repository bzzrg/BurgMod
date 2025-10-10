package com.bzzrg.burgmod.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class InputStatusConfig {

    public static String color1 = "\u00A76";
    public static String color2 = "\u00A7f";
    public static String[] strategy = new String[]{};
    public static boolean toggleSprintMode = false;
    public static boolean label = true;
    public static double labelX = 0d;
    public static double labelY = 0d;
    public static boolean shortenLabel = false;

    private static Configuration config;

    public static void load(File configFile) {
        try {
            config = new Configuration(configFile);
            config.load();

            color1 = config.getString("color1", "general", color1, "First color");
            color2 = config.getString("color2", "general", color2, "Second color");

            strategy = config.getStringList("strategy", "inputStatus", strategy, "Strategy");
            toggleSprintMode = config.getBoolean("toggleSprintMode", "inputStatus", toggleSprintMode, "Enable/disable toggle sprint mode for strategy");
            label = config.getBoolean("label", "inputStatus", label, "Enable/disable label");
            labelX = config.getFloat("labelX", "inputStatus", (float) labelX, -10000f, 10000f, "Label X position");
            labelY = config.getFloat("labelY", "inputStatus", (float) labelY, -10000f, 10000f, "Label Y position");
            shortenLabel = config.getBoolean("shortenLabel", "inputStatus", shortenLabel, "Enable/disable shortening the label");

        } catch (Exception e) {
            System.err.println("Error loading BurgMod config!");
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            config.get("general", "color1", color1).set(color1);
            config.get("general", "color2", color2).set(color2);

            config.get("inputStatus", "strategy", strategy).setValues(strategy);
            config.get("inputStatus", "toggleSprintMode", toggleSprintMode).setValue(toggleSprintMode);
            config.get("inputStatus", "label", label).setValue(label);
            config.get("inputStatus", "labelX", labelX).setValue(labelX);
            config.get("inputStatus", "labelY", labelY).setValue(labelY);
            config.get("inputStatus", "shortenLabel", shortenLabel).setValue(shortenLabel);

            if (config.hasChanged()) config.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
