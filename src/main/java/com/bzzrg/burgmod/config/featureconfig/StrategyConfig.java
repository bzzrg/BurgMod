package com.bzzrg.burgmod.config.featureconfig;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.features.strategy.StrategyJump;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.utils.PluginUtils.createDirectory;

public class StrategyConfig {

    public static final List<StrategyTick> strategyTicks = new ArrayList<>();
    public static final List<StrategyJump> strategyJumps = new ArrayList<>();


    public static void updateStrategyJson() {
        strategyFieldsToJson(new File(BurgMod.modConfigFolder, "input_status/strategy.json"));
    }

    public static void strategyFieldsToJson(File strategyJson) {
        JsonArray tickArray = new JsonArray();
        List<StrategyJump> addedJumps = new ArrayList<>();

        for (StrategyTick tick : strategyTicks) {

            if (tick.jump == null) { // Lone ticks

                JsonArray correctInputsArray = new JsonArray();
                tick.correctInputs.forEach(correctInput -> correctInputsArray.add(new JsonPrimitive(correctInput.name())));
                tickArray.add(correctInputsArray);

            } else if (!addedJumps.contains(tick.jump)) { // Jumps

                addedJumps.add(tick.jump);

                // Jump info
                JsonObject jumpJson = new JsonObject();

                jumpJson.add("type", new JsonPrimitive(tick.jump.type.name()));
                jumpJson.add("run1T", new JsonPrimitive(tick.jump.run1T));
                jumpJson.add("cut", new JsonPrimitive(tick.jump.cut));

                if (tick.jump.directions != null) {
                    JsonArray directionsArray = new JsonArray();
                    tick.jump.directions.forEach(onDirection -> directionsArray.add(new JsonPrimitive(onDirection.name())));
                    jumpJson.add("directions", directionsArray);

                } else if (tick.jump.direction != null) {
                    jumpJson.add("direction", new JsonPrimitive(tick.jump.direction.name()));
                }

                if (tick.jump.length != null) {
                    jumpJson.add("length", new JsonPrimitive(tick.jump.length));
                }

                // Jump ticks
                JsonArray jumpTickArray = new JsonArray();

                for (StrategyTick jumpTick : tick.jump.ticks) {
                    JsonArray correctInputsArray = new JsonArray();
                    jumpTick.correctInputs.forEach(correctInput -> correctInputsArray.add(new JsonPrimitive(correctInput.name())));
                    jumpTickArray.add(correctInputsArray);
                }

                jumpJson.add("ticks", jumpTickArray);

                // Add final jump JsonObject to tickArray
                tickArray.add(jumpJson);

            }
        }

        createDirectory(strategyJson.getParentFile());

        try (FileWriter writer = new FileWriter(strategyJson)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(tickArray, writer);

        } catch (IOException e) {
            BurgMod.logger.error("Failed to write to file '" + strategyJson.getName() + "'", e);

        }

    }

    public static void updateStrategyFields() {

        strategyTicks.clear();
        strategyJumps.clear();

        JsonArray tickArray;

        File strategyJson = new File(BurgMod.modConfigFolder, "input_status/strategy.json");

        if (strategyJson.exists()) {
            try (FileReader reader = new FileReader(strategyJson)) {
                tickArray = new Gson().fromJson(reader, JsonArray.class);
            } catch (IOException e) {
                BurgMod.logger.error("Failed to load strategy.json into static fields: Could not read the file. Loading an empty strategy instead", e);
                return;
            }

        } else {
            BurgMod.logger.info("Failed to load strategy.json into static fields: File doesn't exist. Loading an empty strategy instead");
            return;
        }

        try {
            for (JsonElement element : tickArray) {

                if (element instanceof JsonArray) { // Lone ticks

                    Set<StrategyTick.InputType> correctInputs = new HashSet<>();
                    element.getAsJsonArray().forEach(elem -> correctInputs.add(StrategyTick.InputType.valueOf(elem.getAsString())));

                    StrategyTick.addLoneTick(strategyTicks.size(), correctInputs);

                } else if (element instanceof JsonObject) { // Jumps

                    // Jump info
                    JsonObject jumpJson = (JsonObject) element;

                    StrategyJump.JumpType type = StrategyJump.JumpType.valueOf(jumpJson.get("type").getAsString());
                    StrategyJump jump = new StrategyJump(type);

                    jump.run1T = jumpJson.get("run1T").getAsBoolean();
                    if (jump.run1T) jump.run1TButton.displayString = "\u00A7aRun 1t";
                    jump.cut = jumpJson.get("cut").getAsBoolean();
                    if (jump.cut) jump.cutButton.displayString = "\u00A7aCut";

                    if (jumpJson.has("directions")) {

                        jump.directions.clear();
                        jump.directionButtons.get(StrategyTick.InputType.W).displayString = "\u00A7cW";

                        JsonArray directionsArray = jumpJson.getAsJsonArray("directions");

                        for (JsonElement elem : directionsArray) {
                            StrategyTick.InputType inputType = StrategyTick.InputType.valueOf(elem.getAsString());
                            jump.directions.add(inputType);
                            jump.directionButtons.get(inputType).displayString = "\u00A7a" + inputType.name();
                        }

                    } else if (jumpJson.has("direction")) {
                        jump.direction = StrategyTick.InputType.valueOf(jumpJson.get("direction").getAsString());
                        jump.directionButton.displayString = jump.direction.name();

                    }

                    if (jumpJson.has("length")) {
                        jump.length = jumpJson.get("length").getAsInt();
                        jump.lengthSlider.setValue(jump.length);
                        jump.lengthSlider.displayString = jump.length + "t";

                    }

                    // Jump ticks (something wrong with this maybe)
                    jump.removeTicks();
                    JsonArray jumpTickArray = jumpJson.getAsJsonArray("ticks");

                    for (JsonElement jumpTick : jumpTickArray) {

                        Set<StrategyTick.InputType> correctInputs = new HashSet<>();
                        jumpTick.getAsJsonArray().forEach(elem -> correctInputs.add(StrategyTick.InputType.valueOf(elem.getAsString())));

                        StrategyTick.addJumpTick(jump, correctInputs);
                    }

                }

            }
        } catch (Exception e) {
            BurgMod.logger.error("Failed to load strategy.json into static fields: Json has invalid strategy", e);
        }

    }

}
