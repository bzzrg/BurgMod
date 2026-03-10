package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.simulation.UpdateSimOptions;
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
import static com.bzzrg.burgmod.utils.simulation.SimUtils.*;

public class StrategyPreviewer {

    public static final HashMap<Vec3, Vec3> lineLocPairs = new HashMap<>();
    public static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public static void draw() {
        if (mc.thePlayer == null) return;

        bmChat("\u00A7aPreviewing Strategy! \u00A7e(Note: Must be at reset location when clicking preview button so it's accurate)");

        EntityPlayerSP sim = createPlayerSim(mc.thePlayer);
        boolean lastAir = false;

        for (StrategyTick tick : strategyTicks) {

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

}
