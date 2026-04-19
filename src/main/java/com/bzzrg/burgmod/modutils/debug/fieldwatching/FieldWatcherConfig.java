package com.bzzrg.burgmod.modutils.debug.fieldwatching;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;

public final class FieldWatcherConfig {
    private FieldWatcherConfig() {}

    public static final boolean ENABLED = false;

    public static final Spec[] SPECS = {
            new Spec("net/minecraft/entity/Entity", "posX"),
            new Spec("net/minecraft/entity/Entity", "posY"),
            new Spec("net/minecraft/entity/Entity", "posZ"),
    };

    public static boolean shouldLogFieldWrite(Object owner) {
        if (!(owner instanceof EntityPlayerSP)) return false;

        EntityPlayerSP player = (EntityPlayerSP) owner;

        if (player == Minecraft.getMinecraft().thePlayer) {
            return Keyboard.isKeyDown(Keyboard.KEY_L);
        } else {
            return Keyboard.isKeyDown(Keyboard.KEY_K);
        }
    }


}