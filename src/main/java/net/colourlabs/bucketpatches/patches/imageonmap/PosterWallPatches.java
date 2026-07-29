package net.colourlabs.bucketpatches.patches.imageonmap;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

@TargetClass("fr.moribus.imageonmap.ui.PosterWall")
public class PosterWallPatches {
    private static final String CACHE_OWNER = "net/colourlabs/bucketpatches/patches/imageonmap/ChunkEntityCache";
    private static final String GET_ENTITIES_DESC = "(Lorg/bukkit/Chunk;)[Lorg/bukkit/entity/Entity;";

    @TransformMethod("isValid")
    public static void cacheEntitiesInIsValid(MethodNode method) {
        injectClearAtHead(method);
        replaceGetEntities(method);
    }

    @TransformMethod("getMatchingMapFrames")
    public static void cacheEntitiesInGetMatchingFrames(MethodNode method) {
        injectClearAtHead(method);
        replaceGetEntities(method);
    }

    @TransformMethod("getMapFrameAt")
    public static void cacheGetMapFrameAt(MethodNode method) {
        replaceGetEntities(method);
    }

    @TransformMethod("getEmptyFrameAt")
    public static void cacheGetEmptyFrameAt(MethodNode method) {
        replaceGetEntities(method);
    }

    private static void injectClearAtHead(MethodNode method) {
        method.instructions.insertBefore(method.instructions.getFirst(),
            new MethodInsnNode(Opcodes.INVOKESTATIC, CACHE_OWNER, "clear", "()V", false));
    }

    private static void replaceGetEntities(MethodNode method) {
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (!(insn instanceof MethodInsnNode)) continue;
            MethodInsnNode mi = (MethodInsnNode) insn;
            if (!mi.owner.equals("org/bukkit/Chunk") || !mi.name.equals("getEntities")) continue;

            AbstractInsnNode prev = insn.getPrevious();
            if (!(prev instanceof MethodInsnNode)) continue;
            MethodInsnNode prevMi = (MethodInsnNode) prev;
            if (!prevMi.name.equals("getChunk")) continue;
            if (!prevMi.owner.equals("org/bukkit/Location")
                && !prevMi.owner.equals("fr/moribus/imageonmap/tools/world/FlatLocation")) continue;

            method.instructions.set(insn, new MethodInsnNode(
                Opcodes.INVOKESTATIC, CACHE_OWNER, "getEntities", GET_ENTITIES_DESC, false));
        }
    }
}
