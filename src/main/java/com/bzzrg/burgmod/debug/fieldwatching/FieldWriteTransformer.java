package com.bzzrg.burgmod.debug.fieldwatching;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class FieldWriteTransformer implements IClassTransformer {

    private static final String WATCHER = "com/bzzrg/burgmod/debug/fieldwatching/FieldWatcher";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if (bytes == null) return null;
        if (name.startsWith("com.bzzrg.burgmod.debug")) return bytes;

        ClassNode cn = new ClassNode();
        new ClassReader(bytes).accept(cn, 0);

        boolean modified = false;

        for (MethodNode mn : cn.methods) {

            InsnList insns = mn.instructions;
            if (insns == null || insns.size() == 0) continue;

            for (AbstractInsnNode insn : insns.toArray()) {

                if (insn.getOpcode() != Opcodes.PUTFIELD) continue;

                FieldInsnNode fin = (FieldInsnNode) insn;

                if (!FieldWatchConfig.isValidOwner(fin.owner)) continue;

                int fieldId = FieldWatchConfig.fieldId(fin.name);
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

                injected.add(new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        WATCHER,
                        "BYPASS",
                        "Z"
                ));
                injected.add(new JumpInsnNode(Opcodes.IFNE, doPut));

                injected.add(new VarInsnNode(Opcodes.ALOAD, objectIndex));
                injected.add(new LdcInsnNode(fieldId));
                injected.add(new VarInsnNode(loadOpcode(fieldType), valueIndex));
                injected.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        WATCHER,
                        "write",
                        watcherDesc,
                        false
                ));
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
            case Type.BOOLEAN:
            case Type.BYTE:
            case Type.CHAR:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.ILOAD;
            case Type.FLOAT:
                return Opcodes.FLOAD;
            case Type.LONG:
                return Opcodes.LLOAD;
            case Type.DOUBLE:
                return Opcodes.DLOAD;
            default:
                throw new IllegalArgumentException("Unsupported field type: " + type);
        }
    }

    private static int storeOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.BYTE:
            case Type.CHAR:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.ISTORE;
            case Type.FLOAT:
                return Opcodes.FSTORE;
            case Type.LONG:
                return Opcodes.LSTORE;
            case Type.DOUBLE:
                return Opcodes.DSTORE;
            default:
                throw new IllegalArgumentException("Unsupported field type: " + type);
        }
    }
}