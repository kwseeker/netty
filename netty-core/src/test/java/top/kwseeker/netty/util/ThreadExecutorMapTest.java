package top.kwseeker.netty.util;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import org.junit.Test;

import java.util.concurrent.Executor;

public class ThreadExecutorMapTest {

    @Test
    public void testEventExecutorMap() {
        Executor executor = new ThreadPerTaskExecutor(new DefaultThreadFactory(this.getClass()));
        //executor的“代理”(这个“代理”对象)
        SimpleEventExecutor eventExecutor = new SimpleEventExecutor(executor);
        eventExecutor.execute(() -> {
            System.out.println("Hello");
        });
    }

}
