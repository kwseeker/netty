package top.kwseeker.iodemo.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

@Slf4j
public class NioServer {

    public synchronized static void start(int port) {
        try {
            //创建多路复用器并开启
            Selector selector = Selector.open();
            //创建并打开ServerSocketChannel,监听客户端的连接，它是所有客户端连接的父管道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //绑定监听端口，设置连接为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            //将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            new Thread(new NioConnectionHandler(selector)).start();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("NioConnectionHandler exception: {}", e.getMessage());
        }
    }
}
