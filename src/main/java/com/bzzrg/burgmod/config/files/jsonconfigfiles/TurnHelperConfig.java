package com.bzzrg.burgmod.config.files.jsonconfigfiles;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.files.utils.JsonConfigFile;
import com.bzzrg.burgmod.config.files.utils.ListConvertor;
import com.bzzrg.burgmod.features.turnhelper.YawPoint;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TurnHelperConfig extends JsonConfigFile {

    public static boolean enabled = false;

    public static String mode = "ONE_MOVING_TARGET";
    public static boolean deltaYaws = true;
    public static boolean showTurnAccuracy = false;

    public static float colorRed = 0.68f;
    public static float colorGreen = 0.85f;
    public static float colorBlue = 0.9f;
    public static float opacity = 0.5f;
    public static float yawPlusMinus = 1f;

    public static List<YawPoint> yawPoints = new ArrayList<>();

    public static int turnAccuracyLabelX = 0;
    public static int turnAccuracyLabelY = 0;

    private TurnHelperConfig(File file) {
        super(file);
    }

    @Override
    protected void init() {

        addBool("enabled", () -> enabled, v -> enabled = v);

        addString("mode", () -> mode, v -> mode = v);
        addBool("deltaYaws", () -> deltaYaws, v -> deltaYaws = v);
        addBool("showTurnAccuracy", () -> showTurnAccuracy, v -> showTurnAccuracy = v);

        addDouble("colorRed", () -> (double) colorRed, v -> colorRed = v.floatValue());
        addDouble("colorGreen", () -> (double) colorGreen, v -> colorGreen = v.floatValue());
        addDouble("colorBlue", () -> (double) colorBlue, v -> colorBlue = v.floatValue());
        addDouble("opacity", () -> (double) opacity, v -> opacity = v.floatValue());
        addDouble("yawPlusMinus", () -> (double) yawPlusMinus, v -> yawPlusMinus = v.floatValue());

        addList("yawPoints", () -> yawPoints, v -> yawPoints = v, convertor);

        addInt("turnAccuracyLabelX", () -> turnAccuracyLabelX, v -> turnAccuracyLabelX = v);
        addInt("turnAccuracyLabelY", () -> turnAccuracyLabelY, v -> turnAccuracyLabelY = v);
    }

    private static final ListConvertor<YawPoint> convertor = new ListConvertor<YawPoint>() {

        @Override
        public JsonElement toJson(YawPoint yawPoint) {
            JsonObject obj = new JsonObject();
            obj.addProperty("yaw", yawPoint.yaw);
            obj.addProperty("tickNum", yawPoint.tickNum);
            return obj;
        }

        @Override
        public YawPoint fromJson(JsonElement element) {
            JsonObject obj = element.getAsJsonObject();
            float yaw = obj.get("yaw").getAsFloat();
            int tickNum = obj.get("tickNum").getAsInt();
            return new YawPoint(yaw, tickNum);
        }
    };

    public static final TurnHelperConfig instance = new TurnHelperConfig(new File(BurgMod.modConfigFolder, "turn-helper.json"));

}