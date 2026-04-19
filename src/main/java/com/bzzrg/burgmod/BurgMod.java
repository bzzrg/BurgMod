package com.bzzrg.burgmod;

import com.bzzrg.burgmod.config.MainConfigGui;
import com.bzzrg.burgmod.config.MainConfigGuiBinds;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.PosCheckersConfig;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.StratRemindersConfig;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig;
import com.bzzrg.burgmod.config.files.mainconfigsections.*;
import com.bzzrg.burgmod.config.files.utils.MainConfigSection;
import com.bzzrg.burgmod.features.distance.DistanceOffsetHandler;
import com.bzzrg.burgmod.features.distance.EmptyCommand;
import com.bzzrg.burgmod.features.inputstatus.InputStatusHandler;
import com.bzzrg.burgmod.features.perfect45offset.FixStrat45sCommand;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetDrawer;
import com.bzzrg.burgmod.features.perfect45offset.P45OffsetHandler;
import com.bzzrg.burgmod.features.poschecker.PosCheckersDrawer;
import com.bzzrg.burgmod.features.poschecker.PosCheckersHandler;
import com.bzzrg.burgmod.features.strategy.AutoHPKLoader;
import com.bzzrg.burgmod.features.strategy.StrategyPreviewer;
import com.bzzrg.burgmod.features.strategy.StrategyRecorder;
import com.bzzrg.burgmod.features.strategy.TickNumLabelHandler;
import com.bzzrg.burgmod.features.trajectory.TrajectoryHandler;
import com.bzzrg.burgmod.features.turnhelper.TurnHelperDrawer;
import com.bzzrg.burgmod.features.turnhelper.TurnHelperHandler;
import com.bzzrg.burgmod.modutils.GeneralUtils;
import com.bzzrg.burgmod.modutils.TaskScheduler;
import com.bzzrg.burgmod.modutils.debug.EveryTickDebug;
import com.bzzrg.burgmod.modutils.resetting.ResetHandler;
import com.bzzrg.burgmod.modutils.resetting.TeleportTracker;
import com.bzzrg.burgmod.newversionnotifier.DownloadLatestCommand;
import com.bzzrg.burgmod.newversionnotifier.NewVersionNotifier;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static com.bzzrg.burgmod.modutils.GeneralUtils.createDirectory;

@Mod(modid = BurgMod.MODID, name = BurgMod.MODNAME, version = BurgMod.VERSION)
public class BurgMod {

    public static final String MODID = "burgmod";
    public static final String MODNAME = "BurgMod";
    public static final String VERSION = "1.4.0";

    public static Minecraft mc;
    public static Logger logger;
    public static File currentModJar = null;

    public static File modConfigFolder;

    @EventHandler
    public void preInitialize(FMLPreInitializationEvent event) {
        mc = Minecraft.getMinecraft();
        logger = event.getModLog();
        currentModJar = event.getSourceFile();

        MainConfigSection.mainConfigFile = event.getSuggestedConfigurationFile();
        modConfigFolder = new File(event.getModConfigurationDirectory(), "BurgMod");
        createDirectory(modConfigFolder);

        new DistanceOffsetConfig();
        new GeneralConfig();
        new InputStatusConfig();
        new P45OffsetConfig();
        new TrajectoryConfig();
        MainConfigSection.updateFields();

        PosCheckersConfig.instance.updateFields();
        StrategyConfig.instance.updateFields();
        StratRemindersConfig.instance.updateFields();
        TurnHelperConfig.instance.updateFields();


    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {

        ClientCommandHandler.instance.registerCommand(new FixStrat45sCommand());
        ClientCommandHandler.instance.registerCommand(new DownloadLatestCommand());

        ClientCommandHandler.instance.registerCommand(new EmptyCommand("bm"));

        // if the command isn't already registered (from MPKMod or CyvClient/CyvForge), then register an empty command to avoid invalid usage.
        // If this is just running before the other mods' code, then this will be overridden because last registration takes priority
        if (ClientCommandHandler.instance.getCommands().get("mpk") == null) {
            ClientCommandHandler.instance.registerCommand(new EmptyCommand("mpk"));
        }
        if (ClientCommandHandler.instance.getCommands().get("cyv") == null) {
            ClientCommandHandler.instance.registerCommand(new EmptyCommand("cyv"));
        }

        MainConfigGui.initOptions();
        MinecraftForge.EVENT_BUS.register(new MainConfigGuiBinds());

        MinecraftForge.EVENT_BUS.register(new ResetHandler());
        MinecraftForge.EVENT_BUS.register(new NewVersionNotifier());
        MinecraftForge.EVENT_BUS.register(new AutoHPKLoader());
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
        MinecraftForge.EVENT_BUS.register(new GeneralUtils());
        MinecraftForge.EVENT_BUS.register(new TurnHelperDrawer());
        MinecraftForge.EVENT_BUS.register(new TurnHelperHandler());
        MinecraftForge.EVENT_BUS.register(new TickNumLabelHandler());
        MinecraftForge.EVENT_BUS.register(new DistanceOffsetHandler());


    }
}
