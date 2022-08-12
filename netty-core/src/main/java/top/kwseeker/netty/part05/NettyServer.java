package top.kwseeker.netty.part05;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private static final int PORT = 6666;

    public static void main(String[] args) {
        new NettyServer().bind(PORT);
    }

    private void bind(int port) {
        //创建处理连接事件的多路复用器(Selector)及连接任务处理线程池（默认是[2*CPU核心数]组，每组一个Selector+一个线程池）
        EventLoopGroup bossGroup = new NioEventLoopGroup(3);
        //创建处理业务事件的多路复用器及任务处理线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {
            //这里全是初始化操作
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)    //非阻塞模式
                    .option(ChannelOption.SO_BACKLOG, 128)  //连接等待队列长度，连接事件是排队处理的
                    .childHandler(new MyChannelInitializer());

            //bind和sync是核心启动操作
            //bind：创建Channel实例，注册ChannelHandler到Pipeline,将Channel注册到某组的Selector监听
            // 看源码知boosGroup只有一组有注册监听ServerSocketChannel的连接事件，因为一个Channel只能被一个Selector监听（多个Selector同时监听同一个Channel,只有一个能获取连接请求数据）
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
