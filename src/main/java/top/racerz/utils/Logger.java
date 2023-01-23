package top.racerz.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static final String ANSI_RESET = "\033[0m";

    public static final String ANSI_PURPLE = "\033[35m";

    public static final String ANSI_RED = "\033[31m";

    public static final String ANSI_BLUE = "\033[34m";

    public static void print(String str) {
        System.out.println(str);
    }

    public static void info(String infoString) {
        System.out.println(printWithColor(infoString, ANSI_BLUE));
    }

    public static void warning(String warningString) {
        System.out.println(printWithColor(warningString, ANSI_PURPLE));
    }

    public static void error(String errorString) {
        System.out.println(printWithColor(errorString, ANSI_RED));
    }

    private static String printWithColor(String str, String color) {
        return color + "[" + getLocalTime() + "]" + str + ANSI_RESET;
    }

    private static String getLocalTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

}
