package com.bzzrg.burgmod.debug.fieldwatching;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class FieldWatchConfig {
    private FieldWatchConfig() {}

    public static boolean ENABLED = false;

    public static final int F_MOVE_FORWARD  = 1;
    public static final int F_MOVE_STRAFING = 2;
    public static final int F_POS_X         = 3;
    public static final int F_POS_Z         = 4;

    private static final Spec[] SPECS = {
            //new Spec(F_MOVE_FORWARD,  "moveForward"),
            //new Spec(F_MOVE_STRAFING, "moveStrafing"),
            new Spec(F_POS_X,         "posX"),
            new Spec(F_POS_Z,         "posZ"),
    };

    public static boolean isValidOwner(String owner) {
        return owner.equals("net/minecraft/entity/EntityLivingBase")
                || owner.equals("net/minecraft/client/entity/EntityPlayerSP")
                || owner.equals("net/minecraft/entity/Entity");
    }

    public static boolean shouldLog(Object obj) {
        if (!(obj instanceof EntityPlayerSP)) return false;

        EntityPlayerSP player = (EntityPlayerSP) obj;

        if (Keyboard.isKeyDown(Keyboard.KEY_P) && player == Minecraft.getMinecraft().thePlayer) {
            return true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_O) && player != Minecraft.getMinecraft().thePlayer) {
            return true;
        }

        return false;
    }


    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new HashMap<>();

    private static final class Spec {
        final int id;
        final String name;

        Spec(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static String fieldName(int id) {
        for (Spec spec : SPECS) {
            if (spec.id == id) return spec.name;
        }
        return null;
    }

    public static int fieldId(String name) {
        for (Spec spec : SPECS) {
            if (spec.name.equals(name)) return spec.id;
        }
        return 0;
    }

    public static Object get(Object obj, int id) {
        try {
            Field field = getField(obj.getClass(), fieldName(id));
            return field == null ? null : field.get(obj);
        } catch (Throwable t) {
            return null;
        }
    }

    public static void set(Object obj, int id, Object v) {
        try {
            Field field = getField(obj.getClass(), fieldName(id));
            if (field != null) field.set(obj, v);
        } catch (Throwable ignored) {
        }
    }

    private static Field getField(Class<?> cls, String name) {
        if (name == null) return null;

        Map<String, Field> byName = FIELD_CACHE.get(cls);
        if (byName != null && byName.containsKey(name)) {
            return byName.get(name);
        }

        if (byName == null) {
            byName = new HashMap<>();
            FIELD_CACHE.put(cls, byName);
        }

        Class<?> current = cls;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                byName.put(name, field);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        byName.put(name, null);
        return null;
    }
}