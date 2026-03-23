package com.bzzrg.burgmod.utils.simulation;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class PlayerSim extends EntityPlayerSP {

    public PlayerSim() {
        super(Minecraft.getMinecraft(),
                Minecraft.getMinecraft().thePlayer.worldObj,
                new AntiPacketNetHandler(Minecraft.getMinecraft(), Minecraft.getMinecraft().getNetHandler().getNetworkManager(), Minecraft.getMinecraft().getNetHandler().getGameProfile()),
                Minecraft.getMinecraft().thePlayer.getStatFileWriter());
    }

    @Override
    public void playSound(String name, float volume, float pitch) {}

    @Override
    public void onLivingUpdate() {
        this.inPortal = false;
        this.timeInPortal = 0.0F;
        this.prevTimeInPortal = 0.0F;

        super.onLivingUpdate();

        this.inPortal = false;
        this.timeInPortal = 0.0F;
        this.prevTimeInPortal = 0.0F;
    }

    @Override
    public boolean isCurrentViewEntity() {
        return true;
    }

    // Stop minecraft calls to make the sim sprinting, that goes off of if im holding sprint, I make sim sprint manually
    private static class AntiPacketNetHandler extends NetHandlerPlayClient {
        public AntiPacketNetHandler(Minecraft mcIn, NetworkManager nm, GameProfile gp) {
            super(mcIn, mcIn.currentScreen, nm, gp);
        }

        @Override
        public void addToSendQueue(Packet packetIn) {
        }
    }

}