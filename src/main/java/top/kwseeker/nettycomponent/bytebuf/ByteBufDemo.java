package top.kwseeker.nettycomponent.bytebuf;

import io.netty.buffer.*;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.util.internal.PlatformDependent;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * ByteBuf 的分配、写、读、释放
 */
public class ByteBufDemo {

    public static void main(String[] args) {
        try {
            //Pooled =================================================================
            //PooledHeapByteBuf ------------------------------------------------------
            ByteBuf pooledHeapByteBuf;
            ByteBufAllocator pooledByteBufAllocator = PooledByteBufAllocator.DEFAULT;
            pooledHeapByteBuf = pooledByteBufAllocator.heapBuffer(16);
            pooledHeapByteBuf.release();

            //PooledDirectByteByf ---------------------------------------------------
            ByteBuf pooledDirectByteBufSubPage = pooledByteBufAllocator.directBuffer(16);   //小于8K
            pooledDirectByteBufSubPage.release();

            ByteBuf pooledDirectByteBufPage = pooledByteBufAllocator.directBuffer(10240);   //大于8K
            pooledDirectByteBufPage.release();

            //Unpooled ===============================================================
            //UnpooledHeapByteBuf ----------------------------------------------------
            //直接分配一个数组
            ByteBuf unpooledHeapByteBuf;
            //ByteBufAllocator unpooledByteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
            //ByteBufAllocator UnpooledByteBufAllocator = new UnpooledByteBufAllocator(PlatformDependent.directBufferPreferred());
            ByteBufAllocator unpooledByteBufAllocator = new UnpooledByteBufAllocator(
                    //PlatformDependent.directBufferPreferred(),      //默认为true
                    false,
                    false,
                    PlatformDependent.useDirectBufferNoCleaner());
            // 1.1)
            //unpooledHeapByteBuf = Unpooled.buffer();
            // 1.2)
            //unpooledHeapByteBuf = unpooledByteBufAllocator.buffer();
            //unpooledHeapByteBuf = unpooledByteBufAllocator.heapBuffer();
            unpooledHeapByteBuf = unpooledByteBufAllocator.heapBuffer(16, Integer.MAX_VALUE);
            // 1.3)
            //unpooledHeapByteBuf = new UnpooledHeapByteBuf(unpooledByteBufAllocator, 16, Integer.MAX_VALUE);

            if(!unpooledHeapByteBuf.isReadable()) {
                unpooledHeapByteBuf.writeBytes("Arvin Lee".getBytes());
            }
            CharSequence cs = unpooledHeapByteBuf.readCharSequence(5, Charset.defaultCharset());
            System.out.println(cs.toString());
            unpooledHeapByteBuf.readBytes(System.out, unpooledHeapByteBuf.readableBytes());

            unpooledHeapByteBuf.release();

            //UnpooledDirectByteBuf --------------------------------------------------
            //分配内存：PlatformDependent.allocateDirectNoCleaner(initialCapacity);
            ByteBuf unpooledDirectByteBuf;
            // 1.1)
            //unpooledDirectByteBuf = unpooledByteBufAllocator.directBuffer();
            unpooledDirectByteBuf = unpooledByteBufAllocator.directBuffer(16, Integer.MAX_VALUE);
            // 1.2)
            //unpooledDirectByteBuf = new UnpooledDirectByteBuf(unpooledByteBufAllocator, 16, Integer.MAX_VALUE);

            unpooledDirectByteBuf.release();

            //其他Unpooled ByteBuf ---------------------------------------------------
            //UnpooledUnsafeDirectByteBuf
            //ByteBuf unpooledUnsafeDirectByteBuf = new UnpooledUnsafeDirectByteBuf(unpooledByteBufAllocator, 16, 1024);


            //其他 ===================================================================
            //EmptyByteBuf
            //CompositeByteBuf

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
