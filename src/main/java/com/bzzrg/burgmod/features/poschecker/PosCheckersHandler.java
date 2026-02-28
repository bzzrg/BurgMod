package com.bzzrg.burgmod.features.poschecker;

import com.bzzrg.burgmod.config.specialconfig.PosCheckersConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

import static com.bzzrg.burgmod.config.specialconfig.PosCheckersConfig.*;
import static com.bzzrg.burgmod.utils.GeneralUtils.isAirborne;

public class PosCheckersHandler {
    private static boolean lastInAir = false;
    public static final List<PosMessageSender> posMessageSenders = new ArrayList<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.phase != TickEvent.Phase.END || player == null) {
            return;
        }
        if (!PosCheckersConfig.enabled) {
            posMessageSenders.clear();
            return;
        }

        boolean validCoords = xMin <= player.posX && player.posX <= xMax && zMin <= player.posZ && player.posZ <= zMax;
        if (validCoords && isAirborne() && !lastInAir) {
            for (PosChecker posChecker : posCheckers) {
                posMessageSenders.add(new PosMessageSender(posChecker));
            }
        }

        new ArrayList<>(posMessageSenders).forEach(PosMessageSender::tick);

        lastInAir = isAirborne();
    }

    public static void onReset() {
        posMessageSenders.clear();
    }




}
