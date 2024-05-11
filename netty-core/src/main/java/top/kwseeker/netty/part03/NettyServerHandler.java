package top.kwseeker.netty.part03;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

import java.io.IOException;

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
        System.out.println("pipeline identity: " + System.identityHashCode(channel.pipeline()));

        //将客户端channel加入分组
        ChannelGroupHolder.channelGroup.add(channel);

        //通知客户端通道已经建立成功
        String linkedMsg = "链接建立成功\n";
        //ByteBuf buf = Unpooled.buffer(linkedMsg.getBytes().length);
        //buf.writeBytes(linkedMsg.getBytes(StandardCharsets.UTF_8));
        //有了StringEncoder这里直接写就行
        ctx.writeAndFlush(linkedMsg);

        //群发消息
        String groupMsg = "用户" + getMsgPrefix(channel) + "加入房间";
        ChannelGroupHolder.channelGroup.writeAndFlush(groupMsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        //super.channelInactive(ctx);   //TODO 这里的fireChannelInactive()到底做了啥
        System.out.println("客户端断开链接" + ctx.channel().localAddress().toString());

        // 当有客户端退出后，从channelGroup中移除
        ChannelGroupHolder.channelGroup.remove(ctx.channel());

        //群发消息
        String groupMsg = "用户" + getMsgPrefix(channel) + "退出房间";
        ChannelGroupHolder.channelGroup.writeAndFlush(groupMsg);
    }

    /**
     * 读取客户端发送的数据
     * @param ctx 上下文对象, 含有通道channel，管道pipeline
     * @param msg 就是客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        SocketChannel channel = (SocketChannel) ctx.channel();
        //ChannelPipeline pipeline = ctx.pipeline(); //本质是一个双向链接, 出站入站
        //将 msg 转成一个 ByteBuf，类似NIO 的 ByteBuffer
        //ByteBuf buf = (ByteBuf) msg;
        //System.out.println("客户端消息:" + buf.toString(CharsetUtil.UTF_8));
        //前面有解码器后已经将ByteBuf按解码器转成String了
        System.out.println("客户端消息：" + msg);

        //群发消息
        String groupMsg = getMsgPrefix(channel) + msg;
        ChannelGroupHolder.channelGroup.writeAndFlush(groupMsg);
    }

    private String getMsgPrefix(SocketChannel channel) {
        return "[" + channel.remoteAddress().getHostString() + ":" + channel.remoteAddress().getPort() + "]：";
    }

    /**
     * 数据读取完毕处理方法 TODO 怎样才算读完
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
