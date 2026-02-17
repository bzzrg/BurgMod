package com.bzzrg.burgmod.utils;

import com.bzzrg.burgmod.features.inputstatus.InputStatusLabel;
import com.bzzrg.burgmod.features.inputstatus.StrategyRecorder;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Set;

public class ResetHandler {

    public static boolean movedSinceReset = true;

    private static boolean pendingTeleport = false;
    private static double lastX, lastY, lastZ;

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        pendingTeleport = true;
    }

    public static Set<StrategyTick.InputType> getInputs() {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (player == null) {
            throw new IllegalStateException("Failed to get player inputs: thePlayer variable is null");
        }

        Set<StrategyTick.InputType> inputs = new HashSet<>();

        if (mc.gameSettings.keyBindForward.isKeyDown()) inputs.add(StrategyTick.InputType.W);
        if (mc.gameSettings.keyBindLeft.isKeyDown()) inputs.add(StrategyTick.InputType.A);
        if (mc.gameSettings.keyBindBack.isKeyDown()) inputs.add(StrategyTick.InputType.S);
        if (mc.gameSettings.keyBindRight.isKeyDown()) inputs.add(StrategyTick.InputType.D);

        if (player.isSprinting()) inputs.add(StrategyTick.InputType.SPR);
        if (player.isSneaking()) inputs.add(StrategyTick.InputType.SNK);
        if (!player.onGround) inputs.add(StrategyTick.InputType.AIR);

        return inputs;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTickChange(TickEvent.ClientTickEvent event) {

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (event.phase != TickEvent.Phase.END || player == null) return;

        if (player.posX - lastX == 0 && player.posY - lastY == 0 && player.posZ - lastZ == 0) {

            if (pendingTeleport && player.onGround) {
                pendingTeleport = false;

                movedSinceReset = false;
                InputStatusLabel.onReset();
                StrategyRecorder.onReset();

            }
        } else {
            movedSinceReset = true;
        }

        lastX = player.posX;
        lastY = player.posY;
        lastZ = player.posZ;

    }
}
