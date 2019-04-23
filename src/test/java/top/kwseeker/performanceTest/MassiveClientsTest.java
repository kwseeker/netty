package top.kwseeker.performanceTest;

/**
 * 单机连接测试
 */
public class MassiveClientsTest {

    public static void main(String[] args) {
        Client.start(8000);
    }
}
