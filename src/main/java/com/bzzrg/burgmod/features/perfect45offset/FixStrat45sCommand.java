package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.files.mainconfigsections.P45OffsetConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyListGui;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;

public class FixStrat45sCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "bmfixstrat45s";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/" + getCommandName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {

        if (P45OffsetConfig.numOf45s > StrategyTick.getJumpIndices().size()) {
            bmChat("\u00A7c# of 45s inside perfect 45 offset config is more than # of jumps inside your strategy! Please fix this before you attempt to use Fix Strat 45s!");
        } else {

            List<Set<InputType>> validStratInputs = P45OffsetHandler.getValidStratInputs();
            StrategyListGui.clearStrategy();

            for (Set<InputType> inputs : validStratInputs) {
                new StrategyTick(strategyTicks.size(), inputs, null);
            }

            Minecraft.getMinecraft().displayGuiScreen(new StrategyListGui());

            bmChat("\u00A7aFixed the 45 jumps from your strategy!");
        }


    }
}
