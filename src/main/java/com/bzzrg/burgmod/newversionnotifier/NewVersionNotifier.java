package com.bzzrg.burgmod.newversionnotifier;

import com.bzzrg.burgmod.BurgMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.modutils.GeneralUtils.chat;

public class NewVersionNotifier {

    private static boolean ranForThisJoin = false;
    public static String cachedDownloadUrl = "";

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (mc.thePlayer == null || mc.theWorld == null) {
            ranForThisJoin = false;
            return;
        }
        if (ranForThisJoin) return;
        ranForThisJoin = true;

        new Thread(() -> {
            try {
                BufferedReader versionReader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/bzzrg/BurgMod/main/version.txt").openStream()));
                String latestVersion = versionReader.readLine();
                versionReader.close();

                if (latestVersion.equals(BurgMod.VERSION)) return;

                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.github.com/repos/bzzrg/BurgMod/releases/latest").openConnection();
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

                BufferedReader apiReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = apiReader.readLine()) != null) {
                    response.append(line);
                }
                apiReader.close();

                JsonObject release = new JsonParser().parse(response.toString()).getAsJsonObject();
                String changelog = release.get("body").getAsString();
                cachedDownloadUrl = release.getAsJsonArray("assets").get(0).getAsJsonObject().get("browser_download_url").getAsString();

                mc.addScheduledTask(() -> {
                    if (mc.thePlayer == null) return;

                    bmChat("\u00A7bNew BurgMod version available! \u00A77(" + BurgMod.VERSION + " \u2192 " + latestVersion + ")");

                    chat("\u00A76----------- Changelog -----------");
                    for (String changelogLine : changelog.split("\\r?\\n")) {
                        chat("\u00A7e" + changelogLine.replace("\r", ""));
                    }
                    chat("\u00A76--------------------------------");

                    IChatComponent buttons = new ChatComponentText("");

                    IChatComponent webButton = new ChatComponentText("\u00A79[Open Download Page]");
                    webButton.getChatStyle().setChatClickEvent(new ClickEvent(
                            ClickEvent.Action.OPEN_URL, "https://github.com/bzzrg/BurgMod/releases/tag/v" + latestVersion));

                    IChatComponent autoButton = new ChatComponentText(" \u00A7a[Auto Download]");
                    autoButton.getChatStyle().setChatClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND, "/bmdownloadlatest"));

                    buttons.appendSibling(webButton);
                    buttons.appendSibling(autoButton);
                    mc.thePlayer.addChatMessage(buttons);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}