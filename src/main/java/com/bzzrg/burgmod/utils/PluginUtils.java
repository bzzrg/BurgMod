package com.bzzrg.burgmod.utils;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class PluginUtils {

    public static int getScaledWidth() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int getScaledHeight() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }


    public static void sendMessage(String message) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) player.addChatMessage(new ChatComponentText(message));
    }

    public static void createDirectory(File folder) {
        if (folder.mkdirs()) {
            BurgMod.logger.info("Created directory: " + folder.getAbsolutePath());
        }
    }

    public static boolean isAirborne() { // can't use onGround because teleport causes a 1-tick false
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return false;
        return player.worldObj.getCollidingBoundingBoxes(player, player.getEntityBoundingBox().offset(0.0, -1.0E-4, 0.0)).isEmpty();
    }

    public static Set<StrategyTick.InputType> getInputs() {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (player == null) {
            throw new IllegalStateException("Failed to get player inputs: thePlayer variable is null");
        }

        Set<StrategyTick.InputType> inputs = new HashSet<>();

        if (mc.gameSettings.keyBindForward.isKeyDown()) inputs.add(StrategyTick.InputType.W);
        if (mc.gameSettings.keyBindLeft.isKeyDown()) inputs.add(StrategyTick.InputType.A);
        if (mc.gameSettings.keyBindBack.isKeyDown()) inputs.add(StrategyTick.InputType.S);
        if (mc.gameSettings.keyBindRight.isKeyDown()) inputs.add(StrategyTick.InputType.D);

        if (player.isSprinting()) inputs.add(StrategyTick.InputType.SPR);
        if (player.isSneaking()) inputs.add(StrategyTick.InputType.SNK);
        if (isAirborne()) inputs.add(StrategyTick.InputType.AIR);

        return inputs;
    }
}
