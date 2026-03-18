package com.bzzrg.burgmod.utils;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.basicconfig.GeneralConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

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
    public static boolean onGround(EntityPlayerSP player) {
        return player.onGround || getBlockStandingOn(player) != null;
    }
    public static boolean onGround() {
        return onGround(Minecraft.getMinecraft().thePlayer);
    }

    public static BlockPos getBlockStandingOn(EntityPlayerSP player) {

        AxisAlignedBB bb = player.getEntityBoundingBox();
        double feet = bb.minY;

        World world = player.worldObj;

        int minX = MathHelper.floor_double(bb.minX);
        int maxX = MathHelper.floor_double(bb.maxX);
        int minZ = MathHelper.floor_double(bb.minZ);
        int maxZ = MathHelper.floor_double(bb.maxZ);

        int minY = MathHelper.floor_double(feet) - 1;
        int maxY = MathHelper.floor_double(feet);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {

                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(pos);

                    AxisAlignedBB blockBB = state.getBlock().getCollisionBoundingBox(world, pos, state);
                    if (blockBB == null) continue;

                    if (Math.abs(blockBB.maxY - feet) < 1e-6) {
                        return pos;
                    }
                }
            }
        }

        return null;
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
