package top.kwseeker.nettydemo.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {     //在连接被建立且准备通信的时候被调用
        log.debug("Call channelActive() ...");
        final ByteBuf time = ctx.alloc().buffer(4);     //分配一个4byte的ByteBuf
        time.writeInt((int)(System.currentTimeMillis()/1000L + 2208988800L));   //向ByteBuf写入时间值

        final ChannelFuture channelFuture = ctx.writeAndFlush(time);    //返回一个还未发生的IO操作
        //当 ChannelFuture 的IO操作完成之后，关闭channel
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }
}
