package com.bzzrg.burgmod.command;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler;
import com.bzzrg.burgmod.config.basicconfig.GeneralConfig;
import com.bzzrg.burgmod.config.specialconfig.PosCheckersConfig;
import com.bzzrg.burgmod.features.poschecker.Axis;
import com.bzzrg.burgmod.features.poschecker.PosChecker;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.utils.simulation.PlayerSim;
import com.bzzrg.burgmod.utils.simulation.UpdateSimOptions;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.specialconfig.PosCheckersConfig.posCheckers;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.*;
import static com.bzzrg.burgmod.utils.GeneralUtils.*;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.createSim;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.updateSim;

public class BMCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "bm";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/" + getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("burgmod");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {

        Runnable sendMainUsage = () -> {
            bmChat("\u00A7bUsage (/bm):");
            sendBMBullet("pos", "Adds checkers that send your X/Z pos any amount of ticks (0-100) after jumping");
            sendBMBullet("strat", "Save strategies under keys or smarter HPK OJ keys");
            sendBMBullet("dp", "Sets # of decimal places to show globally for all features involving numbers");
            sendBMBullet("c1", "Color 1 for labels");
            sendBMBullet("c2", "Color 2 for labels");
            chat("\u00A78(Check MC controls to access more BurgMod config)");
        };

        if (strings.length == 0) {
            sendMainUsage.run();
            return;
        }

        List<EnumChatFormatting> colors = Arrays.stream(EnumChatFormatting.values()).filter(EnumChatFormatting::isColor).collect(Collectors.toList());

        switch (strings[0]) {

            case "pos": {

                Runnable sendPosUsage = () -> {
                    bmChat("\u00A7bUsage (/bm pos):");
                    sendBMBullet("pos toggle", "Toggles all pos checkers on/off without deleting them");
                    sendBMBullet("pos add <x/z/both> <airtime>", "Adds new pos checker for either X or Z axis for a specific airtick");
                    sendBMBullet("pos remove <checker_num>", "Removes an added pos checker, use /bm pos list for checker numbers");
                    sendBMBullet("pos clear", "Removes all pos checkers");
                    sendBMBullet("pos list", "Lists all added pos checkers and their number");
                    sendBMBullet("pos limit", "Adds limits so that pos checkers for a jump will only send if first air tick was within limits");
                };

                String action;
                try {
                    action = strings[1];
                } catch (Exception e) {
                    sendPosUsage.run();
                    break;
                }

                switch (action) {
                    case "toggle": {
                        PosCheckersConfig.enabled = !PosCheckersConfig.enabled;
                        if (PosCheckersConfig.enabled) {
                            bmChat("\u00A7aEnabled position checkers!");
                        } else {
                            bmChat("\u00A7cDisabled position checkers!");
                        }

                        break;
                    }
                    case "add": {

                        Axis axis;
                        try {
                            axis = Axis.valueOf(strings[2].toUpperCase());
                        } catch (Exception e) {
                            bmChat("Please input either X, Z, or BOTH for your axis. (3rd argument)");
                            break;
                        }

                        int airtime;
                        try {
                            airtime = Integer.parseInt(strings[3]);
                            if (airtime < 1 || airtime > 100) {
                                bmChat("\u00A7cAirtime must be between 1 and 100! (4th argument)");
                                break;
                            }
                        } catch (Exception e) {
                            bmChat("\u00A7cPlease input a valid integer for your airtime. (4th argument)");
                            break;
                        }

                        posCheckers.add(new PosChecker(axis, airtime));
                        bmChat(String.format("\u00A7aAdded new position checker! Axis: %s, Ticks: %d \u00A77(Checker #%d)",
                                axis, airtime, posCheckers.size()));

                        break;
                    }
                    case "remove": {
                        try {
                            posCheckers.remove(Integer.parseInt(strings[2]) - 1);
                        } catch (Exception e) {
                            bmChat("\u00A7cInput a valid position checker number! (Use /bm pos list to see checker numbers) (3rd argument)");
                            break;
                        }

                        bmChat(String.format("\u00A7aRemoved position checker at checker number %d!", Integer.parseInt(strings[2])));

                        break;
                    }
                    case "clear": {
                        posCheckers.clear();
                        bmChat("\u00A7aRemoved all position checkers!");
                        break;
                    }
                    case "list": {

                        bmChat("\u00A7bPosition Checkers:");
                        if (posCheckers.size() == 0) {
                            chat("\u00A7cNone!");
                        }

                        for (int i = 0; i < posCheckers.size(); i++) {
                            PosChecker posChecker = posCheckers.get(i);
                            String axisName = posChecker.axis.name().substring(0, 1).toUpperCase() + posChecker.axis.name().substring(1).toLowerCase();
                            chat(String.format("\u00A77- \u00A7eAxis: %s | Airtime: %d (Checker #%d)",
                                    axisName, posChecker.airtime, i + 1));
                        }

                        break;
                    }
                    case "limit": {

                        Runnable sendLimitUsage = () -> {
                            bmChat("\u00A7bUsage (/bm pos limit):");
                            sendBMBullet("pos limit xmin <coordinate>", "Sets minumum player X coordinate required for position checkers to work");
                            sendBMBullet("pos limit xmax <coordinate>", "Sets maximum player X coordinate required for position checkers to work");
                            sendBMBullet("pos limit zmin <coordinate>", "Sets minumum player Z coordinate required for position checkers to work");
                            sendBMBullet("pos limit zmax <coordinate>", "Sets maximum player Z coordinate required for position checkers to work");
                            sendBMBullet("pos limit frompos <x> <z> <block_range>", "Set coordinate limits so that only jumps from a certain block range around this position can set off position checkers");
                            sendBMBullet("pos limit fromcurrent <block_range>", "Same as /bm pos limit pos, but uses your position's X and Z");
                            sendBMBullet("pos limit fromstrat <block_range> \u00A7c(IMPORTANT: Only works if momentum is noturn and command is run at reset location)", "Same as /bm pos limit pos, but uses the X and Z of final jump from strategy");
                        };

                        String limitAction;
                        try {
                            limitAction = strings[2].toLowerCase();
                        } catch (Exception e) {
                            sendLimitUsage.run();
                            break;
                        }

                        Double coordLimit;
                        try {
                            coordLimit = Double.parseDouble(strings[3]);
                        } catch (Exception e) {
                            coordLimit = null;
                        }

                        Runnable sendUpdatedCheckers = () -> {
                            bmChat(formatDp("\u00A7aSet minimum x coordinate for position checkers to %dp!", PosCheckersConfig.xMin));
                            bmChat(formatDp("\u00A7aSet maximum x coordinate for position checkers to %dp!", PosCheckersConfig.xMax));
                            bmChat(formatDp("\u00A7aSet minimum z coordinate for position checkers to %dp!", PosCheckersConfig.zMin));
                            bmChat(formatDp("\u00A7aSet maximum z coordinate for position checkers to %dp!", PosCheckersConfig.zMax));
                        };

                        switch (limitAction) {

                            case "xmin": {
                                if (coordLimit == null) {
                                    bmChat("\u00A7cPlease input a valid number for your minimum x coordinate. (4th argument)");
                                } else {
                                    PosCheckersConfig.xMin = coordLimit;
                                    bmChat("\u00A7aSet minimum x coordinate for position checkers to " + coordLimit + "!");
                                }
                                break;

                            }
                            case "xmax": {
                                if (coordLimit == null) {
                                    bmChat("\u00A7cPlease input a valid number for your maximum x coordinate. (4th argument)");
                                } else {
                                    PosCheckersConfig.xMax = coordLimit;
                                    bmChat("\u00A7aSet maximum x coordinate for position checkers to " + coordLimit + "!");
                                }
                                break;
                            }
                            case "zmin": {
                                if (coordLimit == null) {
                                    bmChat("\u00A7cPlease input a valid number for your minimum z coordinate. (4th argument)");
                                } else {
                                    PosCheckersConfig.zMin = coordLimit;
                                    bmChat("\u00A7aSet minimum z coordinate for position checkers to " + coordLimit + "!");
                                }
                                break;

                            }
                            case "zmax": {
                                if (coordLimit == null) {
                                    bmChat("\u00A7cPlease input a valid number for your maximum z coordinate. (4th argument)");
                                } else {
                                    PosCheckersConfig.zMax = coordLimit;
                                    bmChat("\u00A7aSet maximum z coordinate for position checkers to " + coordLimit + "!");
                                }
                                break;
                            }
                            case "frompos": {
                                double x;
                                try {
                                    x = Double.parseDouble(strings[3]);
                                } catch (Exception e) {
                                    bmChat("\u00A7cPlease input a valid number for your x coordinate. (4th argument)");
                                    break;
                                }

                                double z;
                                try {
                                    z = Double.parseDouble(strings[4]);
                                } catch (Exception e) {
                                    bmChat("\u00A7cPlease input a valid number for your z coordinate. (5th argument)");
                                    break;
                                }

                                double blockRange;
                                try {
                                    blockRange = Double.parseDouble(strings[5]);
                                } catch (Exception e) {
                                    bmChat("\u00A7cPlease input a valid number for your block range. (6th argument)");
                                    break;
                                }

                                PosCheckersConfig.xMin = x - blockRange;
                                PosCheckersConfig.xMax = x + blockRange;
                                PosCheckersConfig.zMin = z - blockRange;
                                PosCheckersConfig.zMax = z + blockRange;
                                sendUpdatedCheckers.run();

                                PosCheckersLimitBoxDrawer.drawFor4Seconds();
                                break;
                            }
                            case "fromcurrent": {

                                EntityPlayerSP player = mc.thePlayer;
                                if (player == null) break;

                                double blockRange;
                                try {
                                    blockRange = Double.parseDouble(strings[3]);
                                } catch (Exception e) {
                                    bmChat("\u00A7cPlease input a valid number for your block range. (4th argument)");
                                    break;
                                }

                                PosCheckersConfig.xMin = player.posX - blockRange;
                                PosCheckersConfig.xMax = player.posX + blockRange;
                                PosCheckersConfig.zMin = player.posZ - blockRange;
                                PosCheckersConfig.zMax = player.posZ + blockRange;
                                sendUpdatedCheckers.run();

                                PosCheckersLimitBoxDrawer.drawFor4Seconds();
                                break;
                            }
                            case "fromstrat": {

                                EntityPlayerSP real = mc.thePlayer;
                                if (real == null) break;

                                double blockRange;
                                try {
                                    blockRange = Double.parseDouble(strings[3]);
                                } catch (Exception e) {
                                    bmChat("\u00A7cPlease input a valid number for your block range. (4th argument)");
                                    break;
                                }

                                StrategyTick finalJumpTick = StrategyTick.getJumpTick(0);

                                if (finalJumpTick == null) {
                                    bmChat("\u00A7cYour strategy doesn't contain any jumps! (Read this command's info for more explanation)");
                                    break;
                                }

                                PlayerSim sim = createSim();
                                boolean lastAir = false;

                                for (StrategyTick tick : strategyTicks) {

                                    updateSim(sim, new UpdateSimOptions(
                                            tick.correctInputs.contains(InputType.W),
                                            tick.correctInputs.contains(InputType.A),
                                            tick.correctInputs.contains(InputType.S),
                                            tick.correctInputs.contains(InputType.D),
                                            tick.correctInputs.contains(InputType.SPR),
                                            tick.correctInputs.contains(InputType.SNK),
                                            tick.correctInputs.contains(InputType.AIR) && !lastAir,
                                            null));
                                    lastAir = tick.correctInputs.contains(InputType.AIR);

                                    if (tick == finalJumpTick) break;

                                }

                                PosCheckersConfig.xMin = sim.posX - blockRange;
                                PosCheckersConfig.xMax = sim.posX + blockRange;
                                PosCheckersConfig.zMin = sim.posZ - blockRange;
                                PosCheckersConfig.zMax = sim.posZ + blockRange;
                                sendUpdatedCheckers.run();

                                PosCheckersLimitBoxDrawer.drawFor4Seconds();
                                break;
                            }
                            default: {
                                sendLimitUsage.run();
                                break;
                            }
                        }

                        break;
                    }
                    default: {
                        sendPosUsage.run();
                        break;
                    }

                }

                PosCheckersConfig.updateJson();

                break;
            }
            case "strat": {

                Runnable sendStratUsage = () -> {
                    bmChat("\u00A7bUsage (/bm strat):");
                    sendBMBullet("strat save <key>", "Saves strategy under key");
                    sendBMBullet("strat load <key>", "Loads strategy saved under key");
                    sendBMBullet("strat delete <key>", "Deletes strategy saved under key");
                    sendBMBullet("strat savehpk <jump #>", "Saves strategy to OJ Jump #, HPK only");
                    sendBMBullet("strat autoloadhpk", "Enables auto-load for strat when joining jump, uses join jump chat msgs & savehpk strats, HPK only");
                    sendBMBullet("strat list", "Lists all keys of saved strategies");
                };

                String action;
                try {
                    action = strings[1];
                } catch (Exception e) {
                    sendStratUsage.run();
                    break;
                }

                switch (action) {
                    case "save": {

                        String key;
                        try {
                            key = strings[2];
                        } catch (Exception e) {
                            bmChat("\u00A7cPlease provide a key to save your current strategy to. (3rd argument)");
                            break;
                        }

                        strategyFieldsToJson(new File(BurgMod.modConfigFolder, "input_status/strategies/" + key + ".json"));
                        bmChat("\u00A7aSaved current strategy under key: \u00A7b" + key);

                        break;
                    }
                    case "load": {

                        String key;
                        try {
                            key = strings[2];
                        } catch (Exception e) {
                            bmChat("\u00A7cPlease provide a key to load a strategy from. (3rd argument)");
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
                            bmChat("\u00A7aLoaded strategy from key: \u00A7b" + key);

                        } else {
                            bmChat("\u00A7cThere is no strategy saved under the provided key. (3rd argument)");
                        }

                        break;
                    }
                    case "delete": {

                        String key;
                        try {
                            key = strings[2];
                        } catch (Exception e) {
                            bmChat("\u00A7cPlease provide a key of a strategy to delete. (3rd argument)");
                            break;
                        }

                        if (new File(BurgMod.modConfigFolder, "input_status/strategies/" + key + ".json").delete()) {
                            bmChat("\u00A7aDeleted strategy under key: \u00A7b" + key);
                        } else {
                            bmChat("\u00A7cThere is no strategy saved under the provided key. (3rd argument)");
                        }

                        break;
                    }
                    case "savehpk": {
                        int jumpNum;
                        try {
                            jumpNum = Integer.parseInt(strings[2]);
                        } catch (Exception e) {
                            bmChat("\u00A7cPlease provide a valid jump # to save your current strategy to. (3rd argument)");
                            break;
                        }

                        strategyFieldsToJson(new File(BurgMod.modConfigFolder, "input_status/hpk_strategies/" + jumpNum + ".json"));
                        bmChat("\u00A7aSaved current strategy under jump #: \u00A7b" + jumpNum + " \u00A77(used by autoloadhpk)");
                        break;
                    }
                    case "autoloadhpk": {

                        GeneralConfig.autoStrategyLoadOn = !GeneralConfig.autoStrategyLoadOn;

                        if (GeneralConfig.autoStrategyLoadOn) {
                            bmChat("\u00A7aAuto loading for HPK strategies (created with savehpk) is now enabled!");
                        } else {
                            bmChat("\u00A7eAuto loading for HPK strategies (created with savehpk) is now disabled!");
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
                        break;
                    }
                    default:
                        sendStratUsage.run();
                        break;
                }
                break;
            }
            case "dp": {

                try {
                    int decimalPrecision = Integer.parseInt(strings[1]);

                    if (decimalPrecision < 0 || decimalPrecision > 100) {
                        bmChat("\u00A7cYour integer must be between 0 and 100! (2nd argument)");
                        break;
                    }

                    GeneralConfig.decimalPrecision = decimalPrecision;
                    bmChat("\u00A7aChanged decimal precision to " + decimalPrecision + " decimal places!");

                } catch (Exception e) {
                    bmChat("\u00A7bUsage (/bm dp):");
                    chat("\u00A77- \u00A7e/bm dp <integer> \u00A77(Sets decimal precision globally for all features involving numbers)");
                    break;
                }
                break;
            }
            case "c1": {
                try {
                    GeneralConfig.color1 = EnumChatFormatting.valueOf(strings[1].toUpperCase()).toString();
                    bmChat("\u00A7aColor 1 has been set to: " + GeneralConfig.color1 + strings[1].toUpperCase());
                } catch (Exception e) {
                    bmChat("\u00A7cInput a valid MC color code (caps don't matter) (2nd argument):");
                    chat(colors.stream().map(color -> color + color.name()).collect(Collectors.joining(", ")));
                }
                break;
            }
            case "c2": {
                try {
                    GeneralConfig.color2 = EnumChatFormatting.valueOf(strings[1].toUpperCase()).toString();
                    bmChat("\u00A7aColor 2 has been set to: " + GeneralConfig.color2 + strings[1].toUpperCase());
                } catch (Exception e) {
                    bmChat("\u00A7cInput a valid MC color code (caps don't matter) (2nd argument):");
                    chat(colors.stream().map(color -> color + color.name()).collect(Collectors.joining(", ")));
                }
                break;
            }
            default: {
                sendMainUsage.run();
                break;
            }
        }

        BasicConfigHandler.updateConfigFile();

    }

    private static void sendBMBullet(String args, String info) {

        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;

        IChatComponent nonInfoComp = new ChatComponentText("\u00A77- \u00A7e/bm " + args + " ");
        IChatComponent infoComp = new ChatComponentText("\u00A76\u00A7l[INFO]");
        infoComp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("\u00A76" + info)));
        player.addChatMessage(nonInfoComp.appendSibling(infoComp));

    }

}
