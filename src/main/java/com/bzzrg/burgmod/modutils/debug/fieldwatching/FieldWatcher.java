package com.bzzrg.burgmod.modutils.debug.fieldwatching;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.bzzrg.burgmod.modutils.debug.fieldwatching.FieldWatcherConfig.SPECS;

public class FieldWatcher {

    public static boolean BYPASS = false;

    public static void write(Object obj, int fieldId, boolean newVal) { writeBoxed(obj, fieldId, newVal); }
    public static void write(Object obj, int fieldId, byte newVal)    { writeBoxed(obj, fieldId, newVal); }
    public static void write(Object obj, int fieldId, char newVal)    { writeBoxed(obj, fieldId, newVal); }
    public static void write(Object obj, int fieldId, short newVal)   { writeBoxed(obj, fieldId, newVal); }
    public static void write(Object obj, int fieldId, int newVal)     { writeBoxed(obj, fieldId, newVal); }
    public static void write(Object obj, int fieldId, float newVal)   { writeBoxed(obj, fieldId, newVal); }
    public static void write(Object obj, int fieldId, long newVal)    { writeBoxed(obj, fieldId, newVal); }
    public static void write(Object obj, int fieldId, double newVal)  { writeBoxed(obj, fieldId, newVal); }

    private static void writeBoxed(Object obj, int fieldId, Object newVal) {

        if (!FieldWatcherConfig.ENABLED) {
            BYPASS = true;
            try {
                set(obj, fieldId, newVal);
            } finally {
                BYPASS = false;
            }
            return;
        }

        Object oldVal = get(obj, fieldId);

        if (FieldWatcherConfig.shouldLogFieldWrite(obj) && valuesDifferent(oldVal, newVal)) {
            log(fieldId, oldVal, newVal);
        }

        BYPASS = true;
        try {
            set(obj, fieldId, newVal);
        } finally {
            BYPASS = false;
        }
    }

    private static boolean valuesDifferent(Object oldVal, Object newVal) {
        if (oldVal == null) return newVal != null;
        return !oldVal.equals(newVal);
    }

    private static void log(int fieldId, Object oldVal, Object newVal) {

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

        String shortTrace = caller == null
                ? "unknown"
                : caller.getClassName() + "." + caller.getMethodName() + ":" + caller.getLineNumber();

        String name = fieldName(fieldId);

        StringBuilder full = new StringBuilder();
        for (StackTraceElement s : trace) {
            full.append(s).append('\n');
        }

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

    public static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new HashMap<>();
    private static final Map<String, Integer> FIELD_IDS = new HashMap<>();
    private static int NEXT_ID = 1;

    static {
        for (Spec spec : SPECS) {
            spec.id = NEXT_ID++;
            FIELD_IDS.put(spec.name, spec.id);
        }
    }

    public static boolean isValidClass(String clazz) {
        for (Spec spec : SPECS) {
            if (spec.clazz.equals(clazz)) return true;
        }
        return false;
    }

    public static String fieldName(int id) {
        for (Spec spec : SPECS) {
            if (spec.id == id) return spec.name;
        }
        return null;
    }

    public static int fieldId(String name) {
        Integer id = FIELD_IDS.get(name);
        return id == null ? 0 : id;
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
        } catch (Throwable ignored) {}
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

    public static class FieldWriteTransformer implements IClassTransformer {

        private static final String WATCHER = "com/bzzrg/burgmod/modutils/debug/fieldwatching/FieldWatcher";

        @Override
        public byte[] transform(String name, String transformedName, byte[] bytes) {

            if (bytes == null) return null;
            if (name.startsWith("com.bzzrg.burgmod.utils.debug")) return bytes;

            ClassNode cn = new ClassNode();
            new ClassReader(bytes).accept(cn, 0);

            boolean modified = false;

            for (MethodNode mn : cn.methods) {

                InsnList insns = mn.instructions;
                if (insns == null || insns.size() == 0) continue;

                for (AbstractInsnNode insn : insns.toArray()) {

                    if (insn.getOpcode() != Opcodes.PUTFIELD) continue;

                    FieldInsnNode fin = (FieldInsnNode) insn;

                    if (!isValidClass(fin.owner)) continue;

                    int fieldId = fieldId(fin.name);
                    if (fieldId == 0) continue;

                    Type fieldType = Type.getType(fin.desc);
                    String watcherDesc = watcherDesc(fieldType);
                    if (watcherDesc == null) continue;

                    int valueIndex = mn.maxLocals;
                    int objectIndex = valueIndex + fieldType.getSize();
                    mn.maxLocals += fieldType.getSize() + 1;

                    LabelNode doPut = new LabelNode();
                    LabelNode after = new LabelNode();

                    InsnList injected = new InsnList();

                    injected.add(new VarInsnNode(storeOpcode(fieldType), valueIndex));
                    injected.add(new VarInsnNode(Opcodes.ASTORE, objectIndex));

                    injected.add(new FieldInsnNode(Opcodes.GETSTATIC, WATCHER, "BYPASS", "Z"));
                    injected.add(new JumpInsnNode(Opcodes.IFNE, doPut));

                    injected.add(new VarInsnNode(Opcodes.ALOAD, objectIndex));
                    injected.add(new LdcInsnNode(fieldId));
                    injected.add(new VarInsnNode(loadOpcode(fieldType), valueIndex));
                    injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, WATCHER, "write", watcherDesc, false));
                    injected.add(new JumpInsnNode(Opcodes.GOTO, after));

                    injected.add(doPut);
                    injected.add(new VarInsnNode(Opcodes.ALOAD, objectIndex));
                    injected.add(new VarInsnNode(loadOpcode(fieldType), valueIndex));

                    insns.insertBefore(insn, injected);
                    insns.insert(insn, after);

                    modified = true;
                }
            }

            if (!modified) return bytes;

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        }

