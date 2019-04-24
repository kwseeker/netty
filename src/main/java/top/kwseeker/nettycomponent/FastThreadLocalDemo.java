package top.kwseeker.nettycomponent;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import sun.jvm.hotspot.oops.ObjectHeap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

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

    public static class TaskForIndexTest implements Runnable {
        private static final FastThreadLocal FTL1 = new FastThreadLocal<Object>() {
            @Override
            public Object initialValue() {
                return new Object();
            }
        };
        private static final FastThreadLocal FTL2 = new FastThreadLocal<Object>() {
            @Override
            public Object initialValue() {
                return new Object();
            }
        };

        @Override
        public void run() {
            try {
                System.out.println("FTL1:" + FTL1.get() + "  FTL2:" + FTL2.get());
                Field ftlIndex = FastThreadLocal.class.getDeclaredField("index");
                ftlIndex.setAccessible(true);
                System.out.println("FTL1.index=" + ftlIndex.get(FTL1) + "  FTL2.index=" + ftlIndex.get(FTL2));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static final FastThreadLocal<Object> FTL = new FastThreadLocal<>();

    public static void main(String[] args) {
//        try {
//            Thread thread = new FastThreadLocalThread(new Task(), "Thread-task");
//            thread.start();
//
//            FastThreadLocal<String> mainFtl = new FastThreadLocal<>();
//            ThreadLocal<String> mainTl = new ThreadLocal<>();
//            mainFtl.set("Hello");           //调试发现main线程中直接将FastThreadLocal对象插入到threadLocals，和子线程中内存存储方式不一样
//            mainTl.set("Arvin");
//            mainFtl = null;
//            mainTl = null;
//            System.gc();
//
//            Thread.sleep(1000);
//            System.out.println();           //这个时候看main线程是否还有线程本地变量(依旧存在)，main线程是否存在run()的概念
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //测试不同线程FastThreadLocal的index值是否会相等
        //FTL1.index=1  FTL2.index=2
        //FTL1.index=1  FTL2.index=2    由此可见只有在同一个线程中FastThreadLocal才会不同
//        new Thread(new TaskForIndexTest()).start();
//        new Thread(new TaskForIndexTest()).start();

        //通过简单的Demo调试理解FastThreadLocal的工作原理
        Object defaultVal = FTL.get();
        System.out.println(defaultVal);
        assert defaultVal == null;       //InternalThreadLocalMap被创建时默认会填充满同一个空Object对象，但是get()时又将其设置成null。
        FTL.set(new Object());
        Object currentVal = FTL.get();
        System.out.println(currentVal);
        assert defaultVal != currentVal;
        FTL.remove();

        new FastThreadLocalThread(new Runnable() {
            private final FastThreadLocal<Object> FTL = new FastThreadLocal<>();
            @Override
            public void run() {
                Object defaultVal = FTL.get();
                FTL.set(new Object());
                Object currentVal = FTL.get();
                System.out.println(defaultVal + " " + currentVal);
                FTL.remove();
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();   //这里打个断点看看FTL在线程threadLocals中的值是否都被清理了
    }
}
