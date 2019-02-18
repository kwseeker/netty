package top.kwseeker.iodemo;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class NioConnectionHandler implements Runnable {

    @Setter
    private volatile boolean stopped = false;

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    NioConnectionHandler(int port) {
        try {
            //创建多路复用器并开启
            selector = Selector.open();
            //创建并打开ServerSocketChannel,监听客户端的连接，它是所有客户端连接的父管道
            serverSocketChannel = ServerSocketChannel.open();
            //绑定监听端口，设置连接为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            //将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("NioConnectionHandler exception: {}", e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while(!stopped) {
                selector.select(10000); //
                Set<SelectionKey> keySet = selector.selectedKeys();
                if(keySet.size() > 0) {
                    log.info("Received {} piece message", keySet.size());
                    Iterator<SelectionKey> keyIterator = keySet.iterator();
                    SelectionKey selectionKey = null;
                    while (keyIterator.hasNext()) {
                        selectionKey = keyIterator.next();
                        keyIterator.remove();
                        try{
                            handleInput(selectionKey);
                        }catch(Exception e){
                            if(selectionKey != null){
                                selectionKey.cancel();
                                if(selectionKey.channel() != null){
                                    selectionKey.channel().close();
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(selector != null) {
                    selector.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            //处理新接入的请求消息
            ServerSocketChannel ssc= (ServerSocketChannel) key.channel();
            //多路复用器监听到有新的客户端接入，处理新的接入请求，
            //完成TCP三次握手，建立物理链路
            SocketChannel sc= ssc.accept();
            //设置客户端链路为非阻塞模式
            sc.configureBlocking(false);
            //将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作，
            //用来读取客户端发送的网络消息
            sc.register(selector,SelectionKey.OP_READ);
        }

        if(key.isReadable()){
            //read the data
            SocketChannel socketChannel= (SocketChannel) key.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            //异步读取客户端请求消息到缓冲区
            int readBytes = socketChannel.read(readBuffer);
            if(readBytes>0){
                readBuffer.flip();
                byte[] requestBytes = new byte[readBuffer.remaining()];
                readBuffer.get(requestBytes);
                String body = new String(requestBytes, StandardCharsets.UTF_8);
                log.info("Message from client: {}", body);

                String response = "Message response by NioServer";
                byte[] responseBytes = response.getBytes();
                ByteBuffer writeBuffer = ByteBuffer.allocate(responseBytes.length);
                writeBuffer.put(responseBytes);
                writeBuffer.flip();
                socketChannel.write(writeBuffer);
            }else if(readBytes<0){
                //对端链路关闭
                key.cancel();
                socketChannel.close();
            }
        }
    }
}
