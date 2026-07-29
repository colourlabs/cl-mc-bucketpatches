package net.colourlabs.bucketpatches.patches.coreprotect;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
    }

    public static void replaceNewThread(MethodNode method) {
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (!(insn instanceof MethodInsnNode)) continue;
            MethodInsnNode mi = (MethodInsnNode) insn;
            if (!mi.owner.equals("java/lang/Thread") || !mi.name.equals("start")) continue;

            List<AbstractInsnNode> toRemove = new ArrayList<>();

            AbstractInsnNode curr = insn;
            toRemove.add(curr);
            curr = curr.getPrevious();

            if (curr instanceof VarInsnNode && curr.getOpcode() == Opcodes.ALOAD) {
                toRemove.add(curr);
                curr = curr.getPrevious();
            }

            if (curr instanceof VarInsnNode && curr.getOpcode() == Opcodes.ASTORE) {
                toRemove.add(curr);
                curr = curr.getPrevious();
            }

            if (!(curr instanceof MethodInsnNode)) continue;
            MethodInsnNode init = (MethodInsnNode) curr;
            if (!init.owner.equals("java/lang/Thread") || !init.name.equals("<init>")) continue;

            toRemove.add(curr);
            curr = curr.getPrevious();

            if (!(curr instanceof VarInsnNode) || curr.getOpcode() != Opcodes.ALOAD) continue;
            int runnableVar = ((VarInsnNode) curr).var;
            toRemove.add(curr);
            curr = curr.getPrevious();

            if (curr.getOpcode() != Opcodes.DUP) continue;
            toRemove.add(curr);
            curr = curr.getPrevious();

            if (!(curr instanceof TypeInsnNode) || curr.getOpcode() != Opcodes.NEW) continue;
            TypeInsnNode newType = (TypeInsnNode) curr;
            if (!newType.desc.equals("java/lang/Thread")) continue;
            toRemove.add(curr);

            InsnList replacement = new InsnList();
            replacement.add(new VarInsnNode(Opcodes.ALOAD, runnableVar));
            replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                "net/colourlabs/bucketpatches/patches/coreprotect/ThreadPool",
                "execute", "(Ljava/lang/Runnable;)V", false));

            method.instructions.insertBefore(curr, replacement);
            for (int i = toRemove.size() - 1; i >= 0; i--) {
                method.instructions.remove(toRemove.get(i));
            }
        }
    }
}
