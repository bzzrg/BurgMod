package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.files.mainconfigsections.P45OffsetConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyListGui;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.perfect45offset.P45OffsetHandler.getJump45Indices;
import static com.bzzrg.burgmod.features.perfect45offset.P45OffsetHandler.getValid45Ticks;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.modutils.GeneralUtils.getLast;

public class FixStrat45sCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "bmfixstrat45s";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return null;
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

            List<Integer> jump45Indices = getJump45Indices();

            StrategyTick lastTick = getLast(strategyTicks);
            if (getLast(jump45Indices) == lastTick.getIndex()) {
                // correctInputs for this tick will be overriden by the fix so use empty HashSet
                new StrategyTick(strategyTicks.size(), new HashSet<>(), lastTick.jump);
            }

            int i = jump45Indices.get(0) + 1;
            for (Set<InputType> validInputs : getValid45Ticks()) {
                strategyTicks.get(i).correctInputs = validInputs;
                i++;
            }

            Minecraft.getMinecraft().displayGuiScreen(new StrategyListGui());

            bmChat("\u00A7aFixed the 45 jumps from your strategy! \u00A7e(Note: Assumes that you use tapping A/D on jump ticks for 45s. If you don't, just replace remove the A & D from your 45 jump ticks)");
        }


    }
}
