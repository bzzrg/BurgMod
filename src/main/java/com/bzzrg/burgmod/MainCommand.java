package com.bzzrg.burgmod;

import com.bzzrg.burgmod.config.InputStatusConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.bzzrg.burgmod.helpers.ModHelper.sendMessage;

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
        if (strings.length == 0) {
            sendMessage("\u00A7bUsage:");
            sendMessage("\u00A77- \u00A7e/burgmod color1");
            sendMessage("\u00A77- \u00A7e/burgmod color2");
            return;
        }

        List<EnumChatFormatting> colors = Arrays.stream(EnumChatFormatting.values()).filter(EnumChatFormatting::isColor).collect(Collectors.toList());

        switch (strings[0]) {
            case "color1":

                try {
                    InputStatusConfig.color1 = EnumChatFormatting.valueOf(strings[1].toUpperCase()).toString();
                    InputStatusConfig.save();
                    sendMessage("\u00A7aColor 1 has been set to: " + InputStatusConfig.color1 + strings[1].toUpperCase());
                } catch (Exception e) {
                    sendMessage("\u00A7cInvalid color code! List of valid color codes:");
                    for (EnumChatFormatting color : colors) {
                        sendMessage("\u00A77- " + color + color.name());
                    }
                    return;
                }

                break;

            case "color2":

                try {
                    InputStatusConfig.color2 = EnumChatFormatting.valueOf(strings[1].toUpperCase()).toString();
                    InputStatusConfig.save();
                    sendMessage("\u00A7aColor 2 has been set to: " + InputStatusConfig.color2 + strings[1].toUpperCase());
                } catch (Exception e) {
                    sendMessage("\u00A7cInvalid color code! List of valid color codes:");
                    for (EnumChatFormatting color : colors) {
                        sendMessage("\u00A77- " + color + color.name());
                    }
                    return;
                }

                break;
            default:
                sendMessage("\u00A7bUsage:");
                sendMessage("\u00A77- \u00A7e/burgmod color1");
                sendMessage("\u00A77- \u00A7e/burgmod color2");
        }
    }
}
