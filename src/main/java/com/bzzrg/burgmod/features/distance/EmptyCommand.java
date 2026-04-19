package com.bzzrg.burgmod.features.distance;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class EmptyCommand extends CommandBase {

    private final String name;

    public EmptyCommand(String name) {
        this.name = name;
    }

    @Override
    public String getCommandName() {
        return name;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) { }
}