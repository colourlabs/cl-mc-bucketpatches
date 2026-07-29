package net.colourlabs.bucketpatches.patches.coreprotect;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

@TargetClass("net.coreprotect.database.Database")
public class DatabasePatches {

    @TransformMethod("getConnection")
    public static void fixNewInstance(MethodNode method) {
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode mi = (MethodInsnNode) insn;
                if (mi.owner.equals("java/lang/Class") && mi.name.equals("newInstance")) {
                    method.instructions.remove(insn);
                }
            }
        }
    }

    @TransformMethod("loadUserID")
    public static void fixLoadUserID(MethodNode method) {
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode) insn;
                if ("user LIKE ?".equals(ldc.cst)) {
                    ldc.cst = "user = ?";
                    break;
                }
            }
        }
    }
}
