package top.kwseeker.iodemo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 演示BIO模型的Socket服务端代码
 *
 */
@Slf4j
public class BioServer {

    private static ServerSocket serverSocket = null;

    public synchronized static void start(int port) throws IOException {
        if(serverSocket != null) {
            log.warn("ServerSocket field is not null");
            return;
        }
        try {
            //如果端口合法且空闲则监听成功
            serverSocket = new ServerSocket(port);
            log.info("BioServer start, use port {}", port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ConnectionHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("BioServer exception: {}", e.getMessage());
        } finally {
            if(serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            log.info("BioServer stop");
        }
    }
}
