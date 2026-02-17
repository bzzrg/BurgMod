package com.bzzrg.burgmod.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class MainConfigGuiBind {

    private final KeyBinding bind;

    public MainConfigGuiBind(KeyBinding bind) {
        this.bind = bind;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (bind.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        }
    }

}
