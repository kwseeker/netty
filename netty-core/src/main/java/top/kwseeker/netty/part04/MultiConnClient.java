package top.kwseeker.netty.part04;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 一个客户端到一个服务端节点可以创建多个连接（中间件中常见）
 * 多个连接使用不同的端口，每个连接有自己的ChannelHandler管道Pipeline实例。
 */
public class MultiConnClient {

    private static final int PORT = 6666;
    private static final String SERVER_IP = "127.0.0.1";

    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.AUTO_READ, true)
                .handler(new MyChannelInitializer());

        try {
            ChannelFuture cf1 = bootstrap.connect(SERVER_IP, PORT).sync();
            ChannelFuture cf2 = bootstrap.connect(SERVER_IP, PORT).sync();
            System.out.println("client started");

            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
