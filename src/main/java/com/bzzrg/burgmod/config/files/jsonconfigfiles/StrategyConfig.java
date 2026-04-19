package com.bzzrg.burgmod.config.files.jsonconfigfiles;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.files.utils.JsonConfigFile;
import com.bzzrg.burgmod.config.files.utils.JsonConvertor;
import com.bzzrg.burgmod.features.strategy.*;
import com.google.gson.*;

import java.io.File;
import java.util.*;

import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.modutils.GeneralUtils.playErrorSound;

public class StrategyConfig extends JsonConfigFile {

    public static boolean autoLoadHPK = false;

    public static final List<StrategyTick> strategyTicks = new ArrayList<>();
    public static final List<StrategyJump> strategyJumps = new ArrayList<>();

    public static boolean showTickNum = false;
    public static int tickNumLabelX = 0;
    public static int tickNumLabelY = 0;

    private StrategyConfig(File file) {
        super(file);
    }

    @Override
    protected void init() {
        addBool("autoLoadHPK", () -> autoLoadHPK, v -> autoLoadHPK = v);
        addJson("strategy", convertor);
    }

    public static final JsonConvertor convertor = new JsonConvertor() {

        @Override
        public JsonElement getJson() {

            JsonArray strategyArray = new JsonArray();
            List<StrategyJump> addedJumps = new ArrayList<>();

            for (StrategyTick tick : strategyTicks) {

                if (tick.jump == null) {

                    JsonArray inputs = new JsonArray();
                    tick.correctInputs.forEach(i -> inputs.add(new JsonPrimitive(i.name())));
                    strategyArray.add(inputs);

                } else if (!addedJumps.contains(tick.jump)) {

                    addedJumps.add(tick.jump);

                    JsonObject jumpJson = new JsonObject();

                    jumpJson.addProperty("type", tick.jump.type.name());

                    if (tick.jump.wasdDirections != null) {
                        JsonArray dirs = new JsonArray();
                        tick.jump.wasdDirections.forEach(d -> dirs.add(new JsonPrimitive(d.name())));
                        jumpJson.add("directions", dirs);

                    } else if (tick.jump.adDirection != null) {
                        jumpJson.addProperty("direction", tick.jump.adDirection.name());
                    }

                    if (tick.jump.length != null) {
                        jumpJson.addProperty("length", tick.jump.length);
                    }

                    jumpJson.addProperty("run1T", tick.jump.run1T);

                    jumpJson.addProperty("ceilingHeight", tick.jump.ceilingHeight.name());

                    JsonArray jumpTicks = new JsonArray();

                    for (StrategyTick jt : tick.jump.ticks) {
                        JsonArray inputs = new JsonArray();
                        jt.correctInputs.forEach(i -> inputs.add(new JsonPrimitive(i.name())));
                        jumpTicks.add(inputs);
                    }

                    jumpJson.add("ticks", jumpTicks);

                    strategyArray.add(jumpJson);
                }
            }

            return strategyArray;
        }

        @Override
        public void setFields(JsonElement element) {

            StrategyListGui.clearStrategy();
            strategyTicks.clear();
            strategyJumps.clear();

            JsonArray strategyArray = element.getAsJsonArray();

            try {
                for (JsonElement elem : strategyArray) {

                    if (elem.isJsonArray()) {

                        Set<InputType> inputs = new HashSet<>();
                        elem.getAsJsonArray().forEach(e -> inputs.add(InputType.valueOf(e.getAsString())));

                        new StrategyTick(strategyTicks.size(), inputs, null);

                    } else if (elem.isJsonObject()) {

                        JsonObject jumpJson = elem.getAsJsonObject();

                        JumpType type = JumpType.valueOf(jumpJson.get("type").getAsString());
                        StrategyJump jump = new StrategyJump(strategyJumps.size(), type);

                        if (jumpJson.has("directions")) {
                            jump.wasdDirections.clear();
                            for (JsonElement e : jumpJson.getAsJsonArray("directions")) {
                                jump.wasdDirections.add(InputType.valueOf(e.getAsString()));
                            }

                        } else if (jumpJson.has("direction")) {
                            jump.adDirection = InputType.valueOf(jumpJson.get("direction").getAsString());
                        }

                        if (jumpJson.has("length")) {
                            jump.length = jumpJson.get("length").getAsInt();
                        }

                        jump.run1T = jumpJson.get("run1T").getAsBoolean();
                        jump.ceilingHeight = CeilingHeight.valueOf(jumpJson.get("ceilingHeight").getAsString());

                        jump.removeTicks();

                        JsonArray jumpTicks = jumpJson.getAsJsonArray("ticks");

                        for (JsonElement jt : jumpTicks) {
                            Set<InputType> inputs = new HashSet<>();
                            jt.getAsJsonArray().forEach(e -> inputs.add(InputType.valueOf(e.getAsString())));

                            new StrategyTick(strategyTicks.size(), inputs, jump);
                        }
                    }
                }

            } catch (Exception e) {

                BurgMod.logger.error("Invalid strategy loaded! Using empty strategy.", e);
                bmChat("\u00A74ERROR: Invalid strategy loaded! Using empty strategy.");
                playErrorSound();

                StrategyListGui.clearStrategy();
            }
        }
    };

    public static final StrategyConfig instance = new StrategyConfig(new File(BurgMod.modConfigFolder, "strategy/strategy.json"));

}