package top.kwseeker.nio;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteOrder;

/**
 * NativeObject 是 Oracle JDK 非开源的类
 * 下面只是反编译并修改后的代码
 */
class NativeObject {

    //protected static final Unsafe unsafe = Unsafe.getUnsafe();
    protected static final Unsafe unsafe = getUnsafe();
    private static ByteOrder byteOrder = null;
    private static int pageSize = -1;

    protected long allocationAddress;
    private final long address;

    private static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    NativeObject(long allocationAddress) {
        this.allocationAddress = allocationAddress;
        this.address = allocationAddress;
    }

    NativeObject(long allocationAddress, long address) {
        this.allocationAddress = allocationAddress;
        this.address = allocationAddress + address;
    }

    protected NativeObject(int allocationAddress, boolean var2) {
        if (!var2) {
            this.allocationAddress = unsafe.allocateMemory(allocationAddress);
            this.address = this.allocationAddress;
        } else {
            int var3 = pageSize();
            long var4 = unsafe.allocateMemory(allocationAddress + var3);
            this.allocationAddress = var4;
            this.address = var4 + (long) var3 - (var4 & (long) (var3 - 1));
        }
    }

    long address() {
        return this.address;
    }

    long allocationAddress() {
        return this.allocationAddress;
    }

    NativeObject subObject(int var1) {
        return new NativeObject((long) var1 + this.address);
    }

    NativeObject getObject(int var1) {
        long var2 = 0L;
        switch (addressSize()) {
            case 4:
                assert unsafe != null;
                var2 = unsafe.getInt((long) var1 + this.address);
                break;
            case 8:
                assert unsafe != null;
                var2 = unsafe.getLong((long) var1 + this.address);
                break;
            default:
                throw new InternalError("Address size not supported");
        }

        return new NativeObject(var2);
    }

    void putObject(int var1, NativeObject var2) {
        switch (addressSize()) {
            case 4:
                this.putInt(var1, (int) (var2.address));
                break;
            case 8:
                this.putLong(var1, var2.address);
                break;
            default:
                throw new InternalError("Address size not supported");
        }

    }

    final byte getByte(int var1) {
        assert unsafe != null;
        return unsafe.getByte((long) var1 + this.address);
    }

    final void putByte(int var1, byte var2) {
        assert unsafe != null;
        unsafe.putByte((long) var1 + this.address, var2);
    }

    final short getShort(int var1) {
        assert unsafe != null;
        return unsafe.getShort((long) var1 + this.address);
    }

    final void putShort(int var1, short var2) {
        assert unsafe != null;
        unsafe.putShort((long) var1 + this.address, var2);
    }

    final char getChar(int var1) {
        assert unsafe != null;
        return unsafe.getChar((long) var1 + this.address);
    }

    final void putChar(int var1, char var2) {
        assert unsafe != null;
        unsafe.putChar((long) var1 + this.address, var2);
    }

    final int getInt(int var1) {
        assert unsafe != null;
        return unsafe.getInt((long) var1 + this.address);
    }

    final void putInt(int var1, int var2) {
        assert unsafe != null;
        unsafe.putInt((long) var1 + this.address, var2);
    }

    final long getLong(int var1) {
        assert unsafe != null;
        return unsafe.getLong((long) var1 + this.address);
    }

    final void putLong(int var1, long var2) {
        assert unsafe != null;
        unsafe.putLong((long) var1 + this.address, var2);
    }

    final float getFloat(int var1) {
        assert unsafe != null;
        return unsafe.getFloat((long) var1 + this.address);
    }

    final void putFloat(int var1, float var2) {
        assert unsafe != null;
        unsafe.putFloat((long) var1 + this.address, var2);
    }

    final double getDouble(int var1) {
        assert unsafe != null;
        return unsafe.getDouble((long) var1 + this.address);
    }

    final void putDouble(int var1, double var2) {
        assert unsafe != null;
        unsafe.putDouble((long) var1 + this.address, var2);
    }

    static int addressSize() {
        assert unsafe != null;
        return unsafe.addressSize();
    }

    static ByteOrder byteOrder() {
        if (byteOrder == null) {
            assert unsafe != null;
            long var0 = unsafe.allocateMemory(8L);

            try {
                unsafe.putLong(var0, 72623859790382856L);
                byte var2 = unsafe.getByte(var0);
                switch (var2) {
                    case 1:
                        byteOrder = ByteOrder.BIG_ENDIAN;
                        break;
                    case 8:
                        byteOrder = ByteOrder.LITTLE_ENDIAN;
                        break;
                    default:
                        assert false;
                }
            } finally {
                unsafe.freeMemory(var0);
            }
        }
        return byteOrder;
    }

    static int pageSize() {
        if (pageSize == -1) {
            assert unsafe != null;
            pageSize = unsafe.pageSize();
        }

        return pageSize;
    }
}
