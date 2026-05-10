package com.bzzrg.burgmod.command;

import com.bzzrg.burgmod.features.collisionoffset.CollisionOffsetHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.HashSet;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.modutils.GeneralUtils.getBlockLookingAt;

public class BMCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "bm";
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
    public void processCommand(ICommandSender sender, String[] args) {
        String argString = String.join(" ", args);

        BlockPos blockLookingAt = getBlockLookingAt();

        if ("setxcol".equals(argString)) {

            if (blockLookingAt == null) {
                bmChat("\u00A7cPlease look at a valid block!");
            } else {
                CollisionOffsetHandler.xColBlock = blockLookingAt;
                bmChat("\u00A7aSet X collision block to the block you are looking at!");
            }

        } else if ("setzcol".equals(argString)) {

            if (blockLookingAt == null) {
                bmChat("\u00A7cPlease look at a valid block!");
            } else {
                CollisionOffsetHandler.zColBlock = blockLookingAt;
                bmChat("\u00A7aSet Z collision block to the block you are looking at!");
            }

        } else if ("setbothcol".equals(argString)) {

            if (blockLookingAt == null) {
                bmChat("\u00A7cPlease look at a valid block!");
            } else {
                CollisionOffsetHandler.xColBlock = blockLookingAt;
                CollisionOffsetHandler.zColBlock = blockLookingAt;
                bmChat("\u00A7aSet X and Z collision block to the block you are looking at!");
            }

        } else if (!new HashSet<>(Arrays.asList("setlb", "setmm", "setlb target", "setmm target")).contains(argString)) {
            sendBMUsage();
        }

    }

    public static void sendBMUsage() {
        bmChat("\u00A7bCommand Usage (/bm):");
        sendBMBullet("setlb", "Sets landing block to the block you are standing on (used for Distance Offset). Using /mpk or /cyv works for this too.");
        sendBMBullet("setmm", "Sets momentum block to the block you are standing on (used for Distance Offset). Using /mpk or /cyv works for this too.");
        sendBMBullet("setlb target", "Sets landing block to the block you are looking at (used for Distance Offset). Using /mpk or /cyv works for this too.");
        sendBMBullet("setmm target", "Sets momentum block to the block you are looking at (used for Distance Offset). Using /mpk or /cyv works for this too.");
        sendBMBullet("setxcol", "Sets X collision block to the block you are looking at (used for Collision Offset).");
        sendBMBullet("setzcol", "Sets Z collision block to the block you are looking at (used for Collision Offset).");
        sendBMBullet("setbothcol", "Sets X and Z collision block to the block you are looking at (used for Collision Offset).");
    }

    private static void sendBMBullet(String args, String info) {

        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;

        IChatComponent nonInfoComp = new ChatComponentText("\u00A77- \u00A7e/bm " + args + " ");
        IChatComponent infoComp = new ChatComponentText("\u00A76\u00A7l[INFO]");
        infoComp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("\u00A76" + info)));
        player.addChatMessage(nonInfoComp.appendSibling(infoComp));

    }

}
