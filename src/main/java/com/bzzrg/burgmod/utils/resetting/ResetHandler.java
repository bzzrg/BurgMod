package com.bzzrg.burgmod.utils.resetting;

import com.bzzrg.burgmod.features.inputstatus.InputStatusLabel;
import com.bzzrg.burgmod.features.perfect45offset.Perfect45OffsetLabel;
import com.bzzrg.burgmod.features.poschecker.PosCheckersHandler;
import com.bzzrg.burgmod.features.strategy.StrategyRecorder;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.bzzrg.burgmod.BurgMod.mc;

public class ResetHandler {

    public static boolean movedSinceReset = true;
    private static double lastX = 0, lastY = 0, lastZ = 0;

    private static boolean initCall = true;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = mc.thePlayer;

        if (event.phase != TickEvent.Phase.END || player == null) {
            return;
        }

        if (TeleportTracker.tpedThisTick || initCall) {

            movedSinceReset = false;
            InputStatusLabel.onReset();
            StrategyRecorder.onReset();
            PosCheckersHandler.onReset();
            Perfect45OffsetLabel.onReset();

            initCall = false;
        } else if (player.posX - lastX != 0 || player.posY - lastY != 0 || player.posZ - lastZ != 0) {
            movedSinceReset = true;
        }

        lastX = player.posX;
        lastY = player.posY;
        lastZ = player.posZ;
    }
}
