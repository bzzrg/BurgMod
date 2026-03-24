package com.bzzrg.burgmod;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.bzzrg.burgmod.BurgMod.latestVersion;
import static com.bzzrg.burgmod.BurgMod.mc;

public class NewVersionNotifier {

    private static boolean ranForThisJoin = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (mc.thePlayer == null || mc.theWorld == null) {
            ranForThisJoin = false;
            return;
        }
        if (ranForThisJoin) return;
        ranForThisJoin = true;

        if (latestVersion == null || latestVersion.equals(BurgMod.VERSION)) return;

        IChatComponent msg = new ChatComponentText("\u00A71[BM] \u00A7rA new version of BurgMod is available! \u00A77(" + BurgMod.VERSION + " \u2192 " + latestVersion + ") ");
        IChatComponent link = new ChatComponentText("\u00A79[Click to Download]");
        link.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/bzzrg/BurgMod/releases"));
        msg.appendSibling(link);

        mc.addScheduledTask(() -> {
            if (mc.thePlayer != null) mc.thePlayer.addChatMessage(msg);
        });
    }
}
