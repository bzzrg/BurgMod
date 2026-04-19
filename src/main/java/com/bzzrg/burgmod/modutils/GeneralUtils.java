package com.bzzrg.burgmod.modutils;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.BurgMod.mc;

public class GeneralUtils {

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

    public static void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        mc.ingameGUI.displayTitle(null, null, fadeIn, stay, fadeOut);
        mc.ingameGUI.displayTitle(title, null, -1, -1, -1);
        mc.ingameGUI.displayTitle(null, subtitle, -1, -1, -1);
    }

    public static boolean onGround() {
        return getBlockStandingOn(mc.thePlayer) != null;
    }

    // coyote tick is account for in this
    public static BlockPos getBlockStandingOn(EntityPlayerSP player) {
        double halfWidth = player.width / 2.0;

        AxisAlignedBB curBB = new AxisAlignedBB(
                player.posX - halfWidth, player.posY - 1e-3, player.posZ - halfWidth,
                player.posX + halfWidth, player.posY, player.posZ + halfWidth
        );

        AxisAlignedBB prevBB = new AxisAlignedBB(
                player.prevPosX - halfWidth, player.posY - 1e-3, player.prevPosZ - halfWidth,
                player.prevPosX + halfWidth, player.posY, player.prevPosZ + halfWidth
        );

        List<AxisAlignedBB> curBoxes = player.worldObj.getCollidingBoundingBoxes(player, curBB);
        List<AxisAlignedBB> prevBoxes = player.worldObj.getCollidingBoundingBoxes(player, prevBB);

        AxisAlignedBB best = null;
        double bestArea = 0.0;

        for (AxisAlignedBB playerBB : new AxisAlignedBB[]{curBB, prevBB}) {
            List<AxisAlignedBB> boxes = playerBB == curBB ? curBoxes : prevBoxes;

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

            if (best != null) break;
        }

        return best != null ? new BlockPos(best.minX, best.minY, best.minZ) : null;
    }

    public static AxisAlignedBB getCollisionBox(BlockPos pos) {
        World world = mc.theWorld;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // This collects ALL collision boxes for the block in its current state
        List<AxisAlignedBB> boxes = new ArrayList<>();
        block.addCollisionBoxesToList(world, pos, state,
                new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                boxes, null);

        // Return the combined bounding box of all collision components
        if (boxes.isEmpty()) return null;

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (AxisAlignedBB box : boxes) {
            minX = Math.min(minX, box.minX);
            minY = Math.min(minY, box.minY);
            minZ = Math.min(minZ, box.minZ);
            maxX = Math.max(maxX, box.maxX);
            maxY = Math.max(maxY, box.maxY);
            maxZ = Math.max(maxZ, box.maxZ);
        }

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static <T> T getLast(List<T> list) {
        return list.get(list.size()-1);
    }

    public static <T extends Enum<T>> T getNextEnumValue(T current) {
        T[] values = current.getDeclaringClass().getEnumConstants();
        return values[(current.ordinal() + 1) % values.length];
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

    // Used like string.format except now new identifier, "%dp" that uses decimal precision from config for floats/doubles, or e notation settings from config if thats set to true
    public static String formatDp(String format, Object... args) {

        Object[] newArgs = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg instanceof Double || arg instanceof Float) {

                double val = ((Number) arg).doubleValue();
                double abs = Math.abs(val);

                if (GeneralConfig.eNotation && abs > 0) {

                    int exponent = (int) Math.floor(Math.log10(abs));

                    if (exponent <= GeneralConfig.eNotationMaxExp) {

                        double leading = abs / Math.pow(10, exponent);
                        if (val < 0) leading = -leading;

                        String leadingStr = String.format("%." + GeneralConfig.decimalPrecision + "f", leading);

                        newArgs[i] = leadingStr + "e" + exponent;
                        continue;
                    }
                }

                // fallback normal decimal
                newArgs[i] = String.format("%." + GeneralConfig.decimalPrecision + "f", val);

            } else {
                newArgs[i] = arg;
            }
        }

        // replace ALL %dp with %s since we preformatted numbers
        format = format.replace("%dp", "%s");

        return String.format(format, newArgs);
    }

    public static boolean lastOnGround = true;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null) return;

        lastOnGround = onGround();
    }




}
