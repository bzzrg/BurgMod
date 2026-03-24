package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig;
import com.bzzrg.burgmod.config.specialconfig.StrategyConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.StrategyConfigGui.clearStrategy;
import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;

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
            clearStrategy();

            for (Set<InputType> inputs : validStratInputs) {
                StrategyTick.addLoneTick(strategyTicks.size(), inputs);
            }

            StrategyConfig.updateStrategyJson();
            Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());

            bmChat("\u00A7aFixed the 45 jumps from your strategy!");
        }


    }
}
