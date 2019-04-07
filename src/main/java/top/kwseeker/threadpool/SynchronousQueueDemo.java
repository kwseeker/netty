package top.kwseeker.threadpool;

import java.util.Date;
import java.util.concurrent.*;

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
            System.out.println(Thread.currentThread().getName() + " run at " + System.nanoTime() + "====================>");
            System.out.println(Thread.currentThread().getName() + " date: " + new Date().toString());  //耗时几十毫秒
        }
    }

    //模拟线程池的工作者线程
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

            /**==========================================
             * SynchronousQueue 阻塞测试
             *==========================================*/
//        try {
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
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

            /**==========================================
             * SynchronousQueue newCachedThreadPool
             *==========================================*/
//            System.out.println(System.nanoTime());
//            String dateTime = new Date().toString();        //这个竟然耗时20-30ms左右
//            System.out.println(System.nanoTime());
//            long mills = System.currentTimeMillis();        //(我的电脑)测试大概耗时50us，currentTimeMillis() 起始时间是基于 1970.1.1 0:00:00 这个确定的时间的
//            System.out.println(System.nanoTime());
//            System.out.println(System.nanoTime());
//            long nanos = System.nanoTime();                 //测试相对比currentTimeMillis()更精确几乎和没有执行这个语句一样， nanoTime() 基于cpu核心的启动时间开始计算的
//            System.out.println(System.nanoTime());
//            System.out.println(System.nanoTime());

            ExecutorService es = Executors.newCachedThreadPool();
            try {
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(2, 300000);      //线程的创建是很耗时的，大概几ms时间（和机器处理能力有关），但是将
                    es.submit(new Task());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /* 打印的结果为什么是这个样子？因为SynchronousQueue以及线程池实现要求必须有线程在等待队列数据的时候才能成功插入，
            看newCacheThreadPool的实现得知，常备线程数为0，然后尝试将工作插入SynchronousQueue工作队列，但是没有消费者正在等着取工作的时候是无法插入的，
            所以前面建了一批线程，然后这批线程有线程执行完工作阻塞地等SynchronousQueue工作队列中的工作的时候(最多等60s)，这时再插入任务，就可以成功插入了，然后就由此
            线程处理。TODO：还有个疑问为何总是被7号线程抢到？
            pool-1-thread-1 run at 328615778007744====================>
            pool-1-thread-2 run at 328615780326893====================>
            pool-1-thread-3 run at 328615782524528====================>
            pool-1-thread-4 run at 328615785168219====================>
            pool-1-thread-5 run at 328615787513055====================>
            pool-1-thread-6 run at 328615789891893====================>
            pool-1-thread-7 run at 328615792177691====================>
            pool-1-thread-8 run at 328615794798035====================>
            pool-1-thread-9 run at 328615796973900====================>
            pool-1-thread-1 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-8 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-4 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-5 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-9 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-10 run at 328615799616368====================>
            pool-1-thread-2 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-3 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-6 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-10 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615802074652====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615804587195====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615806635397====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615809145682====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615811459667====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615813985923====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615816548596====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615818817355====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615821112392====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            pool-1-thread-7 run at 328615823654118====================>
            pool-1-thread-7 date: Sun Apr 07 13:29:05 CST 2019
            */

            /**==========================================
             * SynchronousQueue 模拟线程池线程消费任务
             * 模拟一个常备线程数为2的线程池
             *==========================================*/
//            SynchronousQueue<Runnable> queue = new SynchronousQueue<>();    //TransferStack, 双栈算法
//
//            new Thread(() -> {  //模拟线程池刚创建，通过execute送进来一个任务
//                queue.offer(new Task());
//            }).start();
//            Thread handleThread1 = new Thread(new TaskHandler(queue));
//            handleThread1.start();
//            new Thread(() -> {  //外部线程又通过execute送进来一个任务
//                queue.offer(new Task());
//            }).start();
//            Thread handleThread2 = new Thread(new TaskHandler(queue));
//            handleThread2.start();
//            //假设常备线程数量为2，这时再送进来一个任务，就需要加锁判断常备线程是否有空闲的
//            new Thread(() -> {
//                queue.offer(new Task());
//            }).start();
//            Thread.State handler1State = handleThread1.getState();
//            Thread.State handler2State = handleThread2.getState();
//            if(handler1State == Thread.State.RUNNABLE || handler2State == Thread.State.RUNNABLE) {
//                //有空闲
//                if(handler1State == Thread.State.RUNNABLE) {
//
//                } else {
//
//                }
//            } else {
//                //没有空闲且线程数量不大于最大线程数量限制
//                new Thread(new TaskHandler(queue)).start();
//            }


//            queue.offer(new Task());
//            queue.offer(new Task(), 1, TimeUnit.MILLISECONDS);
//            queue.put(new Task());
//
//            //检查
//            boolean isEmpty = queue.isEmpty();
//            int size = queue.size();
//
//
//            //输出
//            Runnable task = queue.take();
//            task = queue.poll();
//            task = queue.poll(1, TimeUnit.MICROSECONDS);
    }
}
