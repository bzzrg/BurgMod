package com.bzzrg.burgmod.utils.resetting;

import io.netty.channel.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TeleportTracker {

    private static final String HANDLER_NAME = "burgmod_tp_hook";

    public static volatile boolean teleportedThisTick = false;

    private static boolean installed = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            teleportedThisTick = false;
            tryInstall();
        }
    }

    private static void tryInstall() {
        if (installed) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.getNetHandler() == null) return;

        NetworkManager nm = mc.getNetHandler().getNetworkManager();
        if (nm == null) return;

        Channel ch = nm.channel();
        if (ch == null) return;

        ch.eventLoop().execute(() -> {
            ChannelPipeline p = ch.pipeline();
            if (p.get(HANDLER_NAME) != null) {
                installed = true;
                return;
            }

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
        });
    }
}
