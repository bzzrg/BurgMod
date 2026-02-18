package com.bzzrg.burgmod.utils.resetting;

import com.bzzrg.burgmod.features.inputstatus.InputStatusLabel;
import com.bzzrg.burgmod.features.inputstatus.StrategyRecorder;
import com.bzzrg.burgmod.features.poschecker.PosCheckerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ResetHandler {

    public static boolean movedSinceReset = true;
    private static double lastX = 0, lastY = 0, lastZ = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        if (TeleportTracker.teleportedThisTick) {
            movedSinceReset = false;
            InputStatusLabel.onReset();
            StrategyRecorder.onReset();
            PosCheckerHandler.onReset();
        } else if (player.posX - lastX != 0 || player.posY - lastY != 0 || player.posZ - lastZ != 0) {
            movedSinceReset = true;
        }

        lastX = player.posX;
        lastY = player.posY;
        lastZ = player.posZ;
    }

}
