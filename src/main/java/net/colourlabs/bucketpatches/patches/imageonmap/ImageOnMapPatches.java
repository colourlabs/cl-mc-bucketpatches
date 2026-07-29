package net.colourlabs.bucketpatches.patches.imageonmap;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

@TargetClass("fr.moribus.imageonmap.image.Renderer")
public class ImageOnMapPatches {
    @TransformMethod("render")
    public static void fixRender(MethodNode method) {
        method.instructions.clear();
        method.localVariables.clear();
        method.tryCatchBlocks.clear();

        LabelNode end = new LabelNode();
        LabelNode useFallback = new LabelNode();
        LabelNode afterRender = new LabelNode();

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,
            "fr/moribus/imageonmap/image/Renderer", "image",
            "Ljava/awt/image/BufferedImage;"));
        method.instructions.add(new JumpInsnNode(Opcodes.IFNULL, end));

        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "org/bukkit/Bukkit", "getServer",
            "()Lorg/bukkit/Server;", false));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
            "org/bukkit/Server", "getServicesManager",
            "()Lorg/bukkit/plugin/ServicesManager;", true));
        method.instructions.add(new LdcInsnNode(
            org.objectweb.asm.Type.getType("Ljava/util/function/BiConsumer;")));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
            "org/bukkit/plugin/ServicesManager", "load",
            "(Ljava/lang/Class;)Ljava/lang/Object;", true));
        method.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
            "java/util/function/BiConsumer"));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 4));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        method.instructions.add(new JumpInsnNode(Opcodes.IFNULL, useFallback));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,
            "fr/moribus/imageonmap/image/Renderer", "image",
            "Ljava/awt/image/BufferedImage;"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
            "java/util/function/BiConsumer", "accept",
            "(Ljava/lang/Object;Ljava/lang/Object;)V", true));
        method.instructions.add(new JumpInsnNode(Opcodes.GOTO, afterRender));

        method.instructions.add(useFallback);
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new InsnNode(Opcodes.ICONST_0));
        method.instructions.add(new InsnNode(Opcodes.ICONST_0));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,
            "fr/moribus/imageonmap/image/Renderer", "image",
            "Ljava/awt/image/BufferedImage;"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
            "org/bukkit/map/MapCanvas", "drawImage",
            "(IILjava/awt/Image;)V", true));

        method.instructions.add(afterRender);
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
        method.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD,
            "fr/moribus/imageonmap/image/Renderer", "image",
            "Ljava/awt/image/BufferedImage;"));

        method.instructions.add(end);
        method.instructions.add(new InsnNode(Opcodes.RETURN));
    }
}
