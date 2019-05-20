package top.kwseeker.nettycomponent.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * 不适合处理耗时长的任务，不适合处理对时间精度要求高的任务（随着任务增多触发延迟会越来越严重）
 */
public class HashWheelTimerDemo {

    public static void main(String[] args) throws Exception {
        long tickDuration = 10L;    //10ms

        //默认是512个刻度 * 10ms, 理论上 5.12s round一周
        HashedWheelTimer timer = new HashedWheelTimer(tickDuration, TimeUnit.MILLISECONDS);    //tickDuration最小为1ms
        timer.start();
        //添加10个任务，5、6、7...秒后执行
        Timeout[] timeout = new Timeout[10];
        System.out.println("Current time：" + System.currentTimeMillis());

        //加入一个耗时的任务，测试是否会影响其他任务按时执行
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println(System.currentTimeMillis() + " start long time task");
                Thread.sleep(2500);
                System.out.println(System.currentTimeMillis() + " end long time task");
            }
        }, 4, TimeUnit.SECONDS);

        for(int i=0; i < 10; i++) {
            timeout[i] = timer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    System.out.println(System.currentTimeMillis() + " Time expired and finish task");
                }
            }, 5+i, TimeUnit.SECONDS);
        }

        assert timeout[1].cancel();
        assert timeout[4].cancel();
        assert timeout[7].cancel();

        Thread.sleep(100000);
        timer.stop();
    }
}

/*
Current time：1558340050808
1558340054821 start long time task
1558340057325 end long time task
1558340057325 Time expired and finish task  //本来应该在1558340055808时执行的
1558340057818 Time expired and finish task
1558340058819 Time expired and finish task
1558340060821 Time expired and finish task
1558340061819 Time expired and finish task
1558340063820 Time expired and finish task
1558340064818 Time expired and finish task
*/