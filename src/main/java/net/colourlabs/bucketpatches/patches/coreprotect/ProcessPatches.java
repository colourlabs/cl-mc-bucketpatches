package net.colourlabs.bucketpatches.patches.coreprotect;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.sql.Connection;
import java.sql.SQLException;

@TargetClass("net.coreprotect.consumer.Process")
public class ProcessPatches {

    public static boolean safeIsValid(Connection conn, int timeout) {
        try {
            return conn.isValid(timeout);
        } catch (SQLException e) {
            return true; // SQLite throws SQLFeatureNotSupportedException; assume valid
        }
    }

    @TransformMethod("validateConnection")
    public static void fixIsValid(MethodNode method) {
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode mi = (MethodInsnNode) insn;
                if (mi.owner.equals("java/sql/Connection") && mi.name.equals("isValid")) {
                    method.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "net/colourlabs/bucketpatches/patches/coreprotect/ProcessPatches",
                        "safeIsValid",
                        "(Ljava/sql/Connection;I)Z", false));
                }
            }
        }
    }
}
