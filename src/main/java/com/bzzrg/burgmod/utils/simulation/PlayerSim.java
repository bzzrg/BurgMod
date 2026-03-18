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
    public void updateEntityActionState() {
        if (!this.isCurrentViewEntity()) {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
            this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
        }
        super.updateEntityActionState();
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