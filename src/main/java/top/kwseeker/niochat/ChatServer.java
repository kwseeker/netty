package top.kwseeker.niochat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Nio实现的局域网聊天工具
 *  1）命令启动并指定端口号
 *  2）ChatServer组件
 *      服务器Socket通道实例 ServerSocketChannel，处理Socket连接
 *      客户端通道列表 SocketChannel，当前与此服务器连接的客户端列表
 *      通道选择器 Selector
 *      数据缓冲 ByteBuffer，存储聊天数据
 */
public class ChatServer {

    private static ServerSocketChannel server = null;
    private static List<SocketChannel> clientList = new ArrayList<>();
    private static Selector selector = null;
    private static ByteBuffer buffer = ByteBuffer.allocate(1024);
    private static boolean BlockFlag = false;

    public static void main(String[] args) {
        //服务器启动

    }

    public static void start(int port) {
        try {
            //启动选择器和服务器Socket通道
            selector = Selector.open();
            server = ServerSocketChannel.open();

            //ServerSocketChannel 绑定 Socket 通信端口
            server.socket().bind(new InetSocketAddress(port));
            //非阻塞模式
            server.configureBlocking(BlockFlag);
            //只监听 Accept 连接事件
            server.register(selector, SelectionKey.OP_ACCEPT);

            //启动连接处理线程，用户收发数据
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
