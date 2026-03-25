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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
    public static void playErrorSound() {
        playSound("mob.endermen.portal", 1.0F, 0.5F);
    }

    public static void createDirectory(File folder) {
        if (folder.mkdirs()) {
            BurgMod.logger.info("Created directory: " + folder.getAbsolutePath());
        }
    }

    public static boolean onGround() {
        return getBlockStandingOn(mc.thePlayer) != null;
    }

    // coyote tick is account for in this
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

    public static <T> T getLast(List<T> list) {
        return list.get(list.size()-1);
    }

    public static Set<InputType> getInputs() {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.thePlayer;

        if (p == null) {
            throw new IllegalStateException("Failed to get player inputs: thePlayer variable is null");
        }

        Set<InputType> inputs = new HashSet<>();

        if (mc.gameSettings.keyBindForward.isKeyDown()) inputs.add(InputType.W);
        if (mc.gameSettings.keyBindLeft.isKeyDown()) inputs.add(InputType.A);
        if (mc.gameSettings.keyBindBack.isKeyDown()) inputs.add(InputType.S);
        if (mc.gameSettings.keyBindRight.isKeyDown()) inputs.add(InputType.D);

        if (p.isSprinting()) inputs.add(InputType.SPR);
        if (p.isSneaking()) inputs.add(InputType.SNK);
        if (lastOnGround && !onGround() && mc.gameSettings.keyBindJump.isKeyDown()) inputs.add(InputType.JMP);

        return inputs;
    }
    public static String formatDp(String format, Object... args) { // Used like string.format except now new identifier, "%dp" that uses decimal precision from config for floats/doubles
        format = format.replace("%dp", "%." + GeneralConfig.decimalPrecision + "f");
        return String.format(format, args);
    }

    public static boolean lastOnGround = true;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null) return;

        lastOnGround = onGround();
    }




}
