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
     * 一个线程写完通知另外一个线程读，每次在对方的值上加１再写回去
     */
    @Test
    public void testPipe2() throws IOException, InterruptedException {
        Pipe pipe = Pipe.open();

        Object sig1 = new Object();
        Object sig2 = new Object();

        Thread thread1 = new Thread(() -> {
            try {
                int initVal = 1;
                while (true) {
                    ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(initVal).getBytes());
                    Thread.sleep(500);
                    pipe.sink().write(buffer);
                    synchronized (sig2) {
                        sig2.notify();
                    }

                    synchronized (sig1) {
                        sig1.wait();
                    }
                    ByteBuffer readBuffer = ByteBuffer.allocate(10);
                    Thread.sleep(500);
                    int len = pipe.source().read(readBuffer);
                    if (len > 0) {
                        int val = Integer.parseInt(new String(readBuffer.array()).trim());
                        System.out.println(val);
                        initVal = val + 1;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                int initVal = 1;
                while(true) {
                    synchronized (sig2) {
                        sig2.wait();
                    }
                    ByteBuffer readBuffer = ByteBuffer.allocate(10);
                    Thread.sleep(500);
                    int len = pipe.source().read(readBuffer);
                    if (len > 0) {
                        int val = Integer.parseInt(new String(readBuffer.array()).trim());
                        System.out.println(val);
                        initVal = val + 1;
                    }

                    ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(initVal).getBytes());
                    Thread.sleep(500);
                    pipe.sink().write(buffer);
                    synchronized (sig1) {
                        sig1.notify();
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
    }

    /**
     * Java管道也支持Selector,TODO
     */
    @Test
    public void testPipeWithSelector() {

    }
}
