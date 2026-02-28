package com.bzzrg.burgmod.config.specialconfig;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.features.poschecker.Axis;
import com.bzzrg.burgmod.features.poschecker.PosChecker;
import com.bzzrg.burgmod.utils.GeneralUtils;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class PosCheckersConfig {

    public static boolean enabled = true;
    public static double xMin = -10000;
    public static double xMax = 10000;
    public static double zMin = -10000;
    public static double zMax = 10000;
    public static final List<PosChecker> posCheckers = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File getConfigFile() {
        return new File(BurgMod.modConfigFolder, "pos_checker/config.json");
    }

    public static void updateJson() {
        try {
            File file = getConfigFile();
            GeneralUtils.createDirectory(file.getParentFile());

            JsonObject root = new JsonObject();
            root.addProperty("enabled", enabled);
            root.addProperty("xMin", xMin);
            root.addProperty("xMax", xMax);
            root.addProperty("zMin", zMin);
            root.addProperty("zMax", zMax);

            JsonArray array = new JsonArray();
            for (PosChecker pc : posCheckers) {
                JsonObject obj = new JsonObject();
                obj.addProperty("axis", pc.axis.name()); // "X", "Z", "BOTH"
                obj.addProperty("airtime", pc.airtime);
                array.add(obj);
            }
            root.add("posCheckers", array);

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(root, writer);
            }
        } catch (Exception e) {
            BurgMod.logger.error("Failed to write PosChecker config (likely disk or permission error)", e);
        }
    }

    public static void updateFields() {
        try {
            File file = getConfigFile();
            if (!file.exists()) return;

            FileReader reader = new FileReader(file);
            JsonObject root = GSON.fromJson(reader, JsonObject.class);

            enabled = root.get("enabled").getAsBoolean();
            xMin = root.get("xMin").getAsDouble();
            xMax = root.get("xMax").getAsDouble();
            zMin = root.get("zMin").getAsDouble();
            zMax = root.get("zMax").getAsDouble();

            posCheckers.clear();
            JsonArray array = root.getAsJsonArray("posCheckers");
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();

                Axis axis = Axis.valueOf(obj.get("axis").getAsString());
                int airtime = obj.get("airtime").getAsInt();

               posCheckers.add(
                        new PosChecker(axis, airtime)
                );
            }

        } catch (Exception e) {
            BurgMod.logger.error("Failed to read PosChecker config (likely externally modified or corrupted)", e);
        }
    }
}