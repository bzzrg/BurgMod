package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.utils.resetting.ResetHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.bzzrg.burgmod.utils.GeneralUtils.getInputs;

public class StrategyRecorder {

    public static boolean recording = false;
    public static final List<Set<InputType>> recordedStrategy = new ArrayList<>();

    public static void onReset() {
        if (recording) {
            recordedStrategy.clear();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || Minecraft.getMinecraft().thePlayer == null || !recording || !ResetHandler.movedSinceReset) {
            return;
        }

        // Get player's current inputs
        Set<InputType> inputs = getInputs();
        recordedStrategy.add(inputs);
    }

}
