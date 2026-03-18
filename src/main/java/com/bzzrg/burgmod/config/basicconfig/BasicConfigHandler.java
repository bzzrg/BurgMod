package com.bzzrg.burgmod.config.basicconfig;

import com.bzzrg.burgmod.BurgMod;
import net.minecraftforge.common.config.Configuration;

public class BasicConfigHandler {

    public static Configuration config;

    public static void updateConfigFile() { // fields are generally updated through guis, so this should be called on gui close
        try {
            GeneralConfig.updateConfigFile();
            InputStatusConfig.updateConfigFile();
            P45OffsetConfig.updateConfigFile();
            TrajectoryConfig.updateConfigFile();

            if (config.hasChanged()) config.save(); // saves changes to Configuration object to the actual file

        } catch (Exception e) {
            BurgMod.logger.error("Error updating config file for BurgMod", e);
        }
    }

    public static void updateFields() { // calls once on startup to prep fields
        try {
            config = new Configuration(BurgMod.modConfigFile);
            config.load();

            GeneralConfig.updateFields();
            InputStatusConfig.updateFields();
            P45OffsetConfig.updateFields();
            TrajectoryConfig.updateFields();

        } catch (Exception e) {
            BurgMod.logger.error("Error updating fields for BurgMod", e);
        }
    }

    // ===== READ =====
    public static boolean getBool(String cat, String key, boolean def) {
        return config.get(cat, key, def).getBoolean();
    }
    public static int getInt(String cat, String key, int def) {
        return config.get(cat, key, def).getInt();
    }
    public static float getFloat(String cat, String key, float def) {
        return (float) config.get(cat, key, def).getDouble();
    }
    public static double getDouble(String cat, String key, double def) {
        return config.get(cat, key, def).getDouble();
    }
    public static String getString(String cat, String key, String def) {
        return config.get(cat, key, def).getString();
    }


    // ===== WRITE =====
    public static void setBool(String cat, String key, boolean val) {
        config.get(cat, key, val).setValue(val);
    }
    public static void setInt(String cat, String key, int val) {
        config.get(cat, key, val).setValue(val);
    }
    public static void setFloat(String cat, String key, float val) {
        config.get(cat, key, val).setValue(val);
    }
    public static void setDouble(String cat, String key, double val) {
        config.get(cat, key, val).setValue(val);
    }
    public static void setString(String cat, String key, String val) {
        config.get(cat, key, val).set(val);
    }

}
