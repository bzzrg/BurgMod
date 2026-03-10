package com.bzzrg.burgmod.debug.fieldwatching;

import com.bzzrg.burgmod.debug.FieldWatchConfig;
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
            if (insns == null) continue;

            for (AbstractInsnNode insn : insns.toArray()) {

                if (insn.getOpcode() != Opcodes.PUTFIELD) continue;

                FieldInsnNode fin = (FieldInsnNode) insn;

                if (!FieldWatchConfig.isValidOwner(fin.owner))
                    continue;

                int fieldId = FieldWatchConfig.fieldId(fin.name);
                if (fieldId == 0) continue;

                LabelNode doPut = new LabelNode();
                LabelNode after = new LabelNode();

                InsnList injected = new InsnList();

                injected.add(new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        WATCHER,
                        "BYPASS",
                        "Z"
                ));

                injected.add(new JumpInsnNode(Opcodes.IFNE, doPut));

                injected.add(new InsnNode(Opcodes.SWAP));
                injected.add(new LdcInsnNode(fieldId));
                injected.add(new InsnNode(Opcodes.SWAP));
                injected.add(new InsnNode(Opcodes.DUP_X2));
                injected.add(new InsnNode(Opcodes.POP));
                injected.add(new InsnNode(Opcodes.SWAP));

                injected.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        WATCHER,
                        "write",
                        "(Ljava/lang/Object;IF)V",
                        false
                ));

                injected.add(new JumpInsnNode(Opcodes.GOTO, after));
                injected.add(doPut);

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
}