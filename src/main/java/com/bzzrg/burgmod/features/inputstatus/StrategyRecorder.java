package com.bzzrg.burgmod.features.inputstatus;

import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.utils.ResetHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.utils.ResetHandler.getInputs;

public class StrategyRecorder {

    public static boolean recording = false;
    public static final List<Set<StrategyTick.InputType>> recordedStrategy = new ArrayList<>();

    public static void onReset() {
        if (recording) {
            recordedStrategy.clear();
        }
    }

    @SubscribeEvent
    public void onTickChange(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || Minecraft.getMinecraft().thePlayer == null || !recording || InputStatusLabel.finished || !ResetHandler.movedSinceReset) {
            return;
        }

        // Get player's current inputs
        Set<StrategyTick.InputType> inputs = getInputs();
        recordedStrategy.add(inputs);

        System.out.println("added inputs to recordedStrategy: " + inputs);

    }

}
