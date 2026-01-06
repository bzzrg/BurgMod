package com.bzzrg.burgmod.inputstatus;

import com.bzzrg.burgmod.config.InputStatusConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bzzrg.burgmod.config.InputStatusConfig.color1;
import static com.bzzrg.burgmod.config.InputStatusConfig.shortenLabel;
import static com.bzzrg.burgmod.helpers.ModHelper.scaledX;
import static com.bzzrg.burgmod.helpers.ModHelper.scaledY;
import static com.bzzrg.burgmod.inputstatus.StrategyTick.strategyTicks;

public class InputSLabel {

    private int tickNum = 0;
    private boolean finished = false;
    private String inputStatus = color1 + "Input Status: \u00A74Not Loaded";

    private boolean pendingTeleport = false;
    private double lastX, lastY, lastZ;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (InputStatusConfig.label) Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(inputStatus, scaledX(InputStatusConfig.labelX), scaledY(InputStatusConfig.labelY), 0xFFFFFF);
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        pendingTeleport = true;
    }

    private boolean isInAir() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double yDecimal = (player.posY * 10000) % 1;
        return yDecimal > 0.0001 && 1 - yDecimal > 0.0001;
    }

    @SubscribeEvent
    public void onTickChange(TickEvent.ClientTickEvent event) {

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.phase != TickEvent.Phase.END || player == null) return;

        if (pendingTeleport && !isInAir() && player.posX - lastX == 0 && player.posY - lastY == 0 && player.posZ - lastZ == 0) {
            tickNum = 0;
            finished = false;
            pendingTeleport = false;
        }

        lastX = player.posX;
        lastY = player.posY;
        lastZ = player.posZ;

        updateInputStatusLabel();
    }

    private void updateInputStatusLabel() {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        // Return if no strategy has been set
        if (strategyTicks.isEmpty()) {
            tickNum = 0;
            finished = false;
            inputStatus = color1 + "Input Status: \u00A7bNo Strategy";
            return;
        }

        // Return if jump is finished
        if (finished) return;

        // Get player's current inputs
        Set<InputType> inputs = new HashSet<>();

        if (mc.gameSettings.keyBindForward.isKeyDown()) inputs.add(InputType.W);
        if (mc.gameSettings.keyBindLeft.isKeyDown()) inputs.add(InputType.A);
        if (mc.gameSettings.keyBindBack.isKeyDown()) inputs.add(InputType.S);
        if (mc.gameSettings.keyBindRight.isKeyDown()) inputs.add(InputType.D);

        if (player.isSprinting()) inputs.add(InputType.SPR);
        if (player.isSneaking()) inputs.add(InputType.SNK);
        if (isInAir()) inputs.add(InputType.AIR);

        // Get correct inputs
        Set<InputType> correctInputs;
        try {
            correctInputs = new HashSet<>(strategyTicks.get(tickNum).correctInputs);
        } catch (Exception e) {
            inputStatus = shortenLabel ? color1 + "Input Status: \u00A7d\u2714" : color1 + "Input Status: \u00A7dSuccess";
            finished = true;
            return;
        }

        // Disregard SPR inputs if toggleSprintMode is on
        if (InputStatusConfig.toggleSprintMode) {
            correctInputs.removeIf(input -> input == InputType.SPR);
            inputs.removeIf(input -> input == InputType.SPR);
        }

        // Set label to be waiting if player hasn't inputted anything yet
        if (tickNum == 0 && inputs.isEmpty()) {
            inputStatus = shortenLabel ? color1 + "Input Status: \u00A7e..." : color1 + "Input Status: \u00A7eWaiting...";
            return;
        }

        if (tickNum == 0) inputStatus = shortenLabel ? color1 + "Input Status: \u00A7a\u2714" : color1 + "Input Status: \u00A7aGood"; // Set label to be started

        // Input fail logic
        if (!inputs.equals(correctInputs)) {

            Set<InputType> missing = new HashSet<>(correctInputs);
            missing.removeAll(inputs);

            Set<InputType> extra = new HashSet<>(inputs);
            extra.removeAll(correctInputs);

            Comparator<InputType> byEnum = Comparator.comparingInt(Enum::ordinal);

            String reason = Stream.concat(missing.stream().sorted(byEnum).map(i -> "\u00A7e" + i), extra.stream().sorted(byEnum).map(i -> "\u00A7a" + i)).collect(Collectors.joining("\u00A7c,"));
            String failed = shortenLabel ? "\u2716" : "Failed";

            StrategyJump jump = strategyTicks.get(tickNum).jump;
            if (jump == null) {
                inputStatus = color1 + "Input Status: \u00A7c" + failed + " (T" + (tickNum + 1) + " | " + reason + "\u00A7c)";
            } else {
                inputStatus = color1 + "Input Status: \u00A7c" + failed + " (" + jump.getName() + " | T" + (jump.ticks.indexOf(strategyTicks.get(tickNum)) + 1) + " | " + reason + "\u00A7c)";
            }

            finished = true;

        } else {
            tickNum++;
        }

    }
}
