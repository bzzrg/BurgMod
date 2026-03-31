package com.bzzrg.burgmod.config.files.jsonconfigfiles;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.files.utils.JsonConfigFile;
import com.bzzrg.burgmod.config.files.utils.ListConvertor;
import com.bzzrg.burgmod.features.poschecker.Axis;
import com.bzzrg.burgmod.features.poschecker.PosChecker;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PosCheckersConfig extends JsonConfigFile {

    public static boolean enabled = true;

    public static double xMin = -10000;
    public static double xMax = 10000;
    public static double zMin = -10000;
    public static double zMax = 10000;

    public static List<PosChecker> posCheckers = new ArrayList<>();

    private PosCheckersConfig(File file) {
        super(file);
    }

    @Override
    protected void init() {

        addBool("enabled", () -> enabled, v -> enabled = v);

        addDouble("xMin", () -> xMin, v -> xMin = v);
        addDouble("xMax", () -> xMax, v -> xMax = v);
        addDouble("zMin", () -> zMin, v -> zMin = v);
        addDouble("zMax", () -> zMax, v -> zMax = v);

        addList("posCheckers", () -> posCheckers, v -> posCheckers = v, convertor);
    }

    private static final ListConvertor<PosChecker> convertor = new ListConvertor<PosChecker>() {

        @Override
        public JsonElement toJson(PosChecker posChecker) {
            JsonObject obj = new JsonObject();
            obj.addProperty("axis", posChecker.axis.name());
            obj.addProperty("airtime", posChecker.airtime);
            return obj;
        }

        @Override
        public PosChecker fromJson(JsonElement element) {
            JsonObject obj = element.getAsJsonObject();
            Axis axis = Axis.valueOf(obj.get("axis").getAsString());
            int airtime = obj.get("airtime").getAsInt();
            return new PosChecker(axis, airtime);
        }
    };

    public static final PosCheckersConfig instance = new PosCheckersConfig(new File(BurgMod.modConfigFolder, "pos-checkers.json"));

}