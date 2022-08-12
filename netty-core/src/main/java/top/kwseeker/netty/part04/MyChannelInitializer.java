package top.kwseeker.netty.part04;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 基于换行符号
        channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
        channel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
        channel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
        channel.pipeline().addLast(new NettyClientHandler());
    }
}

