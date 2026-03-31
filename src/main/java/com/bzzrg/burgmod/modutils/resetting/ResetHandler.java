package com.bzzrg.burgmod.modutils.resetting;

import com.bzzrg.burgmod.config.files.mainconfigsections.InputStatusConfig;
import com.bzzrg.burgmod.config.files.mainconfigsections.P45OffsetConfig;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.PosCheckersConfig;
import com.bzzrg.burgmod.features.inputstatus.InputStatusHandler;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetHandler;
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

    public static void globalReset() {
        movedSinceReset = false;
        if (InputStatusConfig.enabled) InputStatusHandler.onReset();
        if (StrategyRecorder.recording) StrategyRecorder.onReset();
        if (PosCheckersConfig.enabled) PosCheckersHandler.onReset();
        if (P45OffsetConfig.enabled) P45OffsetHandler.onReset();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = mc.thePlayer;

        if (event.phase != TickEvent.Phase.END || player == null) {
            return;
        }

        if (TeleportTracker.tpedThisTick) {
            globalReset();
        } else if (player.posX - lastX != 0 || player.posY - lastY != 0 || player.posZ - lastZ != 0) {
            movedSinceReset = true;
        }

        lastX = player.posX;
        lastY = player.posY;
        lastZ = player.posZ;
    }
}
