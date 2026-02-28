package com.bzzrg.burgmod.features.poschecker;

import com.bzzrg.burgmod.config.basicconfig.GeneralConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;

public class PosMessageSender {
    private final Axis axis;
    private final int airtime;
    private int ticksLeft;

    public PosMessageSender(PosChecker posChecker) {
        this.axis = posChecker.axis;
        this.airtime = posChecker.airtime;
        this.ticksLeft = posChecker.airtime;
    }

    public void tick() {
        ticksLeft--;
        if (ticksLeft <= 0) {

            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

            if (player != null) {
                if (axis == Axis.X) {
                    bmChat(String.format("X: %." + GeneralConfig.decimalPlaces + "f \u00A77(T%d)", player.posX, airtime));
                } else if (axis == Axis.Z) {
                    bmChat(String.format("Z: %." + GeneralConfig.decimalPlaces + "f \u00A77(T%d)", player.posZ, airtime));
                } else if (axis == Axis.BOTH) {
                    bmChat(String.format("X: %." + GeneralConfig.decimalPlaces + "f, Z: %." + GeneralConfig.decimalPlaces + "f \u00A77(T%d)",
                            player.posX, player.posZ, airtime));
                }
            }

            PosCheckersHandler.posMessageSenders.remove(this);
        }
    }
}