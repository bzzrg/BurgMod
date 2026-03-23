package com.bzzrg.burgmod;

import com.bzzrg.burgmod.command.BMCommand;
import com.bzzrg.burgmod.features.poschecker.PosCheckersDrawer;
import com.bzzrg.burgmod.config.basicconfig.BasicConfigHandler;
import com.bzzrg.burgmod.config.MainConfigGuiBind;
import com.bzzrg.burgmod.config.specialconfig.PosCheckersConfig;
import com.bzzrg.burgmod.utils.debug.EveryTickDebug;
import com.bzzrg.burgmod.features.inputstatus.InputStatusHandler;
import com.bzzrg.burgmod.features.perfect45offset.FixStrat45sCommand;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetDrawer;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetHandler;
import com.bzzrg.burgmod.features.poschecker.PosCheckersHandler;
import com.bzzrg.burgmod.features.strategy.AutoStrategyLoad;
import com.bzzrg.burgmod.features.strategy.StrategyPreviewer;
import com.bzzrg.burgmod.features.strategy.StrategyRecorder;
import com.bzzrg.burgmod.features.trajectory.TrajectoryHandler;
import com.bzzrg.burgmod.utils.TaskScheduler;
import com.bzzrg.burgmod.utils.resetting.ResetHandler;
import com.bzzrg.burgmod.utils.resetting.TeleportTracker;
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

import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.updateStrategyFields;
import static com.bzzrg.burgmod.utils.GeneralUtils.createDirectory;

@Mod(modid = BurgMod.MODID, name = BurgMod.MODNAME, version = BurgMod.VERSION)
public class BurgMod {

    public static final String MODID = "burgmod";
    public static final String MODNAME = "BurgMod";
    public static final String VERSION = "1.2.0";

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

        BasicConfigHandler.updateFields();
        PosCheckersConfig.updateFields();

    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new BMCommand());
        ClientCommandHandler.instance.registerCommand(new FixStrat45sCommand());

        KeyBinding bind = new KeyBinding("Open Config Gui", Keyboard.KEY_B, "BurgMod");
        ClientRegistry.registerKeyBinding(bind);
        updateStrategyFields();

        MinecraftForge.EVENT_BUS.register(new ResetHandler());
        MinecraftForge.EVENT_BUS.register(new MainConfigGuiBind(bind));
        MinecraftForge.EVENT_BUS.register(new NewVersionSender());
        MinecraftForge.EVENT_BUS.register(new AutoStrategyLoad());
        MinecraftForge.EVENT_BUS.register(new InputStatusHandler());
        MinecraftForge.EVENT_BUS.register(new StrategyRecorder());
        MinecraftForge.EVENT_BUS.register(new TrajectoryHandler());
        MinecraftForge.EVENT_BUS.register(new PosCheckersHandler());
        MinecraftForge.EVENT_BUS.register(new TeleportTracker());
        MinecraftForge.EVENT_BUS.register(new PosCheckersDrawer());
        MinecraftForge.EVENT_BUS.register(new StrategyPreviewer());
        MinecraftForge.EVENT_BUS.register(new EveryTickDebug());
        MinecraftForge.EVENT_BUS.register(new P45OffsetHandler());
        MinecraftForge.EVENT_BUS.register(new TaskScheduler());
        MinecraftForge.EVENT_BUS.register(new P45OffsetDrawer());

    }
}
