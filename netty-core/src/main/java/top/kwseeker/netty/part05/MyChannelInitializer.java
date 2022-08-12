package top.kwseeker.netty.part05;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.kwseeker.netty.part05.codec.MyDecoder;
import top.kwseeker.netty.part05.codec.MyEncoder;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        //解码器
        //ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
        // 基于指定字符串【换行符，这样功能等同于LineBasedFrameDecoder】
        //ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, false, Delimiters.lineDelimiter()));
        // 基于最大长度
        // ch.pipeline().addLast(new FixedLengthFrameDecoder(4));
        //在管道中添加自己的接收数据实现方法
        ch.pipeline().addLast(new MyEncoder());
        ch.pipeline().addLast(new MyDecoder());
        ch.pipeline().addLast(new NettyServerHandler());
    }
}
