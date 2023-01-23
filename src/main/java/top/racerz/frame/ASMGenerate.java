package top.racerz.frame;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import top.racerz.utils.Logger;
import top.racerz.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

public class ASMGenerate implements Opcodes {

    private String Option;

    public ASMGenerate(String option) {
        this.Option = option;
    }

    public byte[] dump() throws Exception {

        final String classNameASM = this.Option;

        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER,  classNameASM, null, "java/lang/Object", null);
        // 构造函数部分
        Class<?> clazz = Class.forName("top.racerz.Exploit." + this.Option);
        Method method = clazz.getMethod("insert", ClassWriter.class);
        method.invoke(null, cw);
        // TODO: echo 部分
        String echo = StringUtil.getCurrentPropertiesValue("echo");
        Class<?> clazz2 = Class.forName("top.racerz.echo." + echo);
        Method method2 = clazz2.getMethod("insert", ClassWriter.class, String.class);
        method2.invoke(null, cw, this.Option);

        cw.visitEnd();
        return cw.toByteArray();
    }

    public static byte[] run(String fileName) throws Exception {
        String option = fileName.substring(0, fileName.lastIndexOf("."));
        return new ASMGenerate(option).dump();
    }

    public static void main(String[] args) throws Exception {
        String filePath = "Command.class";
        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(new ASMGenerate("Command").dump());
        outputStream.close();
    }
}
