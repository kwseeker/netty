package top.kwseeker.iodemo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NioServer {

    public synchronized static void start(int port) {
        NioConnectionHandler nioConnectionHandler = new NioConnectionHandler(port);
        new Thread(nioConnectionHandler).start();
    }
}
