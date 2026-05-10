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

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (StrategyConfig.showTickNum) {

            mc.fontRendererObj.drawStringWithShadow(String.format("%sTick #: %s%d", color1, color2, tickNum), StrategyConfig.tickNumLabelX, StrategyConfig.tickNumLabelY, -1);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null) {
            return;
        }

        if (ResetHandler.movedSinceReset) {
            tickNum++;
        } else {
            tickNum = 0;
        }
    }

}
