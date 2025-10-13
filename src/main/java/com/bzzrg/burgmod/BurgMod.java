package com.bzzrg.burgmod;

import com.bzzrg.burgmod.config.InputStatusConfig;
import com.bzzrg.burgmod.inputstatus.InputStatusLabel;
import com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyEditorBind;
import com.bzzrg.burgmod.inputstatus.strategyeditor.StrategyEditorGui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = BurgMod.MODID, name = BurgMod.MODNAME, version = BurgMod.VERSION)
public class BurgMod {

    public static final String MODID = "burgmod";
    public static final String MODNAME = "BurgMod";
    public static final String VERSION = "1.0.1";

    @EventHandler
    public void preInitialize(FMLPreInitializationEvent event) {
        InputStatusConfig.load(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new MainCommand());
        KeyBinding bind = new KeyBinding("Open Strategy Editor", Keyboard.KEY_O, "BurgMod");
        ClientRegistry.registerKeyBinding(bind);
        MinecraftForge.EVENT_BUS.register(new StrategyEditorBind(bind));
        MinecraftForge.EVENT_BUS.register(new InputStatusLabel());
        StrategyEditorGui.loadStrategy();
    }
}