        private static String watcherDesc(Type type) {
            switch (type.getSort()) {
                case Type.BOOLEAN: return "(Ljava/lang/Object;IZ)V";
                case Type.BYTE:    return "(Ljava/lang/Object;IB)V";
                case Type.CHAR:    return "(Ljava/lang/Object;IC)V";
                case Type.SHORT:   return "(Ljava/lang/Object;IS)V";
                case Type.INT:     return "(Ljava/lang/Object;II)V";
                case Type.FLOAT:   return "(Ljava/lang/Object;IF)V";
                case Type.LONG:    return "(Ljava/lang/Object;IJ)V";
                case Type.DOUBLE:  return "(Ljava/lang/Object;ID)V";
                default:           return null;
            }
        }

        private static int loadOpcode(Type type) {
            switch (type.getSort()) {
                case Type.INT: return Opcodes.ILOAD;
                case Type.FLOAT: return Opcodes.FLOAD;
                case Type.LONG: return Opcodes.LLOAD;
                case Type.DOUBLE: return Opcodes.DLOAD;
                default: return Opcodes.ILOAD;
            }
        }

        private static int storeOpcode(Type type) {
            switch (type.getSort()) {
                case Type.INT: return Opcodes.ISTORE;
                case Type.FLOAT: return Opcodes.FSTORE;
                case Type.LONG: return Opcodes.LSTORE;
                case Type.DOUBLE: return Opcodes.DSTORE;
                default: return Opcodes.ISTORE;
            }
        }
    }

    @IFMLLoadingPlugin.Name("BurgModFieldWatcher")
    @IFMLLoadingPlugin.MCVersion("1.8.9")
    public static class CoremodLoader implements IFMLLoadingPlugin {

        @Override
        public String[] getASMTransformerClass() {
            return new String[] {
                    "com.bzzrg.burgmod.utils.debug.fieldwatching.FieldWatcher$FieldWriteTransformer"
            };
        }

        @Override public String getModContainerClass() { return null; }
        @Override public String getSetupClass() { return null; }
        @Override public void injectData(Map<String, Object> data) {}
        @Override public String getAccessTransformerClass() { return null; }
    }
}