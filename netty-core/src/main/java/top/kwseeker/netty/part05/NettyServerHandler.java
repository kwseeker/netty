package top.kwseeker.netty.part05;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
    * 当客户端连接服务器完成就会触发该方法
    */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        System.out.println("客户端链接通道建立完成");
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("Local IP: " + channel.localAddress().getHostString());
        System.out.println("Local Port: " + channel.localAddress().getPort());
        System.out.println("Remote IP: " + channel.remoteAddress().getHostString());
        System.out.println("Remote Port: " + channel.remoteAddress().getPort());

        //通知客户端通道已经建立成功
        String str = "链接建立成功\n";
        //ByteBuf buf = Unpooled.buffer(str.getBytes().length);
        //buf.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(str);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端断开链接" + ctx.channel().localAddress().toString());
    }

    /**
     * 读取客户端发送的数据
     * @param ctx 上下文对象, 含有通道channel，管道pipeline
     * @param msg 就是客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //前面有解码器后已经将ByteBuf按解码器转成String了
        System.out.println("客户端消息：" + msg);
    }

    /**
     * 数据读取完毕处理方法
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //ByteBuf buf = Unpooled.copiedBuffer("Hello Client !".getBytes(CharsetUtil.UTF_8));
        //ctx.writeAndFlush(buf);
    }

    /**
     * 处理异常, 一般是需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        System.out.println("异常信息：" + cause.getMessage());
    }
}
