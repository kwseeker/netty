package top.kwseeker.nio;

import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.channels.SelectionKey;

/**
 * sun.nio.ch.AllocatedNativeObject 是个 default class, 只能在包内或类内部访问
 * 这里测试需要将其代码重新实现copy下
 *
 * 是由Unsafe实现的
 *
 * sun.nio.ch.AllocatedNativeObject 代表的是下面的 events
 * 	struct epoll_event events[EPOLLEVENTS];
 * 	ret = epoll_wait(epollfd, events, EPOLLEVENTS, -1);
 *
 * typedef union epoll_data {       //联合体，实际类型可能是下面任意一种类型的其中一种，所以内存对齐按最大长度的类型计算，
 *     void *ptr;                   //8
 *     int fd;                      //4
 *     __uint32_t u32;              //4
 *     __uint64_t u64;              //8
 *  } epoll_data_t;
 *
 * struct epoll_event {
 *     __uint32_t events;           //4
 *     epoll_data_t data;           //8
 * };
 */
public class AllocateNativeObjectTest {

    private static final int EVENT_OFFSET = 0;          //因为event是第一个变量，偏移值是0
    private static final int FD_OFFSET = 4;             //__uint32_t events占
    private static final int SIZE_EPOLLEVENT = 12;      //即上面struct epoll_event的长度，为何是12?　是由内存对齐规则计算出来的(规则随便搜索下就能找到，和java内存对齐规则有点不太一样)
                                                        //C语言 sizeof(struct epoll_event) 返回值就是12
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

        int serverSocketFd = 24;
        putDescriptor(1, serverSocketFd);
        putEventOps(1, SelectionKey.OP_ACCEPT);
        //上面操作相当于下面C语言代码event的赋值操作
        //struct epoll_event event;
        //event.data.fd=fd;
        //event.events=EPOLLIN;
        //if(enable_et) {
        //    event.events|=EPOLLET;
        //}
        //epoll_ctl(epoll_fd,EPOLL_CTL_ADD,fd,&event);

        int fd = getDescriptor(1);
        int events = getEventOps(1);
        System.out.println("fd=" + fd + ", events=" + events);

        closeEPollFD();
    }

    /* 填充第idx个　struct epoll_event 的 events 值　*/
    static void putEventOps(int idx, int events) {
        int offset = SIZE_EPOLLEVENT * idx + EVENT_OFFSET;
        pollArray.putInt(offset, events);
    }

    /* 填充第idx个　struct epoll_event 的 data 值　*/
    static void putDescriptor(int idx, int fd) {
        int offset = SIZE_EPOLLEVENT * idx + FD_OFFSET;
        pollArray.putInt(offset, fd);
    }

    static int getEventOps(int idx) {
        int offset = SIZE_EPOLLEVENT * idx + EVENT_OFFSET;
        return pollArray.getInt(offset);
    }

    static int getDescriptor(int idx) {
        int offset = SIZE_EPOLLEVENT * idx + FD_OFFSET;
        return pollArray.getInt(offset);
    }

    static void closeEPollFD() {
        pollArray.free();
    }
}
