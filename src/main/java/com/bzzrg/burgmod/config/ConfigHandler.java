package com.bzzrg.burgmod.config;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.basicconfig.Perfect45OffsetConfig;
import com.bzzrg.burgmod.config.basicconfig.GeneralConfig;
import com.bzzrg.burgmod.config.basicconfig.InputStatusConfig;
import com.bzzrg.burgmod.config.basicconfig.TrajectoryConfig;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static Configuration config;

    public static void updateConfigFile() { // fields are generally updated through guis, so this should be called on gui close
        try {
            GeneralConfig.updateConfigFile();
            InputStatusConfig.updateConfigFile();
            Perfect45OffsetConfig.updateConfigFile();
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
            Perfect45OffsetConfig.updateFields();
            TrajectoryConfig.updateFields();

        } catch (Exception e) {
            BurgMod.logger.error("Error updating fields for BurgMod", e);
        }
    }




}
