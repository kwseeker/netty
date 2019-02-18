package top.kwseeker.iodemo.nio;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
    private volatile boolean stop = false;
    private Selector selector;

    NioConnectionHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while(!stop) {
                int readyKeyNum = selector.select(10000); //最长阻塞时间，感觉在while循环中和select()并没有区别
                Set<SelectionKey> keySet = selector.selectedKeys();
                int keySetSize = keySet.size();
                log.info("readyKeyNum:{}, keySetSize:{}", readyKeyNum, keySetSize);
                if(keySetSize > 0) {
                    Iterator<SelectionKey> keyIterator = keySet.iterator();
                    SelectionKey selectionKey = null;
                    while (keyIterator.hasNext()) {
                        selectionKey = keyIterator.next();
                        keyIterator.remove();
                        try{
                            if(selectionKey.isConnectable()) {
                                log.info("handle connection event");
                            } else if(selectionKey.isAcceptable()) {
                                log.info("handle accept event");
                                handleInput(selectionKey);
                            } else if(selectionKey.isWritable()) {
                                log.info("handle write event");
                            } else if(selectionKey.isReadable()) {
                                log.info("handle read event");
                                handleInput(selectionKey);
                            }
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
            log.error("NioConnectionHandler exception: {}", e.getMessage());
        } finally {
            try {
                if(selector != null) {
                    selector.close();
                    log.info("关闭selector");
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("NioConnectionHandler exception: {}", e.getMessage());
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            //处理新接入的请求消息
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            //多路复用器监听到有新的客户端接入，处理新的接入请求，
            //完成TCP三次握手，建立物理链路
            SocketChannel sc = ssc.accept();
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
