package net.colourlabs.bucketpatches.patches.greentext;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

@TargetClass("lol.nightshade.greentext.GreenText")
public class GreenTextPatches {
    @TransformMethod("onPlayerChat")
    public static void fixOnPlayerChat(MethodNode method) {
        method.instructions.clear();
        method.localVariables.clear();
        method.tryCatchBlocks.clear();

        LabelNode checkGreen = new LabelNode();
        LabelNode end = new LabelNode();

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "org/bukkit/event/player/AsyncPlayerChatEvent", "getMessage",
            "()Ljava/lang/String;", false));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 2));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new LdcInsnNode("<"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/String", "contains",
            "(Ljava/lang/CharSequence;)Z", false));
        method.instructions.add(new JumpInsnNode(Opcodes.IFEQ, checkGreen));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        method.instructions.add(buildColorConcat("GOLD", 2));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "org/bukkit/event/player/AsyncPlayerChatEvent", "setMessage",
            "(Ljava/lang/String;)V", false));
        method.instructions.add(new JumpInsnNode(Opcodes.GOTO, end));

        method.instructions.add(checkGreen);
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new LdcInsnNode(">"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/String", "startsWith",
            "(Ljava/lang/String;)Z", false));
        method.instructions.add(new JumpInsnNode(Opcodes.IFEQ, end));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        method.instructions.add(buildColorConcat("GREEN", 2));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "org/bukkit/event/player/AsyncPlayerChatEvent", "setMessage",
            "(Ljava/lang/String;)V", false));

        method.instructions.add(end);
        method.instructions.add(new InsnNode(Opcodes.RETURN));
    }

    private static InsnList buildColorConcat(String colorName, int msgVar) {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
            "java/lang/StringBuilder", "<init>", "()V", false));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC,
            "org/bukkit/ChatColor", colorName,
            "Lorg/bukkit/ChatColor;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder", "append",
            "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, msgVar));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder", "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder", "toString",
            "()Ljava/lang/String;", false));
        return list;
    }
}
