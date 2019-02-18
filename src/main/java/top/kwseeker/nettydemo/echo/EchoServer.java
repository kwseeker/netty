package top.kwseeker.nettydemo.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TODO：EventLoopGroup 事件循环器工作原理
 *
 * 测试：
 *      telnet <ip> <port>
 */
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    //服务器启动方法
    public void start() throws Exception {
        //NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器,
        EventLoopGroup bossGroup = new NioEventLoopGroup();     //第一个用来接收进来的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();   //第二个处理已经被接收的连接

        try{
            //启动NIO服务的辅助启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());     //ChanelPipeLine 是一个 ChannelHandlers 链表，用于处理或拦截 Channel 接入事件或输出操作
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)              //设置指定的 Channel 实现的配置参数
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //端口绑定
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //等待服务器 socket 关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        try {
            new EchoServer(port).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
