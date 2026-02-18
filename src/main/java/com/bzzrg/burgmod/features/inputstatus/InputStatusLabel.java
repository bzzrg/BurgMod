package com.bzzrg.burgmod.features.inputstatus;

import com.bzzrg.burgmod.config.featureconfig.InputStatusConfig;
import com.bzzrg.burgmod.features.strategy.StrategyJump;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.utils.resetting.ResetHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bzzrg.burgmod.config.ConfigHandler.color1;
import static com.bzzrg.burgmod.config.featureconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.utils.PluginUtils.getInputs;

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

        if (event.phase != TickEvent.Phase.END || Minecraft.getMinecraft().thePlayer == null || finished || !ResetHandler.movedSinceReset) {
            return;
        }

        if (StrategyRecorder.recording) {
            label = color1 + "Input Status: \u00A7bRecording Strategy...";
            return;
        }

        // Set label to be started
        if (tickNum == 0) {
            label = InputStatusConfig.shortenLabel ? color1 + "Input Status: \u00A7a\u2714" : color1 + "Input Status: \u00A7aGood";
        }

        // Get player's current inputs
        Set<StrategyTick.InputType> inputs = getInputs();

        // Get correct inputs
        Set<StrategyTick.InputType> correctInputs;
        try {
            correctInputs = new HashSet<>(strategyTicks.get(tickNum).correctInputs);

        } catch (Exception e) { // If tickNum is out of the range of strategyTicks, that means the strategy is complete and this code will run
            label = InputStatusConfig.shortenLabel ? color1 + "Input Status: \u00A7d\u2714" : color1 + "Input Status: \u00A7dSuccess";
            finished = true;
            return;
        }

        // Input fail logic
        if (!inputs.equals(correctInputs)) {

            Set<StrategyTick.InputType> missing = new HashSet<>(correctInputs);
            missing.removeAll(inputs);

            Set<StrategyTick.InputType> extra = new HashSet<>(inputs);
            extra.removeAll(correctInputs);

            Comparator<StrategyTick.InputType> byEnum = Comparator.comparingInt(Enum::ordinal);

            String reason = Stream.concat(missing.stream().sorted(byEnum).map(i -> "\u00A7e" + i), extra.stream().sorted(byEnum).map(i -> "\u00A7a" + i)).collect(Collectors.joining("\u00A7c,"));
            String failed = InputStatusConfig.shortenLabel ? "\u2716" : "Failed";

            StrategyJump jump = strategyTicks.get(tickNum).jump;
            if (jump == null) {
                label = color1 + "Input Status: \u00A7c" + failed + " (T" + (tickNum + 1) + " | " + reason + "\u00A7c)";
            } else {
                label = color1 + "Input Status: \u00A7c" + failed + " (" + jump.getName() + " | T" + (jump.ticks.indexOf(strategyTicks.get(tickNum)) + 1) + " | " + reason + "\u00A7c)";
            }

            finished = true;

        } else {
            tickNum++;
        }
    }


}
