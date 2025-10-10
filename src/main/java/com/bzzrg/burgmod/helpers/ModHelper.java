package com.bzzrg.burgmod.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;

public class ModHelper {

    public static int scaledX(double xPercent) {
        int screenWidth = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - 1;
        return Math.round(screenWidth * ((float) xPercent / 100));
    }

    public static int scaledY(double yPercent) {
        int screenHeight = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - 1;
        return Math.round(screenHeight * ((float) yPercent / 100));
    }

    public static void sendMessage(String message) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) player.addChatMessage(new ChatComponentText(message));
    }

}
