package top.kwseeker.netty.part05.codec;

import org.junit.Assert;
import org.junit.Test;

import java.util.Formatter;

public class MyEncoderTest {

    @Test
    public void testEncode() {
        String msg = "hello";
        byte[] bytes = msg.getBytes();

        byte[] send = new byte[bytes.length + 2];
        System.arraycopy(bytes, 0, send, 1, bytes.length);
        send[0] = 0x02;
        send[send.length - 1] = 0x03;

        String hex = byte2HexString(send);
        Assert.assertEquals("0268656c6c6f03", hex);
    }

    public static String byte2HexString(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }

        Formatter formatter = new Formatter();
        String pattern = "%02x";
        for (byte aByte : bytes) {
            formatter.format(pattern, aByte);
        }
        return formatter.toString();
    }
}