package com.bzzrg.burgmod.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class EveryTickDebug {

    public static void logPlayerState(String tag, EntityPlayerSP p) {

        // Avoid Vec3.toString formatting differences; print explicit doubles/floats
        System.out.printf(
                "%s pos=(%.15f, %.15f, %.15f) prevPos=(%.15f, %.15f, %.15f) lastTickPos=(%.15f, %.15f, %.15f) " +
                        "mot=(%.15f, %.15f, %.15f) " +
                        "onG=%s colH=%s colV=%s col=%s airBorne=%s " +
                        "spr=%s snk=%s usingItem=%s " +
                        "yaw=%.5f pitch=%.5f yawHead=%.5f renderYawOff=%.5f " +
                        "mf=%.5f msf=%.5f jmpFactor=%.5f " +
                        "miF=%.5f miS=%.5f miJ=%s miSn=%s " +
                        "inWater=%s onLadder=%s%n",
                tag,
                p.posX, p.posY, p.posZ,
                p.prevPosX, p.prevPosY, p.prevPosZ,
                p.lastTickPosX, p.lastTickPosY, p.lastTickPosZ,
                p.motionX, p.motionY, p.motionZ,
                p.onGround, p.isCollidedHorizontally, p.isCollidedVertically, p.isCollided, p.isAirBorne,
                p.isSprinting(), p.isSneaking(), p.isUsingItem(),
                p.rotationYaw, p.rotationPitch, p.rotationYawHead, p.renderYawOffset,
                p.moveForward, p.moveStrafing, p.jumpMovementFactor,
                (p.movementInput != null ? p.movementInput.moveForward : Float.NaN),
                (p.movementInput != null ? p.movementInput.moveStrafe : Float.NaN),
                (p.movementInput != null && p.movementInput.jump),
                (p.movementInput != null && p.movementInput.sneak),
                p.isInWater(), p.isOnLadder()
        );

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.phase != TickEvent.Phase.END || player == null) {
            return;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_P)) System.out.printf("Real is sprinting: %s%n", player.isSprinting());

    }
}
