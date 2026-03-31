package com.bzzrg.burgmod.features.poschecker;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.PosCheckersConfig;
import com.bzzrg.burgmod.modutils.TaskScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.bzzrg.burgmod.config.files.jsonconfigfiles.PosCheckersConfig.*;
import static com.bzzrg.burgmod.modutils.GeneralUtils.*;

public class PosCheckersHandler {


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (!PosCheckersConfig.enabled && event.phase != TickEvent.Phase.END || player == null) return;

        boolean validCoords = xMin <= player.posX && player.posX <= xMax &&
                zMin <= player.posZ && player.posZ <= zMax;

        if (validCoords && lastOnGround && !onGround()) {

            for (PosChecker posChecker : posCheckers) {

                TaskScheduler.schedule(posChecker.airtime-1, () -> { // -1 on airtime because if you want to send on first air tick (airtime = 1), well this code runs on the first air tick

                    if (posChecker.axis == Axis.X) {
                        bmChat(formatDp("X: %dp \u00A77(T%d)", player.posX, posChecker.airtime));
                    } else if (posChecker.axis == Axis.Z) {
                        bmChat(formatDp("Z: %dp \u00A77(T%d)", player.posZ, posChecker.airtime));
                    } else if (posChecker.axis == Axis.BOTH) {
                        bmChat(formatDp("X: %dp, Z: %dp \u00A77(T%d)", player.posX, player.posZ, posChecker.airtime));
                    }

                });

            }

        }

    }

    public static void onReset() {
        TaskScheduler.clearTasks();
    }

}