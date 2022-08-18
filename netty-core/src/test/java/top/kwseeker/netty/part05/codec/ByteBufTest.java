package top.kwseeker.netty.part05.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import org.junit.Test;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Random;

public class ByteBufTest {

    static final Unsafe UNSAFE;
    static final boolean PREFER_DIRECT;

    static {
        //通过反射尝试获取UNSAFE单例
        final Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                    // We always want to try using Unsafe as the access still works on java9 as well and
                    // we need it for out native-transports and many optimizations.
                    Throwable cause = ReflectionUtil.trySetAccessible(unsafeField, false);
                    if (cause != null) {
                        return cause;
                    }
                    // the unsafe instance
                    return unsafeField.get(null);
                } catch (NoSuchFieldException e) {
                    return e;
                } catch (SecurityException e) {
                    return e;
                } catch (IllegalAccessException e) {
                    return e;
                } catch (NoClassDefFoundError e) {
                    // Also catch NoClassDefFoundError in case someone uses for example OSGI and it made
                    // Unsafe unloadable.
                    return e;
                }
            }
        });
        if (maybeUnsafe instanceof Throwable) {
            UNSAFE = null;
        } else {
            UNSAFE = (Unsafe) maybeUnsafe;
        }

        PREFER_DIRECT = checkCleanerSupported();
    }

    /**
     * 测试案例调试时会发现Netty默认创建的ByteBuf是PooledUnsafeDirectByteBuf(池化的直接缓冲)
     * 此ByteBuf分配流程
     * １）判断当前平台是否支持释放直接内存的Cleaner(java8是支持的，Cleaner实例是CleanerJava6)
     * ２）如果支持Cleaner，allocator就创建直接缓冲否则创建堆缓冲
     */
    @Test
    public void testPooledUnsafeDirectByteBuf() throws IOException {
        //Netty启动时没有指定 io.netty.allocator.type 或指定pooled 系统属性就默认用 PooledByteBufAllocator
        //ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBufAllocator allocator =  new PooledByteBufAllocator(checkCleanerSupported());
        int initialCap = 1024;
        //内部：PooledUnsafeDirectByteBuf.newInstance(maxCapacity)
        ByteBuf byteBuf = allocator.ioBuffer(initialCap);
        //看内部源码是封装的Java NIO DirectByteBuffer
        //内部写实现：tmpBuf.clear().position(index).limit(index + length); in.read(tmpBuf);

        //顺序写
        byteBuf.writeBytes("This is ByteBuf! Designed By Netty! A Convenient Buffer!".getBytes());
        //随机写(按索引写, 不会修改readerIndex、writerIndex的值, 想当于覆盖)
        byteBuf.setBytes(8, "BYTE_BUF".getBytes());

        //随机读(按索引读，也不会修改readerIndex、writerIndex的值，调试时可以用于输出日志)
        CharSequence charSequence = byteBuf.getCharSequence(0, byteBuf.readableBytes(), StandardCharsets.UTF_8);
        System.out.println("ByteBuf content: " + charSequence.toString());
        //顺序读
        // 1
        int maxReadBytes = 17, counter = 0;
        StringBuilder sb = new StringBuilder();
        while(byteBuf.readableBytes() > 0 && counter < maxReadBytes) {
            sb.append((char)byteBuf.readByte());
            counter++;
        }
        System.out.println("first read: " + sb.toString());
        //压缩（将未读的数据移动到空间数据起始位置[将覆盖已读取的数据], 因为有数据复制操作性能不好（拿时间换空间），使用场景一般更侧重性能，所以非必要勿用）
        System.out.println("discard before: readerIndex=" + byteBuf.readerIndex() + ", writerIndex=" + byteBuf.writerIndex());
        byteBuf.discardReadBytes();
        System.out.println("discard after: readerIndex=" + byteBuf.readerIndex() + ", writerIndex=" + byteBuf.writerIndex());
        // 2 直接读到输出流, 返回byteBuf本身
        System.out.print("second read: ");
        ByteBuf partBuf = byteBuf.readBytes(System.out, 18);
        System.out.println();

        assert System.identityHashCode(byteBuf) == System.identityHashCode(partBuf);    //identityHashCode()返回内存地址hash不管hashCode()是否重写，这里不严谨，其实也是可能冲突的

        //markReaderIndex() resetReaderIndex() 实现重复读
        System.out.println("mark and reset: ");
        partBuf.markReaderIndex();
        partBuf.readBytes(System.out, partBuf.readableBytes());System.out.println();
        partBuf.resetReaderIndex();
        partBuf.readBytes(System.out, partBuf.readableBytes());

        byteBuf.clear();
    }

    /**
     * 标准化的容量：
     * 8482 -> 16384
     * 5522 -> 8192
     * 9894 -> 16384
     * 7624 -> 8192
     * 2732 -> 4096
     */
    @Test
    public void testNormalizeCapacity() {
        Random random = new Random();
        System.out.println("标准化的容量：");
        for (int i = 0; i < 5; i++) {
            int reqCap = random.nextInt(10000);
            System.out.println(reqCap + " -> " + normalizeCapacity(reqCap));
        }
    }

    /**
     * 求大于等于reqCapacity的最小2次幂
     * 比如一个二进制数 10000 按下面执行，最终会发现最高位的1会填满1之后所有的0
     */
    private int normalizeCapacity(int reqCapacity) {
        int normalizedCapacity = reqCapacity;
        normalizedCapacity --;
        normalizedCapacity |= normalizedCapacity >>>  1;
        normalizedCapacity |= normalizedCapacity >>>  2;
        normalizedCapacity |= normalizedCapacity >>>  4;
        normalizedCapacity |= normalizedCapacity >>>  8;
        normalizedCapacity |= normalizedCapacity >>> 16;
        normalizedCapacity ++;

        if (normalizedCapacity < 0) {
            normalizedCapacity >>>= 1;
        }
        return normalizedCapacity;
    }

    private static boolean checkCleanerSupported() {
        //CleanerJava6.isSupported()
        ByteBuffer direct = ByteBuffer.allocateDirect(1);
        Object mayBeCleanerField = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Field cleanerField =  direct.getClass().getDeclaredField("cleaner");
                    if (!PlatformDependent.hasUnsafe()) {
                        // We need to make it accessible if we do not use Unsafe as we will access it via reflection.
                        cleanerField.setAccessible(true);
                    }
                    return cleanerField;
                } catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (mayBeCleanerField instanceof Throwable) {
            //throw (Throwable) mayBeCleanerField;
            return false;
        }
        Field cleanerField = (Field) mayBeCleanerField;

        long fieldOffset;
        final Object cleaner;
        if (PlatformDependent.hasUnsafe()) {
            //fieldOffset = PlatformDependent0.objectFieldOffset(cleanerField);
            //cleaner = PlatformDependent0.getObject(direct, fieldOffset);
            fieldOffset = UNSAFE.objectFieldOffset(cleanerField);
            cleaner = UNSAFE.getObject(direct, fieldOffset);
        } else {
            fieldOffset = -1;
            try {
                cleaner = cleanerField.get(direct);
            } catch (IllegalAccessException e) {
                return false;
            }
        }

        return fieldOffset != -1 || cleaner != null;
    }
}
