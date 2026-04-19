package com.bzzrg.burgmod.modutils.debug;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static com.bzzrg.burgmod.BurgMod.mc;

public class EveryTickDebug {

    public static final boolean ENABLED = false;

    public static void logPlayerState(String tag, EntityPlayerSP p) {

        System.out.printf(
                "%s pos=(%.15f, %.15f, %.15f) " +
                        "mot=(%.15f, %.15f, %.15f) " +
                        "onG=%s colH=%s colV=%s " +
                        "yaw=%.5f " +
                        "mf=%.5f msf=%.5f " +
                        "miF=%.5f miS=%.5f " +
                        "jmpFactor=%.5f aiSpeed=%.5f " +
                        "spr=%s snk=%s usingItem=%s%n",

                tag,

                // position
                p.posX, p.posY, p.posZ,

                // motion
                p.motionX, p.motionY, p.motionZ,

                // collision / ground (affects physics heavily)
                p.onGround, p.isCollidedHorizontally, p.isCollidedVertically,

                // direction (affects movement vector)
                p.rotationYaw,

                // movement inputs (raw + processed)
                p.moveForward, p.moveStrafing,
                (p.movementInput != null ? p.movementInput.moveForward : Float.NaN),
                (p.movementInput != null ? p.movementInput.moveStrafe : Float.NaN),

                // movement scaling
                p.jumpMovementFactor,
                p.getAIMoveSpeed(),

                // modifiers
                p.isSprinting(), p.isSneaking(), p.isUsingItem()
        );

    }
    private boolean wasPDown = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = mc.thePlayer;
        if (!ENABLED || event.phase != TickEvent.Phase.END || player == null) return;


        if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_P) && !wasPDown) {
        }

        wasPDown = Keyboard.isKeyDown(Keyboard.KEY_P);


    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (mc.thePlayer == null) return;

        if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
        }
    }


}
