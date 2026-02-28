package com.bzzrg.burgmod.utils.resetting;

import io.netty.channel.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class TeleportTracker {

    private static final String HANDLER_NAME = "burgmod_tp_hook";

    public static volatile boolean teleportedThisTick = false;

    private static volatile boolean installed = false;
    private static volatile Channel installedOn = null;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            teleportedThisTick = false;
            tryInstall();
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        uninstall();
    }

    private static void uninstall() {
        installed = false;

        Channel ch = installedOn;
        installedOn = null;

        if (ch == null) return;

        ch.eventLoop().execute(() -> {
            try {
                ChannelPipeline p = ch.pipeline();
                if (p.get(HANDLER_NAME) != null) p.remove(HANDLER_NAME);
            } catch (Throwable ignored) {
            }
        });
    }

    private static void tryInstall() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getNetHandler() == null) return;

        NetworkManager nm = mc.getNetHandler().getNetworkManager();
        if (nm == null) return;

        Channel ch = nm.channel();
        if (ch == null) return;

        // If we changed servers/worlds, force reinstall on the new channel.
        if (installed && installedOn != ch) {
            uninstall();
        }
        if (installed) return;

        ch.eventLoop().execute(() -> {
            ChannelPipeline p = ch.pipeline();

            if (p.get(HANDLER_NAME) != null) {
                installed = true;
                installedOn = ch;
                return;
            }

            // Some servers/pipelines might not have "packet_handler" yet; guard it.
            if (p.get("packet_handler") == null) return;

            p.addBefore("packet_handler", HANDLER_NAME, new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (msg instanceof S08PacketPlayerPosLook) {
                        teleportedThisTick = true;
                    }
                    super.channelRead(ctx, msg);
                }
            });

            installed = true;
            installedOn = ch;
        });
    }
}