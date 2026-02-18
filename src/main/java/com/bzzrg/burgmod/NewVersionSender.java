package com.bzzrg.burgmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class NewVersionSender {

    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {

        // New thread so accessing github is async
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/bzzrg/BurgMod/main/version.txt").openStream()))) {

                // Get and compare latest version
                String latest = reader.readLine();
                System.out.printf("latest: %s, actual: %s%n", latest, BurgMod.VERSION);

                if (latest == null || latest.equals(BurgMod.VERSION)) return; // Stops code under this from running if player has latest version already

                // Create message
                IChatComponent msg = new ChatComponentText("\u00A71[BurgMod]\u00A7r \u00A7rA new version of BurgMod is available! \u00A77(" + BurgMod.VERSION + " \u2192 " + latest + ") ");
                IChatComponent link = new ChatComponentText("\u00A79[Click to Download]");
                link.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/bzzrg/BurgMod/releases"));
                msg.appendSibling(link);

                // Send message
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                if (player != null) {
                    Minecraft.getMinecraft().addScheduledTask(() -> player.addChatMessage(msg)); // Back to main thread
                }

            } catch (Exception ignored) {}
        }, "BurgMod-New-Version-Message").start();

    }
}
