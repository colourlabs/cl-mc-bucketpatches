package net.colourlabs.bucketpatches.patches.imageonmap;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@TargetClass("fr.moribus.imageonmap.components.i18n.translators.gettext.GettextPOTranslator")
public class GettextPOTranslatorPatches {
    @TransformMethod("getPluralIndex")
    public static void fixNullScriptEngine(MethodNode method) {
        LabelNode originalCode = new LabelNode();

        InsnList head = new InsnList();
        head.add(new VarInsnNode(Opcodes.ALOAD, 0));
        head.add(new FieldInsnNode(Opcodes.GETFIELD,
            "fr/moribus/imageonmap/components/i18n/translators/gettext/GettextPOTranslator",
            "scriptEngine",
            "Ljavax/script/ScriptEngine;"));
        head.add(new JumpInsnNode(Opcodes.IFNONNULL, originalCode));

        head.add(new VarInsnNode(Opcodes.ALOAD, 0));
        head.add(new FieldInsnNode(Opcodes.GETFIELD,
            "fr/moribus/imageonmap/components/i18n/translators/gettext/GettextPOTranslator",
            "source",
            "Lfr/moribus/imageonmap/components/i18n/translators/gettext/POFile;"));
        head.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "fr/moribus/imageonmap/components/i18n/translators/gettext/POFile",
            "getPluralFormScript",
            "()Ljava/lang/String;", false));
        head.add(new VarInsnNode(Opcodes.ALOAD, 1));
        head.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/Integer",
            "intValue",
            "()I", false));
        head.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
            "net/colourlabs/bucketpatches/patches/imageonmap/PluralEval",
            "evaluate",
            "(Ljava/lang/String;I)Ljava/lang/Integer;", false));
        head.add(new InsnNode(Opcodes.ARETURN));
        head.add(originalCode);

        method.instructions.insert(head);
    }
}
