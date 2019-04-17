package top.kwseeker.nettycomponent;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.lang.ref.WeakReference;

/**
 * FastThreadLocal 测试（FastThreadLocal比ThreadLocal又快又安全）
 *
 * 1）为什么FastThreadLocal更快
 *
 * 2）为什么FastThreadLocal更安全
 *
 * TODO：
 * 1）main线程和Thread线程的区别？main线程和Thread线程处理FastThreadLocal的异同？
 *
 */
public class FastThreadLocalDemo {

    public static class SecurityTestTask implements Runnable {
        @Override
        public void run() {

        }
    }

    public static class SpeedTestTask implements Runnable {
        @Override
        public void run() {

        }
    }

    public static class Task implements Runnable {
        private FastThreadLocal<Integer> fastThreadLocal = new FastThreadLocal<>();
        private WeakReference<FastThreadLocal<Integer>> ftlWeakRef = new WeakReference<>(new FastThreadLocal<>());

        @Override
        public void run() {             //FastThreadLocal在run()执行完毕后才会被清理 removeAll(), removeAll()后面加一个断点
            fastThreadLocal.set(1);
            ftlWeakRef.get().set(2);    //这么写应该是有问题的，因为不能保证set前弱引用不被回收
            System.gc();
            System.out.println("Task end");     //这个时候还未退出run()即使ftlWeakRef被回收了,FastThreadLocal的值也不会被回收,
                                                //因为FastThreadLocalRunnable在run()执行后才会调用FastThreadLocal的removeAll()方法
        }
    }

    public static void main(String[] args) {
        try {
            Thread thread = new FastThreadLocalThread(new Task(), "Thread-task");
            thread.start();

            FastThreadLocal<String> mainFtl = new FastThreadLocal<>();
            ThreadLocal<String> mainTl = new ThreadLocal<>();
            mainFtl.set("Hello");           //调试发现main线程中直接将FastThreadLocal对象插入到threadLocals，和子线程中内存存储方式不一样
            mainTl.set("Arvin");
            mainFtl = null;
            mainTl = null;
            System.gc();

            Thread.sleep(1000);
            System.out.println();           //这个时候看main线程是否还有线程本地变量(依旧存在)，main线程是否存在run()的概念

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
