package top.kwseeker.netty.part05.codec;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public class ByteBufferTest {

    /**
     * Hello Nio, I am Arvin Lee!
     * Hello Nio,
     *  I am Arvin Lee! Continue writing!
     *  I am Arvin Lee! Continue writing!
     */
    @Test
    public void testNioDirectByteBuffer() {
        //分配内存并创建缓冲实例，看源码可以看到ByteBuffer中除了allocate还有一组public方法wrap，见后面的测试
        ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(1024);
        //directByteBuffer.clear().position(index).limit(index + length);
        //put写数据
        directByteBuffer.put("Hello Nio, I am Arvin Lee!".getBytes());
        //directByteBuffer.put(" Haha".getBytes()); //未flip转换前可以继续put
        //flip从写模式切换为读模式
        directByteBuffer.flip();
        //get读数据
        while (directByteBuffer.hasRemaining()) {
            System.out.print((char) directByteBuffer.get());
        }
        System.out.println();
        //rewind,重置position=0,可以重新读, 相当于返回前面flip之后
        directByteBuffer.rewind();
        while (directByteBuffer.position() < 10) {   //这次不要读完
            System.out.print((char) directByteBuffer.get());
        }
        System.out.println();
        //未读完的数据重新插入开始位置
        directByteBuffer.compact();
        //可以继续往后写
        directByteBuffer.put(" Continue writing!".getBytes());
        directByteBuffer.flip();
        //记录position到mark
        directByteBuffer.mark();
        while (directByteBuffer.hasRemaining()) {
            System.out.print((char) directByteBuffer.get());
        }
        System.out.println();
        //回滚position
        directByteBuffer.reset();   //这对mark() reset()相当于实现了rewind()
        while (directByteBuffer.hasRemaining()) {
            System.out.print((char) directByteBuffer.get());
        }
        directByteBuffer.clear();
    }

    @Test
    public void testByteBuffer() {
        //HeapByteBuffer
        ByteBuffer buffer = ByteBuffer.wrap("Hello Nio, I am Arvin Lee!".getBytes());
        buffer.put("Hello Nio, I am Arvin Lee!".getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get());
        }
        System.out.println();
        //切片, 这里从11切到limit(26)
        buffer.position(11);
        ByteBuffer sliceBuffer = buffer.slice();
        sliceBuffer.flip();
        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get());
        }
    }

    /**
     * NIO DirectBuffer 如果要按页对齐的话，会求出大于base的最小的页数的整数倍作为缓冲空间开始地址
     */
    @Test
    public void testRoundToPageBoundary() {
        // Round up to page boundary
        long base1 = 4095;
        long base2 = 4100;
        //int ps = Bits.pageSize();
        int ps = 4096;
        long address1 = base1 + ps - (base1 & (ps - 1));    //base1 - (base & (ps - 1)) == ps, 前提ps是２的幂指数
        long address2 = base2 + ps - (base2 & (ps - 1));
        Assert.assertEquals(4096, address1);
        Assert.assertEquals(8192, address2);
    }
}
