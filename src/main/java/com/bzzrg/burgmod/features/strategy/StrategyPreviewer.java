package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.simulation.PlayerSim;
import com.bzzrg.burgmod.utils.simulation.UpdateSimOptions;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.utils.debug.EveryTickDebug.logPlayerState;
import static com.bzzrg.burgmod.utils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.*;

public class StrategyPreviewer {

    public static final Map<Vec3, Vec3> locPairs = new HashMap<>();
    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final List<ScheduledFuture<?>> tasks = new ArrayList<>();

    public static void draw() {
        if (mc.thePlayer == null) return;

        tasks.forEach(task -> task.cancel(false));
        tasks.clear();
        locPairs.clear();

        bmChat("\u00A7aPreviewing Strategy! \u00A7e(Note: Must be at reset location when clicking preview button so it's accurate)");

        PlayerSim sim = createSim();
        updateSim(sim, null);

        logPlayerState("Preview Sim", sim);

        for (StrategyTick tick : strategyTicks) {

            Vec3 oldPos = sim.getPositionVector();
            updateSim(sim, new UpdateSimOptions(
                    tick.correctInputs.contains(InputType.W),
                    tick.correctInputs.contains(InputType.A),
                    tick.correctInputs.contains(InputType.S),
                    tick.correctInputs.contains(InputType.D),
                    tick.correctInputs.contains(InputType.SPR),
                    tick.correctInputs.contains(InputType.SNK),
                    tick.correctInputs.contains(InputType.JMP),
                    null));

            Vec3 newPos = sim.getPositionVector();
            locPairs.put(oldPos, newPos);

            System.out.printf("Preview Sim Pos: %s%n", newPos);
        }

        tasks.add(scheduledExecutor.schedule(() -> mc.addScheduledTask(locPairs::clear), 5, TimeUnit.SECONDS));
    }
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {

        for (Map.Entry<Vec3, Vec3> entry : locPairs.entrySet()) {
            drawLine(entry.getKey(), entry.getValue(), 0.7f, 0, 0, 0.3f, 0.1f);
        }
    }

}
