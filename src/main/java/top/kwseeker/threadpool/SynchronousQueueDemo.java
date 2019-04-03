package top.kwseeker.threadpool;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * SynchronousQueue更像一个内部是队列的管道，读/写线程进行读写操作时都会阻塞知道对方来写/读。
 *
 * TransferStack中有段初始化代码
 * static {
 *      try {
 *          UNSAFE = sun.misc.Unsafe.getUnsafe();
 *          Class<?> k = TransferStack.class;
 *          headOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
 *      } catch (Exception e) {
 *          throw new Error(e);
 *      }
 * }
 * Unsafe 是sun.misc.Unsafe下的一个包，通过这个类可以直接使用底层native方法来获取和操作底层的数据，例如获取一个字段在内存中的偏移量，利用偏移量直接获取或修改一个字段的数据等等；
 * Unsafe类使Java拥有了像C语言的指针一样操作内存空间的能力，同时也带来了指针的问题；所以说是不安全的。
 */
public class SynchronousQueueDemo {

    static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " run at " + new Date().toString());
        }
    }

    static class TaskHandler implements Runnable {

        private SynchronousQueue<Runnable> queue;

        public TaskHandler(SynchronousQueue<Runnable> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                //TODO：这里模拟线程池的做法做成一个死循环，从而实现不释放线程重复处理任务
                for(;;) {
                    Runnable task = queue.take();
                    task.run();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //用于测试阻塞
    static abstract class BlockTestTask implements Runnable {

        private SynchronousQueue<Integer> queueInt;
        private CountDownLatch latch;

        BlockTestTask(SynchronousQueue<Integer> queueInt, CountDownLatch latch) {
            this.queueInt = queueInt;
            this.latch = latch;
        }

        @Override
        public void run() {
        }
    }

    public static void main(String[] args) {
        try {

            /**==========================================
             * SynchronousQueue 阻塞测试
             *==========================================*/
//            SynchronousQueue<Integer> queueInt = new SynchronousQueue<>();
//            CountDownLatch latch = new CountDownLatch(2);
//
//            new Thread(new BlockTestTask(queueInt, latch) {
//                @Override
//                public void run() {
//                    try {
//                        System.out.println("put thread start " + new Date().toString());
//                        Thread.sleep(2000);
//                        queueInt.put(1);
//                        System.out.println("put thread end " + new Date().toString());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } finally {
//                        latch.countDown();
//                    }
//                }
//            }).start();
//            new Thread(new BlockTestTask(queueInt, latch) {
//                @Override
//                public void run() {
//                    try {
//                        System.out.println("take thread start " + new Date().toString());
//                        Thread.sleep(4000);
//                        System.out.println("take content: " + queueInt.take());
//                        System.out.println("take thread end " + new Date().toString());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } finally {
//                        latch.countDown();
//                    }
//                }
//            }).start();
//            System.out.println("start put and get thread, then wait finish " + new Date().toString());
//            latch.await();
//            System.out.println("main thread out");

            /**==========================================
             * SynchronousQueue 模拟线程池线程消费任务
             *
             *==========================================*/
            SynchronousQueue<Runnable> queue = new SynchronousQueue<>();    //TransferStack, 双栈算法

            new Thread(() -> {  //模拟线程池刚创建，通过execute送进来一个任务
                queue.offer(new Task());
            }).start();
            Thread handleThread1 = new Thread(new TaskHandler(queue));
            handleThread1.start();
            new Thread(() -> {  //外部线程又通过execute送进来一个任务
                queue.offer(new Task());
            }).start();
            Thread handleThread2 = new Thread(new TaskHandler(queue));
            handleThread2.start();
            //假设常备线程数量为2，这时再送进来一个任务，就需要加锁判断常备线程是否有空闲的
            new Thread(() -> {
                queue.offer(new Task());
            }).start();
            Thread.State handler1State = handleThread1.getState();
            Thread.State handler2State = handleThread2.getState();
            if(handler1State == Thread.State.RUNNABLE || handler2State == Thread.State.RUNNABLE) {
                //有空闲
                if(handler1State == Thread.State.RUNNABLE) {
                    //
                } else {

                }
            } else {
                //没有空闲且线程数量不大于最大线程数量限制

            }


            queue.offer(new Task());
            queue.offer(new Task(), 1, TimeUnit.MILLISECONDS);
            queue.put(new Task());

            //检查
            boolean isEmpty = queue.isEmpty();
            int size = queue.size();


            //输出
            Runnable task = queue.take();
            task = queue.poll();
            task = queue.poll(1, TimeUnit.MICROSECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
