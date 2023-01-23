package top.racerz.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class StringUtil {

    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }

        return !"".equals(String.valueOf(obj).trim());
    }

    public static String getCurrentPropertiesValue(String key) {
        String value = "";
        Properties p = new Properties();
        try {
            FileInputStream is = new FileInputStream("config.properties");
            p.load(is);
            value = p.getProperty(key);
        } catch (IOException e){
            e.printStackTrace();
        }

        return value;
    }


    public static void main(String[] args) {

    }
}

