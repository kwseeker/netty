package top.kwseeker.nettycomponent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
* GlobalEventExecutor是具备任务队列的单线程事件执行器,其适合用来实行时间短，碎片化的任务
*/
public class GlobalEventExecutorDemo {

    public static void main(String[] args) {

        GlobalEventExecutor gee = GlobalEventExecutor.INSTANCE;

        try {
            System.out.println("GlobalEventExecutor initialized");
            //boolean isInEventLoop = gee.inEventLoop();        //当前线程不等于GlobalEventExecutor中缓存的线程实例或者缓存的那个线程实例为空的话
            gee.execute(() -> {                                 //加入到 taskQueue
                System.out.println("eventExecutor threadId:" + Thread.currentThread().getId() + " Api execute() test task");
            });
            //不要往里面提交计划任务
            //gee.schedule(() -> {                                //加入到 scheduledTaskQueue
            //    System.out.println("eventExecutor threadId:" + Thread.currentThread().getId() + " Api schedule() test task");
            //}, 500, TimeUnit.MILLISECONDS);

            Thread.sleep(1500);     //超过1s线程就会退出一次，退出前quietPeriodTask重新被添加到scheduledTaskQueue

            for (int i=0;i<5;i++) {
                Future<Long> future = gee.submit(new Callable<Long>() {
                    @Override
                    public Long call() {
                        System.out.println("eventExecutor threadId:" + Thread.currentThread().getId() + " inEventLoop:" + gee.inEventLoop());
                        return Thread.currentThread().getId();
                    }
                });
                System.out.println("future: " + future.get());
                Thread.sleep(300);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            gee.shutdownGracefully();
        }
    }
}

/*
GlobalEventExecutor initialized
eventExecutor threadId:11 Api execute() test task
eventExecutor threadId:12 inEventLoop:true
future: 12
eventExecutor threadId:12 inEventLoop:true
future: 12
eventExecutor threadId:13 inEventLoop:true
future: 13
eventExecutor threadId:13 inEventLoop:true
future: 13
eventExecutor threadId:13 inEventLoop:true
future: 13
*/
