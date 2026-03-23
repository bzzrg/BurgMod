package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.specialconfig.StrategyConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyConfigGui;
import com.bzzrg.burgmod.features.strategy.StrategyJump;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyJumps;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
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

        if (P45OffsetHandler.isStrategyPartialInvalid()) {
            bmChat("\u00A7cPlease fix the other issues with your config before you attempt to fix your strategy's 45 jumps!");
        } else {

            List<Set<InputType>> validStratInputs = P45OffsetHandler.getValidStratInputs();
            new ArrayList<>(strategyTicks).forEach(StrategyTick::remove);
            new ArrayList<>(strategyJumps).forEach(StrategyJump::remove);

            for (Set<InputType> inputs : validStratInputs) {
                StrategyTick.addLoneTick(strategyTicks.size(), inputs);
            }

            StrategyConfig.updateStrategyJson();
            Minecraft.getMinecraft().displayGuiScreen(new StrategyConfigGui());

            bmChat("\u00A7aFixed the 45 jumps from your strategy!");
        }


    }
}
