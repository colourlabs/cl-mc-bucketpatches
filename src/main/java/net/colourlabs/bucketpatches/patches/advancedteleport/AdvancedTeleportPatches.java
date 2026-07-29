package net.colourlabs.bucketpatches.patches.advancedteleport;

import net.colourlabs.patchthebucket.api.annotation.TargetClass;
import net.colourlabs.patchthebucket.api.annotation.TransformMethod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

@TargetClass("io.github.niestrat99.advancedteleport.UpdateChecker")
public class AdvancedTeleportPatches {
    @TransformMethod("getURLResults")
    public static void fixGetURLResults(MethodNode method) {
        method.instructions.clear();
        method.localVariables.clear();
        method.tryCatchBlocks.clear();

        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/net/URL"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
            "java/net/URL", "<init>", "(Ljava/lang/String;)V", false));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/net/URL", "openConnection",
            "()Ljava/net/URLConnection;", false));
        method.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
            "java/net/HttpURLConnection"));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 2));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new LdcInsnNode("User-Agent"));
        method.instructions.add(new LdcInsnNode("AdvancedTeleportPA"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/net/URLConnection", "addRequestProperty",
            "(Ljava/lang/String;Ljava/lang/String;)V", false));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/net/URLConnection", "getInputStream",
            "()Ljava/io/InputStream;", false));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 3));

        method.instructions.add(new TypeInsnNode(Opcodes.NEW,
            "java/io/InputStreamReader"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
            "java/io/InputStreamReader", "<init>",
            "(Ljava/io/InputStream;)V", false));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 4));

        method.instructions.add(new TypeInsnNode(Opcodes.NEW,
            "org/json/simple/parser/JSONParser"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
            "org/json/simple/parser/JSONParser", "<init>", "()V", false));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 4));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "org/json/simple/parser/JSONParser", "parse",
            "(Ljava/io/Reader;)Ljava/lang/Object;", false));
        method.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
            "org/json/simple/JSONObject"));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 5));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/io/InputStream", "close", "()V", false));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/net/HttpURLConnection", "disconnect", "()V", false));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
        method.instructions.add(new InsnNode(Opcodes.ARETURN));
    }

    @TransformMethod("getInternalTimestamp")
    public static void fixGetInternalTimestamp(MethodNode method) {
        method.instructions.clear();
        method.localVariables.clear();
        method.tryCatchBlocks.clear();

        method.instructions.add(new LdcInsnNode(
            Type.getObjectType("io/github/niestrat99/advancedteleport/CoreClass")));
        method.instructions.add(new LdcInsnNode("/update.properties"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/lang/Class", "getResourceAsStream",
            "(Ljava/lang/String;)Ljava/io/InputStream;", false));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 0));

        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/Properties"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
            "java/util/Properties", "<init>", "()V", false));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/util/Properties", "load",
            "(Ljava/io/InputStream;)V", false));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        method.instructions.add(new LdcInsnNode("update-timestamp"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/util/Hashtable", "get",
            "(Ljava/lang/Object;)Ljava/lang/Object;", false));
        method.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/String"));
        method.instructions.add(new VarInsnNode(Opcodes.ASTORE, 2));

        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/io/InputStream", "close", "()V", false));

        method.instructions.add(new TypeInsnNode(Opcodes.NEW,
            "java/text/SimpleDateFormat"));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new LdcInsnNode("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
            "java/text/SimpleDateFormat", "<init>",
            "(Ljava/lang/String;)V", false));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/text/DateFormat", "parse",
            "(Ljava/lang/String;)Ljava/util/Date;", false));
        method.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
            "java/util/Date", "getTime", "()J", false));
        method.instructions.add(new InsnNode(Opcodes.LRETURN));
    }
}
