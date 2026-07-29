package net.colourlabs.bucketpatches.patches.coreprotect;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.tree.MethodNode;

@TargetClass("net.coreprotect.listener.PlayerListener")
public class PlayerListenerPatches {
    @TransformMethod("onPlayerArmorStandManipulateEvent")
    public static void useThreadPool1(MethodNode method) {
        ThreadPool.replaceNewThread(method);
    }

    @TransformMethod("onPlayerInteract")
    public static void useThreadPool2(MethodNode method) {
        ThreadPool.replaceNewThread(method);
    }
}
