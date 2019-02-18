package top.kwseeker.iodemo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 伪异步IO
 * 使用线程池处理连接
 */
@Slf4j
public class PaioServer {

    private static ServerSocket serverSocket = null;

    public static void start(int port) throws IOException {
        ExecutorService executorService = null;

        if(serverSocket != null) {
            log.warn("ServerSocket field is not null");
            return;
        }
        try {
            serverSocket = new ServerSocket(port);
            log.info("BioServer start, use port {}", port);

            //使用线程池处理
            //ExecutorService executorService = Executors.newFixedThreadPool(100);
            executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),    //有几个处理器创建几个线程
                    100, 120L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(10000));
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new ConnectionHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("BioServer exception: {}", e.getMessage());
        } finally {
            if(executorService != null) {
                executorService.shutdown();
            }
            if(serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            log.info("BioServer stop");
        }
    }
}
