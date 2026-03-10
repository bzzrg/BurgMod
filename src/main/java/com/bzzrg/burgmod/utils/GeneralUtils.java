package com.bzzrg.burgmod.utils;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.basicconfig.GeneralConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GeneralUtils {

    public static int getScaledWidth() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int getScaledHeight() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }


    public static void chat(String message) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) player.addChatMessage(new ChatComponentText(message));
    }
    public static void bmChat(String message) {
        chat("\u00A71[BM] \u00A7r" + message);
    }



    public static void createDirectory(File folder) {
        if (folder.mkdirs()) {
            BurgMod.logger.info("Created directory: " + folder.getAbsolutePath());
        }
    }

    // can't use onGround alone because teleport causes a 1-tick false, still using because of coyote ground tick when running off a block (where you are sort of standing on air for a tick)
    public static boolean isAirborne() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return false;
        boolean hasNoGroundCollision = player.worldObj.getCollidingBoundingBoxes(player, player.getEntityBoundingBox().offset(0.0, -1.0E-4, 0.0)).isEmpty();
        return hasNoGroundCollision && !player.onGround;
    }

    public static Set<InputType> getInputs() {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (player == null) {
            throw new IllegalStateException("Failed to get player inputs: thePlayer variable is null");
        }

        Set<InputType> inputs = new HashSet<>();

        if (mc.gameSettings.keyBindForward.isKeyDown()) inputs.add(InputType.W);
        if (mc.gameSettings.keyBindLeft.isKeyDown()) inputs.add(InputType.A);
        if (mc.gameSettings.keyBindBack.isKeyDown()) inputs.add(InputType.S);
        if (mc.gameSettings.keyBindRight.isKeyDown()) inputs.add(InputType.D);

        if (player.isSprinting()) inputs.add(InputType.SPR);
        if (player.isSneaking()) inputs.add(InputType.SNK);
        if (isAirborne()) inputs.add(InputType.AIR);

        return inputs;
    }
    public static String formatDp(String format, Object... args) { // Used like string.format except now new identifier, "%dp" that uses decimal precision from config for floats/doubles
        format = format.replace("%dp", "%." + GeneralConfig.decimalPlaces + "f");
        return String.format(format, args);
    }

}
