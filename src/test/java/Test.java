
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


public class Test {
    public static void main(String[] args) throws IOException {
        String[] p = {"ipconfig", "/all",};
        System.out.println(Arrays.toString(p));
        Runtime.getRuntime().exec(new String[]{"ipconfig", "/all",});
    }

}
