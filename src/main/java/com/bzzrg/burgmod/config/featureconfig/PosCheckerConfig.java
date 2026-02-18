package com.bzzrg.burgmod.config.featureconfig;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.features.poschecker.PosCheckerHandler;
import com.bzzrg.burgmod.utils.PluginUtils;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PosCheckerConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static File getConfigFile() {
        return new File(BurgMod.modConfigFolder, "pos_checker/config.json");
    }

    public static void updateJson() {
        try {
            File file = getConfigFile();
            PluginUtils.createDirectory(file.getParentFile());

            JsonArray array = new JsonArray();
            for (PosCheckerHandler.PosChecker pc : PosCheckerHandler.posCheckers) {
                JsonObject obj = new JsonObject();
                obj.addProperty("axis", pc.axis.name()); // "X", "Z", "BOTH"
                obj.addProperty("ticksAfterJump", pc.ticksAfterJump);
                array.add(obj);
            }

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(array, writer);
            }
        } catch (Exception e) {
            BurgMod.logger.error("Failed to write PosChecker config (likely disk or permission error)", e);
        }
    }

    public static void updateFields() {
        try {
            File file = getConfigFile();
            if (!file.exists()) return;

            try (FileReader reader = new FileReader(file)) {
                JsonArray array = GSON.fromJson(reader, JsonArray.class);

                PosCheckerHandler.posCheckers.clear();
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();

                    PosCheckerHandler.Axis axis =
                            PosCheckerHandler.Axis.valueOf(obj.get("axis").getAsString());
                    int ticksAfterJump = obj.get("ticksAfterJump").getAsInt();

                    PosCheckerHandler.posCheckers.add(
                            new PosCheckerHandler.PosChecker(axis, ticksAfterJump)
                    );
                }
            }
        } catch (Exception e) {
            BurgMod.logger.error("Failed to read PosChecker config (likely externally modified or corrupted)", e);
        }
    }
}
