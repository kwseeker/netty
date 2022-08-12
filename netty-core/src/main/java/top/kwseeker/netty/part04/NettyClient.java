package top.kwseeker.netty.part04;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private static final int PORT = 6666;
    private static final String SERVER_IP = "127.0.0.1";

    public static void main(String[] args) {
        new NettyClient().connect(SERVER_IP, PORT);
    }

    private void connect(String inetHost, int inetPort) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.AUTO_READ, true)
                    .handler(new MyChannelInitializer());

            ChannelFuture f = bootstrap.connect(inetHost, inetPort).sync();
            System.out.println("client started");

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
