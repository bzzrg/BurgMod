package com.bzzrg.burgmod.features.inputstatus;

import com.bzzrg.burgmod.config.basicconfig.InputStatusConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyRecorder;
import com.bzzrg.burgmod.utils.resetting.ResetHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Set;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.basicconfig.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.utils.GeneralUtils.getInputs;

public class InputStatusLabel {

    private static int tickNum = 0;
    public static boolean finished = false;
    public static String label = color1 + "Input Status: \u00A74Not Loaded";

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (InputStatusConfig.enabled) {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(label, InputStatusConfig.labelX, InputStatusConfig.labelY, 0xFFFFFF);
        }
    }

    public static void onReset() {
        tickNum = 0;
        finished = false;
        label = InputStatusConfig.shortenLabel ? color1 + "Input Status: \u00A7e..." : color1 + "Input Status: \u00A7eWaiting...";
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || finished || !ResetHandler.movedSinceReset || StrategyRecorder.recording) {
            if (StrategyRecorder.recording) label = color1 + "Input Status: \u00A7bRecording Strategy...";
            return;
        }

        // Set label to be started
        if (tickNum == 0) {
            label = InputStatusConfig.shortenLabel ? color1 + "Input Status: \u00A7a\u2714" : color1 + "Input Status: \u00A7aGood";
        }

        // Get player's current inputs
        Set<InputType> inputs = getInputs();

        // Get correct inputs
        Set<InputType> correctInputs;
        try {
            correctInputs = new HashSet<>(strategyTicks.get(tickNum).correctInputs);

        } catch (Exception e) { // If tickNum is out of the range of strategyTicks, that means the strategy is complete and this code will run
            label = InputStatusConfig.shortenLabel ? color1 + "Input Status: \u00A7d\u2714" : color1 + "Input Status: \u00A7dSuccess";
            finished = true;
            return;
        }

        // Input fail logic
        if (!inputs.equals(correctInputs)) {

            String failed = InputStatusConfig.shortenLabel ? "\u2716" : "Failed";
            label = color1 + "Input Status: \u00A7c" + failed;

            if (InputStatusConfig.showFailTick) label += " (T" + (tickNum+1) + ")";

            if (InputStatusConfig.showFailReason) {

                StringBuilder failReason = new StringBuilder();
                failReason.append(" (");

                for (InputType type : InputType.values()) {
                    boolean pressed = inputs.contains(type);
                    boolean needed  = correctInputs.contains(type);
                    if (!pressed && !needed) continue;

                    if (failReason.length() > 2) failReason.append("\u00A7c, ");

                    if (pressed && needed) {
                        failReason.append("\u00A7a").append(type.name());
                    } else if (needed) {
                        failReason.append("\u00A7e\u00A7m").append(type.name());
                    } else {
                        failReason.append("\u00A7e\u00A7l").append(type.name());
                    }

                    failReason.append("\u00A7r");
                }

                failReason.append("\u00A7c)");

                label += failReason;
            }

            finished = true;

        } else {
            tickNum++;
        }
    }


}
