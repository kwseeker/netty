package top.kwseeker.netty.promise;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.Test;

import java.util.Date;

/**
 * 测试 Promise sync() 与 syncUninterruptibly() 区别
 */
public class PromiseSyncTest {

    @Test
    public void testPromiseSync() {
        try {
            DefaultPromise<String> promise = new DefaultPromise<>(new DefaultEventExecutor());
            promise.addListener(new GenericFutureListener<Future<? super String>>() {
                @Override
                public void operationComplete(Future<? super String> future) throws Exception {
                    System.out.println("Promise complete!");
                }
            });

            System.out.println("Current Time: " + new Date());  // -------->
            //5s后返回结果
            new Thread(() -> {
                sleepMs(5000);
                promise.trySuccess("success");
            }).start();

            //3s时中断主线程
            Thread mainThread = Thread.currentThread();
            new Thread(() -> {
                sleepMs(3000);
                mainThread.interrupt();
            }).start();

            promise.sync();                                     // <---------
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("main out! at " + new Date());   //sync(): 第3s时主线程抛出被中断异常并退出
        }
    }

    @Test
    public void testPromiseSyncUninterruptibly() throws InterruptedException {
        try {
            DefaultPromise<String> promise = new DefaultPromise<>(new DefaultEventExecutor());
            promise.addListener(new GenericFutureListener<Future<? super String>>() {
                @Override
                public void operationComplete(Future<? super String> future) throws Exception {
                    System.out.println("Promise complete!");
                }
            });

            System.out.println("Current Time: " + new Date());  // -------->
            //5s后返回结果
            new Thread(() -> {
                sleepMs(5000);
                promise.trySuccess("success");
            }).start();

            //3s时中断主线程
            Thread mainThread = Thread.currentThread();
            new Thread(() -> {
                sleepMs(3000);
                mainThread.interrupt();
            }).start();

            promise.syncUninterruptibly();                      // <---------
            System.out.println("After promise returned");

            Thread.sleep(3000);                             // 继续等待自己中断自己的执行
            System.out.println("You won‘t see me !");
        } finally {
            System.out.println("Main out! at " + new Date());   //syncUninterruptibly(): 第3s时主线程没有被中断退出，第5s时等promise返回才退出
        }
    }

    public void sleepMs(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
