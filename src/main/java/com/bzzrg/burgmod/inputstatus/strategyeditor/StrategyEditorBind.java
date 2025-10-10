package com.bzzrg.burgmod.inputstatus.strategyeditor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class StrategyEditorBind {

    private final KeyBinding bind;

    public StrategyEditorBind(KeyBinding bind) {
        this.bind = bind;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (bind.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new StrategyEditorGui());
        }
    }

}
