package top.racerz.echo;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Exception implements Opcodes {
    public static void insert(ClassWriter cw, String option) {

        MethodVisitor mv;
        {
            mv = cw.visitMethod(ACC_PUBLIC, "echo", "()V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(37, l0);
            mv.visitTypeInsn(NEW, "java/lang/Exception");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, option, "result", "Ljava/lang/String;");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + option + ";", null, l0, l1, 0);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
    }
}
