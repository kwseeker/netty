package top.kwseeker.nettyutil.client;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringEncoder;
import top.kwseeker.nettyutil.client.handler.ClientHandler;

public class NettyClient {

    public static void main(String[] args) {
        // 1)
        EventLoopGroup group = new NioEventLoopGroup();                 //创建一个IO线程组（根据名称非阻塞IO事件循环组），TODO：通过配置文件进行配置

        try {
            Bootstrap bootstrap = new Bootstrap();                      //配置整个 Netty 程序，串联各个组件，Netty 中 Bootstrap 类是客户端程序的启动引导类，ServerBootstrap 是服务端启动引导类。
                                                                        //
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                            ch.pipeline().addLast(new ClientHandler());
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8888).sync();

            String person = "Arvin Lee\r\n";
            future.channel().writeAndFlush(person);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
/*
1) NioEventLoopGroup 与 EventLoopGroup
    EventLoopGroup是接口，用于定义处理事件循环的线程池的功能

*/