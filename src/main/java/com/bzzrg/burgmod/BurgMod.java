package com.bzzrg.burgmod;

import com.bzzrg.burgmod.command.AutoStrategyLoad;
import com.bzzrg.burgmod.command.MainCommand;
import com.bzzrg.burgmod.config.MainConfigGuiBind;
import com.bzzrg.burgmod.config.ConfigHandler;
import com.bzzrg.burgmod.config.featureconfig.PosCheckerConfig;
import com.bzzrg.burgmod.features.inputstatus.InputStatusLabel;
import com.bzzrg.burgmod.features.inputstatus.StrategyRecorder;
import com.bzzrg.burgmod.features.poschecker.PosCheckerHandler;
import com.bzzrg.burgmod.features.trajectory.TrajectoryHandler;
import com.bzzrg.burgmod.utils.ResetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.File;

import static com.bzzrg.burgmod.config.featureconfig.StrategyConfig.updateStrategyFields;
import static com.bzzrg.burgmod.utils.PluginUtils.createDirectory;

@Mod(modid = BurgMod.MODID, name = BurgMod.MODNAME, version = BurgMod.VERSION)
public class BurgMod {

    public static final String MODID = "burgmod";
    public static final String MODNAME = "BurgMod";
    public static final String VERSION = "1.1.1";

    public static Minecraft mc;
    public static Logger logger;
    public static File modConfigFile;
    public static File modConfigFolder;

    @EventHandler
    public void preInitialize(FMLPreInitializationEvent event) {
        mc = Minecraft.getMinecraft();
        logger = event.getModLog();

        modConfigFile = event.getSuggestedConfigurationFile();
        modConfigFolder = new File(event.getModConfigurationDirectory(), "BurgMod");
        createDirectory(modConfigFolder);

        ConfigHandler.updateFieldsFromConfig();

        PosCheckerConfig.updateFields();

    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new MainCommand());

        KeyBinding bind = new KeyBinding("Open Config Gui", Keyboard.KEY_B, "BurgMod");
        ClientRegistry.registerKeyBinding(bind);
        updateStrategyFields();
        MinecraftForge.EVENT_BUS.register(new MainConfigGuiBind(bind));

        MinecraftForge.EVENT_BUS.register(new NewVersionSender());

        MinecraftForge.EVENT_BUS.register(new AutoStrategyLoad());

        MinecraftForge.EVENT_BUS.register(new ResetHandler());
        MinecraftForge.EVENT_BUS.register(new InputStatusLabel());
        MinecraftForge.EVENT_BUS.register(new StrategyRecorder());

        MinecraftForge.EVENT_BUS.register(new TrajectoryHandler());
        MinecraftForge.EVENT_BUS.register(new PosCheckerHandler());

    }


}
