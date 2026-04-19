package com.bzzrg.burgmod.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;

import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;

public class MainConfigGuiBinds {

    public KeyBinding openGuiBind;
    public Map<KeyBinding, MainConfigGui.Option> optionBinds = new HashMap<>();

    public MainConfigGuiBinds() {

        openGuiBind = new KeyBinding("Open Config Gui", Keyboard.KEY_B, "BurgMod");
        ClientRegistry.registerKeyBinding(openGuiBind);

        for (MainConfigGui.Option option : MainConfigGui.options) {
            KeyBinding bind = new KeyBinding(option.isFeature ? "Toggle " + option.name : option.name, Keyboard.KEY_NONE, "BurgMod");
            optionBinds.put(bind, option);
            ClientRegistry.registerKeyBinding(bind);
        }

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openGuiBind.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new MainConfigGui());
        } else {

            for (Map.Entry<KeyBinding, MainConfigGui.Option> entry : optionBinds.entrySet()) {
                if (entry.getKey().isPressed()) {

                    MainConfigGui.Option option = entry.getValue();
                    option.onMainClick.run();

                    if (option.isFeature) {
                        bmChat(String.format(option.enabledGetter.get() ? "\u00A7aEnabled %s!" : "\u00A7eDisabled %s!", option.name));
                    }

                }
            }

        }
    }

}
