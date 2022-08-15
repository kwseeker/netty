package top.kwseeker.netty.part05.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

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
    public void testPooledUnsafeDirectByteBuf() {
        //Netty启动时没有指定 io.netty.allocator.type 或指定pooled 系统属性就默认用 PooledByteBufAllocator
        //ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBufAllocator allocator =  new PooledByteBufAllocator(checkCleanerSupported());
        int initialCap = 1024;
        //内部：PooledUnsafeDirectByteBuf.newInstance(maxCapacity)
        ByteBuf byteBuf = allocator.ioBuffer(initialCap);
        //看内部源码是封装的Java NIO DirectByteBuffer
        //内部写实现：tmpBuf.clear().position(index).limit(index + length); in.read(tmpBuf);
        byteBuf.writeBytes(new byte[]{2,34,68,69,68,69,3});

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
