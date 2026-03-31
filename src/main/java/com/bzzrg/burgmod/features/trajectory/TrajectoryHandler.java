package com.bzzrg.burgmod.features.trajectory;

import com.bzzrg.burgmod.config.files.mainconfigsections.TrajectoryConfig;
import com.bzzrg.burgmod.modutils.debug.EveryTickDebug;
import com.bzzrg.burgmod.modutils.simulation.PlayerSim;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.modutils.simulation.SimUtils.*;

public class TrajectoryHandler {

    public static boolean allow = false;

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        EntityPlayerSP real = mc.thePlayer;

        if (!TrajectoryConfig.enabled || real == null) return;

        PlayerSim sim = createSim();

        for (int i = 0; i < TrajectoryConfig.tickLength; i++) {
            Vec3 oldPos = new Vec3(sim.posX, sim.posY, sim.posZ);
            updateSim(sim, null);
            Vec3 newPos = new Vec3(sim.posX, sim.posY, sim.posZ);

            drawLine(oldPos, newPos, TrajectoryConfig.colorRed, TrajectoryConfig.colorGreen, TrajectoryConfig.colorBlue, TrajectoryConfig.alpha, TrajectoryConfig.thickness);

            if (allow) EveryTickDebug.logPlayerState("Traj Sim", sim);
        }

        allow = false;

    }

}