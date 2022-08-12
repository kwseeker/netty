package top.kwseeker.netty.part01;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * AbstractBootstrap#initAndRegister() 中会将MyChannelInitializer先封装成DefaultChannelHandlerContext加入到Pipeline,
 * 后面将Channel注册到Selector监听后会读取所有ChannelInitializer依次执行initChannel方法并将ChannelInitializer从Pipeline删除
 */
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    //TODO initChannel 执行流程
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //这里ch是服务端SocketChannel
        System.out.println("ch class: " + ch.getClass().getName());

        ch.pipeline().addLast(new NettyServerHandler());
    }
}
