package top.kwseeker.threadpool;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolExecutorDemo {

    static class Task implements Runnable {

        private static int order = 0;
        private static Map<String, Integer> nameMap = new ConcurrentHashMap<>();
        private String name;
        private Integer costTime;

        static {
            nameMap.put("Arvin", 0);
            nameMap.put("Bob", 0);
            nameMap.put("Cindy", 0);
            nameMap.put("David", 0);
        }

        public Task(String name, Integer costTime) {
            this.name = name;
            this.costTime = costTime;
        }

        @Override
        public String toString() {
            return "{name:" + this.name + ",costTime:" + costTime + ",order:" + order;
        }

        public void run() {
            try {
                System.out.println("模拟的任务 begin");
                Thread.sleep(this.costTime);
                order++;
                nameMap.put(name, order);
                System.out.println("模拟的任务 end, result: " + this.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        ExecutorService executorService = null;

        try {
            //创建线程工厂，JUC只有Executors里面实现了两种线程工厂。
            ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
            //ThreadFactory privilegedThreadFactory = Executors.privilegedThreadFactory();

            //创建线程池
            executorService = new ThreadPoolExecutor(
                    0,
                    Integer.MAX_VALUE,
                    60L,
                    TimeUnit.SECONDS,
                    new SynchronousQueue<>(),
                    defaultThreadFactory);

            //创建任务 RunnableAdapter
            Callable<Object> callableTask = Executors.callable(new Task("Arvin", 500));
            Integer rank = 0;
            Callable<Integer> callableTask2 = Executors.callable(new Task("Cindy", 600), rank);

            //线程池状态
            System.out.println("=====================================>");
            System.out.println("isAllowsCoreThreadTimeOut: " + ((ThreadPoolExecutor) executorService).allowsCoreThreadTimeOut());
            System.out.println("activeCount:" + ((ThreadPoolExecutor) executorService).getActiveCount());
            System.out.println("completeTaskCount:" + ((ThreadPoolExecutor) executorService).getCompletedTaskCount());
            System.out.println("corePoolSize:" + ((ThreadPoolExecutor) executorService).getCorePoolSize());
            System.out.println("keepAliveTime:" + ((ThreadPoolExecutor) executorService).getKeepAliveTime(TimeUnit.SECONDS));
            System.out.println("largestPoolSize:" + ((ThreadPoolExecutor) executorService).getLargestPoolSize());
            System.out.println("maximumPoolSize:" + ((ThreadPoolExecutor) executorService).getMaximumPoolSize());
            System.out.println("poolSize:" + ((ThreadPoolExecutor) executorService).getPoolSize());
            System.out.println("taskCount:" + ((ThreadPoolExecutor) executorService).getTaskCount());

            //执行任务
            //测试1
            //TODO：下面这么写有问题，因为线程返回写回来写的过程是阻塞的，导致后面 println 是等待所有线程执行完成才会执行，使用Selector模型解决
//            executorService.execute(new Task("Bob", 300));          //提交Runnable无返回任务
//            Future<Object> objectFuture = executorService.submit(callableTask);     //提交Callable有返回任务,但是这个任务总是返回null
//            Future<Integer> integerFuture = executorService.submit(callableTask2);
//            System.out.println("rank: " + integerFuture.get());
            //测试2
            //不获取返回值，然后主线程不会阻塞
            executorService.execute(new Task("Bob", 300));          //提交Runnable无返回任务
            executorService.submit(callableTask);     //提交Callable有返回任务,但是这个任务总是返回null
            executorService.submit(callableTask2);
            //测试3
            //invokeAll()是将返回写入到链表,也不失为对测试1的一种解决方法
            //executorService.invokeAll();            //批量提交callableTask,等待所有返回
            //executorService.invokeAny();            //批量提交callableTask,等待一个返回

            //线程池状态
            System.out.println("=====================================>");
            System.out.println("isAllowsCoreThreadTimeOut: " + ((ThreadPoolExecutor) executorService).allowsCoreThreadTimeOut());
            System.out.println("activeCount:" + ((ThreadPoolExecutor) executorService).getActiveCount());
            System.out.println("completeTaskCount:" + ((ThreadPoolExecutor) executorService).getCompletedTaskCount());
            System.out.println("corePoolSize:" + ((ThreadPoolExecutor) executorService).getCorePoolSize());
            System.out.println("keepAliveTime:" + ((ThreadPoolExecutor) executorService).getKeepAliveTime(TimeUnit.SECONDS));
            System.out.println("largestPoolSize:" + ((ThreadPoolExecutor) executorService).getLargestPoolSize());
            System.out.println("maximumPoolSize:" + ((ThreadPoolExecutor) executorService).getMaximumPoolSize());
            System.out.println("poolSize:" + ((ThreadPoolExecutor) executorService).getPoolSize());
            System.out.println("taskCount:" + ((ThreadPoolExecutor) executorService).getTaskCount());

            //关闭线程池
            executorService.shutdown(); //所有线程执行完毕或者异常退出后才会执行关闭操作
            executorService = null;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
        } finally {
            if(executorService != null) {
                executorService.shutdownNow();
            }
        }
    }
}
