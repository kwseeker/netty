package top.kwseeker.embeddedChannelTest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;
import top.kwseeker.nettycomponent.codec.FixedLengthFrameDecoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class FixedLengthFrameDecoderTest {

    @Test
    public void testFrameDecoder() {
        //初始化测试数据
        ByteBuf byteBuf = Unpooled.buffer();                    //
        for (int i = 0; i < 9; i++) {
            byteBuf.writeByte(i);
        }
        ByteBuf input = byteBuf.duplicate();                    //

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
//        assertTrue(channel.writeInbound(input.retain()));       //
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);               //
        read.release();
        read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();
        read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();
        assertNull(channel.readInbound());
        byteBuf.release();
    }
}
