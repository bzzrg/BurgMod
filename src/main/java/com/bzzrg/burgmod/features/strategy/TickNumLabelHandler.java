package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig;
import com.bzzrg.burgmod.modutils.resetting.ResetHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color2;

public class TickNumLabelHandler {

    private static int tickNum = 0;
    private static String label = color1 + "Tick #: \u00A7r?";

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (StrategyConfig.showTickNum) {
            mc.fontRendererObj.drawStringWithShadow(label, StrategyConfig.tickNumLabelX, StrategyConfig.tickNumLabelY, -1);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null) {
            return;
        }

        if (ResetHandler.movedSinceReset) {
            tickNum++;
            label = String.format("%sTick #: %s%d", color1, color2, tickNum);
        } else {
            tickNum = 0;
            label = String.format("%sTick #: %s...", color1, color2);
        }
    }

}
