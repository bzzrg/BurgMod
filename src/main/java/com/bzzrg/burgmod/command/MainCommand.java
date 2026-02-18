package com.bzzrg.burgmod.command;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.ConfigHandler;
import com.bzzrg.burgmod.config.featureconfig.PosCheckerConfig;
import com.bzzrg.burgmod.features.poschecker.PosCheckerHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bzzrg.burgmod.config.featureconfig.StrategyConfig.strategyFieldsToJson;
import static com.bzzrg.burgmod.config.featureconfig.StrategyConfig.updateStrategyFields;
import static com.bzzrg.burgmod.features.poschecker.PosCheckerHandler.posCheckers;
import static com.bzzrg.burgmod.utils.PluginUtils.createDirectory;
import static com.bzzrg.burgmod.utils.PluginUtils.sendMessage;

public class MainCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "burgmod";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/" + getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("bm");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {

        Runnable sendMainUsage = () -> {
            sendMessage("\u00A71[BurgMod]\u00A7r\u00A7r \u00A7bUsage:");
            sendMessage("\u00A77- \u00A7e/bm pos \u00A77(Add checkers that send your X/Z pos any amount of ticks (0-100) after jumping)");
            sendMessage("\u00A77- \u00A7e/bm strat \u00A77(Save strategies under keys or smarter HPK OJ keys)");
            sendMessage("\u00A77- \u00A7e/bm c1 \u00A77(Color 1 for labels)");
            sendMessage("\u00A77- \u00A7e/bm c2 \u00A77(Color 2 for labels)");
            sendMessage("\u00A78(Check MC controls to access more BurgMod config)");
        };

        if (strings.length == 0) {
            sendMainUsage.run();
            return;
        }

        Runnable sendStratUsage = () -> {
            sendMessage("\u00A71[BurgMod]\u00A7r\u00A7r \u00A7bUsage (/bm strat):");
            sendMessage("\u00A77- \u00A7e/bm strat save <key> \u00A77(Saves strategy under key)");
            sendMessage("\u00A77- \u00A7e/bm strat load <key> \u00A77(Loads strategy saved under key)");
            sendMessage("\u00A77- \u00A7e/bm strat delete <key> \u00A77(Deletes strategy saved under key)");
            sendMessage("\u00A77- \u00A7e/bm strat savehpk <jump #> \u00A77(Saves strategy to OJ Jump #, HPK only)");
            sendMessage("\u00A77- \u00A7e/bm strat autoloadhpk \u00A77(Enables auto-load for strat when joining jump, uses join jump chat msgs & savehpk strats, HPK only)");
            sendMessage("\u00A77- \u00A7e/bm strat list \u00A77(Lists all keys of saved strategies)");
        };

        Runnable sendPosUsage = () -> {
            sendMessage("\u00A71[BurgMod]\u00A7r\u00A7r \u00A7bUsage (/bm pos):");
            sendMessage("\u00A77- \u00A7e/bm pos add <X/Z/BOTH> <ticks_after_jump> \u00A77(Adds new pos checker for either X or Z axis any amount of ticks (0-100) after jumping)");
            sendMessage("\u00A77- \u00A7e/bm pos remove <checker_num> \u00A77(Removes an added pos checker, use /bm pos list for checker numbers)");
            sendMessage("\u00A77- \u00A7e/bm pos list \u00A77(Lists all added pos checkers and their number)");
        };

        List<EnumChatFormatting> colors = Arrays.stream(EnumChatFormatting.values()).filter(EnumChatFormatting::isColor).collect(Collectors.toList());

        switch (strings[0]) {

            case "pos": {
                String action;
                try {
                    action = strings[1];
                } catch (Exception e) {
                    sendPosUsage.run();
                    return;
                }

                switch (action) {
                    case "add":

                        PosCheckerHandler.Axis axis;
                        try {
                            axis = PosCheckerHandler.Axis.valueOf(strings[2].toUpperCase());
                        } catch (Exception e) {
                            sendPosUsage.run();
                            break;
                        }

                        int ticksAfterJump;
                        try {
                            ticksAfterJump = Integer.parseInt(strings[3]);
                            if (ticksAfterJump < 1 || ticksAfterJump > 100) {
                                sendMessage("\u00A71[BurgMod]\u00A7r\u00A7r \u00A7cTicks after jump must be between 1 and 100!");
                                break;
                            }
                        } catch (Exception e) {
                            sendPosUsage.run();
                            break;
                        }

                        posCheckers.add(new PosCheckerHandler.PosChecker(axis, ticksAfterJump));
                        sendMessage(String.format("\u00A71[BurgMod]\u00A7r\u00A7r \u00A7aAdded new position checker! Axis: %s, Ticks: %d \u00A77(Checker #%d)",
                                axis, ticksAfterJump, posCheckers.size()));

                        break;
                    case "remove":
                        try {
                            posCheckers.remove(Integer.parseInt(strings[2]) - 1);
                        } catch (Exception e) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cInput a valid position checker number! (Use /bm pos list to see checker numbers)");
                            break;
                        }

                        sendMessage(String.format("\u00A71[BurgMod]\u00A7r \u00A7aRemoved position checker at checker number %d!", Integer.parseInt(strings[2])));

                        break;
                    case "list":

                        sendMessage("\u00A71[BurgMod]\u00A7r \u00A7bPosition Checkers:");
                        if (posCheckers.size() == 0) {
                            sendMessage("\u00A7cNone!");
                        }

                        for (int i = 0; i < posCheckers.size(); i++) {
                            PosCheckerHandler.PosChecker posChecker = posCheckers.get(i);
                            sendMessage(String.format("\u00A77- \u00A7eAxis: %s | Ticks After Jump: %d (Checker #%d)",
                                    posChecker.axis, posChecker.ticksAfterJump, i+1));
                        }

                        break;
                }

                PosCheckerConfig.updateJson();

                break;
            }
            case "strat": {
                String action;
                try {
                    action = strings[1];
                } catch (Exception e) {
                    sendStratUsage.run();
                    return;
                }

                switch (action) {
                    case "save": {

                        String key;
                        try {
                            key = strings[2];
                        } catch (Exception e) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cPlease provide a key to save your current strategy to.");
                            break;
                        }

                        strategyFieldsToJson(new File(BurgMod.modConfigFolder, "input_status/strategies/" + key + ".json"));
                        sendMessage("\"u00A71[BurgMod]\u00A7r \u00A7aSaved current strategy under key: \u00A7b" + key);

                        break;
                    }
                    case "load": {

                        String key;
                        try {
                            key = strings[2];
                        } catch (Exception e) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cPlease provide a key to load a strategy from.");
                            break;
                        }

                        File loadJson = new File(BurgMod.modConfigFolder, "input_status/strategies/" + key + ".json");

                        if (loadJson.exists()) {

                            try {
                                Files.copy(loadJson.toPath(), new File(BurgMod.modConfigFolder, "input_status/strategy.json").toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (Exception e) {
                                BurgMod.logger.error("Failed to load " + loadJson.getName() + ".json into strategy.json", e);
                                break;
                            }

                            updateStrategyFields();
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7aLoaded strategy from key: \u00A7b" + key);

                        } else {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cThere is no strategy saved under the provided key.");
                        }

                        break;
                    }
                    case "delete": {

                        String key;
                        try {
                            key = strings[2];
                        } catch (Exception e) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cPlease provide a key of a strategy to delete.");
                            break;
                        }

                        if (new File(BurgMod.modConfigFolder, "input_status/strategies/" + key + ".json").delete()) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7aDeleted strategy under key: \u00A7b" + key);
                        } else {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cThere is no strategy saved under the provided key.");
                        }

                        break;
                    }
                    case "savehpk": {
                        int jumpNum;
                        try {
                            jumpNum = Integer.parseInt(strings[2]);
                        } catch (Exception e) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cPlease provide a valid jump # to save your current strategy to.");
                            break;
                        }

                        strategyFieldsToJson(new File(BurgMod.modConfigFolder, "input_status/hpk_strategies/" + jumpNum + ".json"));
                        sendMessage("\u00A71[BurgMod]\u00A7r \u00A7aSaved current strategy under jump #: \u00A7b" + jumpNum + " \u00A77(used by autoloadhpk)");
                        break;
                    }
                    case "autoloadhpk": {

                        ConfigHandler.autoStrategyLoadOn = !ConfigHandler.autoStrategyLoadOn;

                        if (ConfigHandler.autoStrategyLoadOn) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7aAuto loading for HPK strategies (created with savehpk) is now enabled!");
                        } else {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7eAuto loading for HPK strategies (created with savehpk) is now disabled!");
                        }

                        break;
                    }
                    case "list": {


                        File strategies = new File(BurgMod.modConfigFolder, "input_status/strategies");
                        createDirectory(strategies);

                        File[] strategiesArray = Objects.requireNonNull(strategies.listFiles()); // In practice, .listFiles will never produce an NPE, requireNonNull is to silence IDE

                        File hpkStrategies = new File(BurgMod.modConfigFolder, "input_status/hpk_strategies");
                        createDirectory(hpkStrategies);

                        File[] hpkStrategiesArray = Objects.requireNonNull(hpkStrategies.listFiles()); // In practice, .listFiles will never produce an NPE, requireNonNull is to silence IDE

                        if (strategiesArray.length == 0 && hpkStrategiesArray.length == 0) {
                            sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cYou don't have any strategies saved yet!");
                            break;
                        }

                        sendMessage("\u00A71[BurgMod]\u00A7r \u00A7bSaved Strategies:");

                        if (strategiesArray.length == 0) {
                            sendMessage("\u00A7cNone!");
                        }

                        for (File strategy : strategiesArray) {
                            String trimmedName = strategy.getName().replace(".json", "");
                            sendMessage("\u00A77- \u00A7e" + trimmedName);
                        }

                        sendMessage("");
                        sendMessage("\u00A71[BurgMod]\u00A7r \u00A7bSaved HPK Strategies:");

                        if (hpkStrategiesArray.length == 0) {
                            sendMessage("\u00A7cNone!");
                        }

                        for (File hpkStrategy : hpkStrategiesArray) {
                            String trimmedName = hpkStrategy.getName().replace(".json", "");
                            sendMessage("\u00A77- \u00A7e" + trimmedName);
                        }
                        break;
                    }
                    default:
                        sendStratUsage.run();
                        break;
                }
                break;
            }
            case "c1": {
                try {
                    ConfigHandler.color1 = EnumChatFormatting.valueOf(strings[1].toUpperCase()).toString();
                    ConfigHandler.updateConfigFromFields();
                    sendMessage("\u00A71[BurgMod]\u00A7r \u00A7aColor 1 has been set to: " + ConfigHandler.color1 + strings[1].toUpperCase());
                } catch (Exception e) {
                    sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cInput a valid MC color code (caps don't matter):");
                    sendMessage(colors.stream().map(color -> color + color.name()).collect(Collectors.joining(", ")));
                }
                break;
            }
            case "c2": {
                try {
                    ConfigHandler.color2 = EnumChatFormatting.valueOf(strings[1].toUpperCase()).toString();
                    ConfigHandler.updateConfigFromFields();
                    sendMessage("\u00A71[BurgMod]\u00A7r \u00A7aColor 2 has been set to: " + ConfigHandler.color2 + strings[1].toUpperCase());
                } catch (Exception e) {
                    sendMessage("\u00A71[BurgMod]\u00A7r \u00A7cInput a valid MC color code (caps don't matter):");
                    sendMessage(colors.stream().map(color -> color + color.name()).collect(Collectors.joining(", ")));
                }
                break;
            }
            default: {
                sendMainUsage.run();
                break;
            }
        }
    }
}
