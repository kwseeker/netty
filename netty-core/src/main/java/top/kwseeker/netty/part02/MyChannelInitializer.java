package top.kwseeker.netty.part02;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        //解码器
        //ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
        // 基于指定字符串【换行符，这样功能等同于LineBasedFrameDecoder】
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, false, Delimiters.lineDelimiter()));
        // 基于最大长度
        // ch.pipeline().addLast(new FixedLengthFrameDecoder(4));
        //在管道中添加自己的接收数据实现方法
        ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));   //telnet测试会出现中文乱码，应该是telnet对中文支持不好
        ch.pipeline().addLast(new NettyServerHandler());
    }
}
