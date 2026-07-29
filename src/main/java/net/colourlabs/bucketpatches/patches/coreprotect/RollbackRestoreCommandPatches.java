package net.colourlabs.bucketpatches.patches.coreprotect;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.tree.MethodNode;

@TargetClass("net.coreprotect.command.RollbackRestoreCommand")
public class RollbackRestoreCommandPatches {
    @TransformMethod("runCommand")
    public static void useThreadPool(MethodNode method) {
        ThreadPool.replaceNewThread(method);
    }
}
