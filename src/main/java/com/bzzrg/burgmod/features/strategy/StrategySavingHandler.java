package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig;
import com.bzzrg.burgmod.config.files.utils.JsonConfigFile;

import java.io.File;
import java.util.Objects;

import static com.bzzrg.burgmod.modutils.GeneralUtils.*;

public class StrategySavingHandler {

    public static void addSavingSettings(StrategyListGui gui) {

        gui.addStringSetting("Save Strat", () -> "", s -> {

            if (s.isEmpty()) {
                bmChat("\u00A7cPlease provide a key to save your current strategy to!");
                playErrorSound();
                return;
            }

            new JsonConfigFile(new File(BurgMod.modConfigFolder, "strategy/saved-strategies/" + s + ".json")) {
                @Override
                protected void init() {
                    addJson("strategy", StrategyConfig.convertor);
                }
            }.updateFile();

            bmChat("\u00A7aSaved current strategy under key: \u00A7b" + s);
        }, "New Key");

        gui.addStringSetting("Delete Strat", () -> "", s -> {
            if (new File(BurgMod.modConfigFolder, "strategy/saved-strategies/" + s + ".json").delete()) {
                bmChat("\u00A7aDeleted strategy under key: \u00A7b" + s);
            } else {
                bmChat("\u00A7cThere is no strategy saved under the provided key!");
                playErrorSound();
            }
        }, "Saved Key");

        gui.addStringSetting("Load Strat", () -> "", s -> {

            File stratFile = new File(BurgMod.modConfigFolder, "strategy/saved-strategies/" + s + ".json");

            if (!stratFile.exists()) {
                bmChat("\u00A7cThere is no strategy saved under the provided key!");
                playErrorSound();
                return;
            }

            new JsonConfigFile(stratFile) {
                @Override
                protected void init() {
                    addJson("strategy", StrategyConfig.convertor);
                }
            }.updateFields();

            StrategyConfig.instance.updateFile();
            gui.resetRows();
            bmChat("\u00A7aLoaded strategy from key: \u00A7b" + s);

        }, "Saved Key");

        gui.addStringSetting("Save HPK Strat", () -> "", s -> {
            int jumpNum;
            try {
                jumpNum = Integer.parseInt(s);
            } catch (Exception e) {
                bmChat("\u00A7cPlease provide a valid jump # to save your current strategy to!");
                playErrorSound();
                return;
            }

            new JsonConfigFile(new File(BurgMod.modConfigFolder, "strategy/saved-hpk-strategies/" + s + ".json")) {
                @Override
                protected void init() {
                    addJson("strategy", StrategyConfig.convertor);
                }
            }.updateFile();

            bmChat("\u00A7aSaved current strategy under jump #: \u00A7b" + jumpNum + " \u00A77(used by Auto Load HPK)");
        }, "OJ Jump #");

        gui.addStringSetting("Delete HPK Strat", () -> "", s -> {
            int jumpNum;
            try {
                jumpNum = Integer.parseInt(s);
            } catch (Exception e) {
                bmChat("\u00A7cPlease provide a valid jump # of a saved HPK strategy to remove!");
                playErrorSound();
                return;
            }

            if (new File(BurgMod.modConfigFolder, "strategy/saved-hpk-strategies/" + jumpNum + ".json").delete()) {
                bmChat("\u00A7aDeleted HPK strategy under jump #: \u00A7b" + jumpNum);
            } else {
                bmChat("\u00A7cThere is no HPK strategy saved under the provided jump number!");
                playErrorSound();
            }
        }, "Saved OJ Jump #");

        gui.addBooleanSetting("Auto Load HPK", () -> StrategyConfig.autoLoadHPK, b -> StrategyConfig.autoLoadHPK = b);

        gui.addActionButton("List Strats", b -> {

            File strategies = new File(BurgMod.modConfigFolder, "strategy/saved-strategies");
            createDirectory(strategies);

            File[] strategiesArray = strategies.listFiles();
            if (strategiesArray == null) return; // never null in practice

            File hpkStrategies = new File(BurgMod.modConfigFolder, "strategy/saved-hpk-strategies");
            createDirectory(hpkStrategies);

            File[] hpkStrategiesArray = hpkStrategies.listFiles();
            if (hpkStrategiesArray == null) return;  // never null in practice

            bmChat("\u00A7bSaved Strategies:");

            if (strategiesArray.length == 0) {
                chat("\u00A7cNone!");
            }

            for (File strategy : strategiesArray) {
                String trimmedName = strategy.getName().replace(".json", "");
                chat("\u00A77- \u00A7e" + trimmedName);
            }

            chat("");
            bmChat("\u00A7bSaved HPK Strategies:");

            if (hpkStrategiesArray.length == 0) {
                chat("\u00A7cNone!");
            }

            for (File hpkStrategy : hpkStrategiesArray) {
                String trimmedName = hpkStrategy.getName().replace(".json", "");
                chat("\u00A77- \u00A7e" + trimmedName);
            }
        });

    }
}