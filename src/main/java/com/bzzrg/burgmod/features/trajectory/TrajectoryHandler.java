package com.bzzrg.burgmod.features.trajectory;

import com.bzzrg.burgmod.config.basicconfig.TrajectoryConfig;
import com.bzzrg.burgmod.utils.simulation.PlayerSim;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.*;

public class TrajectoryHandler {

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
        }

    }

}