package top.kwseeker.netty.part01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 测试两个Selector监听同一个Channel
 *
 * 结果：
 *  一个Channel只能由一个Selector监听，多个Selector监听同一个Channel只有一个会连接成功，其他的读取连接的客户端Channel都会报空指针异常
 *
 * Log:
 *  服务启动成功
 *  客户端连接成功
 *  Exception in thread "Thread-0" java.lang.UnsupportedOperationException
 * 	    at java.nio.ByteBuffer.array(ByteBuffer.java:1002)
 * 	    at top.kwseeker.netty.part01.NioSocketServer$HandlerTask.run(NioSocketServer.java:79)
 * 	    at java.lang.Thread.run(Thread.java:748)
 */
public class NioSocketServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 创建NIO ServerSocketChannel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        // 设置ServerSocketChannel为非阻塞
        serverSocket.configureBlocking(false);
        // 打开Selector处理Channel，即创建epoll
        Selector selector = Selector.open();
        Selector selector2 = Selector.open();
        // 把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
        SelectionKey selectionKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        SelectionKey selectionKey2 = serverSocket.register(selector2, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功");

        Thread thread1 = new Thread(new HandlerTask(selector));
        Thread thread2 = new Thread(new HandlerTask(selector2));
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    static class HandlerTask implements Runnable {

        private final Selector selector;

        public HandlerTask(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 阻塞等待需要处理的事件发生
                    selector.select();

                    // 获取selector中注册的全部事件的 SelectionKey 实例
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    // 遍历SelectionKey对事件进行处理
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        // 如果是OP_ACCEPT事件，则进行连接获取和事件注册
                        if (key.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel socketChannel = server.accept();
                            socketChannel.configureBlocking(false);
                            // 这里只注册了读事件，如果需要给客户端发送数据可以注册写事件
                            SelectionKey selKey = socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println("客户端连接成功");
                        } else if (key.isReadable()) {  // 如果是OP_READ事件，则进行读取和打印
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(128);
                            int len = socketChannel.read(byteBuffer);
                            // 如果有数据，把数据打印出来
                            if (len > 0) {
                                System.out.println("接收到消息：" + new String(byteBuffer.array()));
                            } else if (len == -1) { // 如果客户端断开连接，关闭Socket
                                System.out.println("客户端断开连接");
                                socketChannel.close();
                            }
                        }
                        //从事件集合里删除本次处理的key，防止下次select重复处理
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
