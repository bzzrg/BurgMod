package com.bzzrg.burgmod.debug.fieldwatching;

import com.bzzrg.burgmod.debug.FieldWatchConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

public final class FieldWatcher {

    public static boolean BYPASS = false;

    private FieldWatcher() {}

    public static void write(Object obj, int fieldId, float newVal) {

        if (!FieldWatchConfig.ENABLED) {
            BYPASS = true;
            try {
                FieldWatchConfig.set(obj, fieldId, newVal);
            } finally {
                BYPASS = false;
            }
            return;
        }

        float oldVal = FieldWatchConfig.get(obj, fieldId);

        if (FieldWatchConfig.shouldLog(obj) && oldVal != newVal) {
            log(fieldId, oldVal, newVal);
        }

        BYPASS = true;
        try {
            FieldWatchConfig.set(obj, fieldId, newVal);
        } finally {
            BYPASS = false;
        }
    }

    private static void log(int fieldId, float oldVal, float newVal) {

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        StackTraceElement caller = null;
        for (StackTraceElement s : trace) {
            String c = s.getClassName();
            if (c.equals(FieldWatcher.class.getName())) continue;
            if (c.startsWith("java.")) continue;
            if (c.startsWith("sun.")) continue;
            caller = s;
            break;
        }

        String shortTrace = (caller == null)
                ? "unknown"
                : caller.getClassName() + "." + caller.getMethodName() + ":" + caller.getLineNumber();

        String name = FieldWatchConfig.fieldName(fieldId);

        StringBuilder full = new StringBuilder();
        for (StackTraceElement s : trace)
            full.append(s).append('\n');

        String traceString = full.toString();

        System.out.println("[FieldWatch] " + name + " " + oldVal + " -> " + newVal + " @ " + shortTrace);

        if (Minecraft.getMinecraft().thePlayer == null) return;

        ChatComponentText base =
                new ChatComponentText("[FieldWatch] " + name + " " + oldVal + " -> " + newVal + " ");

        ChatComponentText traceText = new ChatComponentText("[TRACE]");

        traceText.setChatStyle(new ChatStyle()
                .setChatHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText(traceString)
                ))
        );

        base.appendSibling(traceText);

        Minecraft.getMinecraft().thePlayer.addChatMessage(base);
    }
}