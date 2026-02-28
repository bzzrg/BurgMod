package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.sim.UpdateSimOptions;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.utils.sim.SimUtils.*;

public class StrategyPreviewer {

    private static final HashMap<Vec3, Vec3> lineLocPairs = new HashMap<>();
    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

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

    public static void draw() {
        if (mc.thePlayer == null) return;

        bmChat("\u00A7aPreviewing Strategy! \u00A7e(Note: Must be at reset location when clicking preview button so it's accurate)");

        bmChat("\u00A77Note: Strategy simulation is SLIGHTLY inaccurate in its current state, but is currently being worked on right now.");


        EntityPlayerSP sim = createPlayerSim(mc.thePlayer);
        boolean lastAir = false;

        for (StrategyTick tick : strategyTicks) {

            //logPlayerState("Strat Preview Sim T" + (tick.getTickNum()+1), sim);

            sim.lastTickPosX = sim.posX;
            sim.lastTickPosY = sim.posY;
            sim.lastTickPosZ = sim.posZ;

            Vec3 oldPos = sim.getPositionVector();
            updateSim(sim, new UpdateSimOptions(
                    tick.correctInputs.contains(InputType.W),
                    tick.correctInputs.contains(InputType.A),
                    tick.correctInputs.contains(InputType.S),
                    tick.correctInputs.contains(InputType.D),
                    tick.correctInputs.contains(InputType.SPR),
                    tick.correctInputs.contains(InputType.SNK),
                    tick.correctInputs.contains(InputType.AIR) && !lastAir,
                    null));

            Vec3 newPos = sim.getPositionVector();
            lineLocPairs.put(oldPos, newPos);
            lastAir = tick.correctInputs.contains(InputType.AIR);
        }

        scheduledExecutor.schedule(() -> mc.addScheduledTask(lineLocPairs::clear), 5, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {

        for (Map.Entry<Vec3, Vec3> entry : lineLocPairs.entrySet()) {
            drawLine(entry.getKey(), entry.getValue(), 0.7f, 0, 0, 0.3f, 0.1f);
        }
    }


    /*
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.phase != TickEvent.Phase.END ||  player== null) {
            return;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_P)) logPlayerState("Real Player", player);


    }

     */

}
