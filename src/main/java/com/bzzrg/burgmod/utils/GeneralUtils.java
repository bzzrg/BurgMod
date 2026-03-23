package com.bzzrg.burgmod.utils;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.basicconfig.GeneralConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.BurgMod.mc;

public class GeneralUtils {

    public static int getScaledWidth() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int getScaledHeight() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }


    public static void chat(String message) {
        if (mc.thePlayer != null) mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
    public static void bmChat(String message) {
        chat("\u00A71[BM] \u00A7r" + message);
    }

    public static void playSound(String name, float volume, float pitch) {
        if (mc.thePlayer != null) mc.thePlayer.playSound(name, volume, pitch);
    }

    public static void createDirectory(File folder) {
        if (folder.mkdirs()) {
            BurgMod.logger.info("Created directory: " + folder.getAbsolutePath());
        }
    }

    // can't use onGround alone because teleport causes a 1-tick false, still using because of coyote ground tick when running off a block (where you are sort of standing on air for a tick)
    public static boolean onGround(EntityPlayerSP player) {
        return player.onGround || getBlockStandingOn(player) != null;
    }
    public static boolean onGround() {
        return onGround(Minecraft.getMinecraft().thePlayer);
    }

    public static BlockPos getBlockStandingOn(EntityPlayerSP player) {

        double halfWidth = player.width / 2.0;

        AxisAlignedBB playerBB = new AxisAlignedBB(
                player.prevPosX - halfWidth, player.posY - 1e-3, player.prevPosZ - halfWidth,
                player.prevPosX + halfWidth, player.posY, player.prevPosZ + halfWidth
        );

        List<AxisAlignedBB> boxes = player.worldObj.getCollidingBoundingBoxes(player, playerBB);

        AxisAlignedBB best = null;
        double bestArea = 0.0;

        for (AxisAlignedBB box : boxes) {
            double overlapX = Math.min(playerBB.maxX, box.maxX) - Math.max(playerBB.minX, box.minX);
            double overlapZ = Math.min(playerBB.maxZ, box.maxZ) - Math.max(playerBB.minZ, box.minZ);

            if (overlapX > 0 && overlapZ > 0) {
                double area = overlapX * overlapZ;

                if (area > bestArea) {
                    bestArea = area;
                    best = box;
                }
            }
        }

        return best != null ? new BlockPos(best.minX, best.minY, best.minZ) : null;
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
        if (!onGround()) inputs.add(InputType.AIR);

        return inputs;
    }
    public static String formatDp(String format, Object... args) { // Used like string.format except now new identifier, "%dp" that uses decimal precision from config for floats/doubles
        format = format.replace("%dp", "%." + GeneralConfig.decimalPrecision + "f");
        return String.format(format, args);
    }




}
