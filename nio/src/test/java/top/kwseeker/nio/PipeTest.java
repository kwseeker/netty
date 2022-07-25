package top.kwseeker.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class PipeTest {

    /**
     * 自写自读
     */
    @Test
    public void testPipe1() throws IOException {
        Pipe pipe = Pipe.open();
        ByteBuffer buffer = ByteBuffer.wrap("Hello pipe!".getBytes());
        //将缓冲中的数据写入管道
        pipe.sink().write(buffer);
        ByteBuffer readBuffer = ByteBuffer.allocate(100);
        //将管道的数据读到缓冲
        int len = pipe.source().read(readBuffer);
        if (len > 0) {
            System.out.println(new String(readBuffer.array()));
        }
    }

    /**
     * 一个线程写通知另外一个线程读
     */
    @Test
    public void testPipe2() throws IOException {

    }
}
