package com.bzzrg.burgmod.modutils.debug.fieldwatching;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;

public final class FieldWatcherConfig {
    private FieldWatcherConfig() {}

    public static final boolean enabled = false;

    public static final Spec[] specs = {
            new Spec("net/minecraft/entity/Entity", "posX"),
            new Spec("net/minecraft/entity/Entity", "posY"),
            new Spec("net/minecraft/entity/Entity", "posZ"),

    };

    public static boolean shouldLogFieldWrite(Object owner) {
        if (!(owner instanceof EntityPlayerSP)) return false;

        EntityPlayerSP player = (EntityPlayerSP) owner;

        if (Keyboard.isKeyDown(Keyboard.KEY_I) && player == Minecraft.getMinecraft().thePlayer) {
            return true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_U) && player != Minecraft.getMinecraft().thePlayer) {
            return true;
        }

        return false;
    }


}