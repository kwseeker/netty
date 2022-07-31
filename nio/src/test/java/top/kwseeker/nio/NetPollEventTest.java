package top.kwseeker.nio;

import org.junit.Test;
import sun.nio.ch.Net;

public class NetPollEventTest {

    @Test
    public void testPrintNetPollEvent() {
        System.out.println(Net.POLLIN);     //Linux系统中测试结果：1
        System.out.println(Net.POLLOUT);    //4
        System.out.println(Net.POLLCONN);   //4
        System.out.println(Net.POLLERR);    //8
        System.out.println(Net.POLLHUP);    //16
        System.out.println(Net.POLLNVAL);   //32
    }
}
