package net.colourlabs.bucketpatches.patches.advancedteleport;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@TargetClass("io.github.niestrat99.advancedteleport.sql.SQLManager")
public class SQLManagerPatches {
    @TransformMethod("implementConnection")
    public static void fixMySQLDriver(MethodNode method) {
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode) insn;
                if ("com.mysql.jdbc.Driver".equals(ldc.cst)) {
                    ldc.cst = "com.mysql.cj.jdbc.Driver";
                }
            }
        }
    }
}
