package top.kwseeker.nettycomponent.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 一个将输入的数据按固定长度的帧输出的解码器
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if(frameLength <= 0) {
            throw new IllegalArgumentException("frameLength 必须为一个正整数，frameLength=" + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (in.readableBytes() >= frameLength) {
            ByteBuf buf = in.readBytes(frameLength);
            out.add(buf);
        }
    }
}
