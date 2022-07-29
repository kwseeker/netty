package top.kwseeker.nio;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * sun.nio.ch.AllocatedNativeObject 是个 default class, 只能在包内或类内部访问
 * 这里测试需要将其代码重新实现copy下
 */
public class AllocateNativeObjectTest {

    private static final int EVENT_OFFSET = 0;
    private static final int FD_OFFSET = 4;
    private static final int SIZE_EPOLLEVENT = 12;
    private static final int NUM_EPOLLEVENTS = 8192;

    private static AllocatedNativeObject pollArray;
    private static long pollArrayAddress;

    @BeforeClass
    public static void initialize() {
        int eventsNativeObjectSize = NUM_EPOLLEVENTS * SIZE_EPOLLEVENT;

        pollArray = new AllocatedNativeObject(eventsNativeObjectSize, true);
        pollArrayAddress = pollArray.address();
    }

    @Test
    public void testAllocateNativeObject() {
        System.out.println("pollArrayAddress = " + pollArrayAddress);


    }

    void putEventOps(int var1, int var2) {
        int var3 = SIZE_EPOLLEVENT * var1 + EVENT_OFFSET;
        pollArray.putInt(var3, var2);
    }

    void putDescriptor(int var1, int var2) {
        int var3 = SIZE_EPOLLEVENT * var1 + FD_OFFSET;
        pollArray.putInt(var3, var2);
    }

    int getEventOps(int var1) {
        int var2 = SIZE_EPOLLEVENT * var1 + EVENT_OFFSET;
        return pollArray.getInt(var2);
    }

    int getDescriptor(int var1) {
        int var2 = SIZE_EPOLLEVENT * var1 + FD_OFFSET;
        return pollArray.getInt(var2);
    }

    void closeEPollFD() {
        pollArray.free();
    }
}
