package com.bzzrg.burgmod.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;

public final class FieldWatchConfig {
    private FieldWatchConfig() {}
    public static boolean ENABLED = false;

    public static final int F_MOVE_FORWARD  = 1;
    public static final int F_MOVE_STRAFING = 2;

    public static String fieldName(int id) {
        switch (id) {
            case F_MOVE_FORWARD:  return "moveForward";
            case F_MOVE_STRAFING: return "moveStrafing";
        }
        return null;
    }

    public static int fieldId(String name) {
        switch (name) {
            case "moveForward":  return F_MOVE_FORWARD;
            case "moveStrafing": return F_MOVE_STRAFING;
        }
        return 0;
    }

    public static float get(Object obj, int id) {

        EntityLivingBase e = (EntityLivingBase)obj;

        switch (id) {
            case F_MOVE_FORWARD:  return e.moveForward;
            case F_MOVE_STRAFING: return e.moveStrafing;
        }

        return 0f;
    }

    public static void set(Object obj, int id, float v) {

        EntityLivingBase e = (EntityLivingBase)obj;

        switch (id) {
            case F_MOVE_FORWARD:  e.moveForward = v; break;
            case F_MOVE_STRAFING: e.moveStrafing = v; break;
        }
    }

    public static boolean isValidOwner(String owner) {
        return owner.equals("net/minecraft/entity/EntityLivingBase")
                || owner.equals("net/minecraft/client/entity/EntityPlayerSP");
    }

    public static boolean shouldLog(Object obj) {

        if (!(obj instanceof EntityLivingBase))
            return false;

        EntityLivingBase e = (EntityLivingBase)obj;

        if (!(e instanceof EntityPlayerSP))
            return false;

        if (Keyboard.isKeyDown(Keyboard.KEY_P) && e == Minecraft.getMinecraft().thePlayer)
            return true;

        return Keyboard.isKeyDown(Keyboard.KEY_O) && e != Minecraft.getMinecraft().thePlayer;
    }


}