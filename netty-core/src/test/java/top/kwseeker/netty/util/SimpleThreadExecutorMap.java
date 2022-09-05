package top.kwseeker.netty.util;

import io.netty.util.concurrent.FastThreadLocal;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * 将原ThreadExecutorMap不重要的代码删除后的类
 */
public class SimpleThreadExecutorMap {

    private static final FastThreadLocal<SimpleEventExecutor> mappings = new FastThreadLocal<>();

    public static SimpleEventExecutor currentExecutor() {
        return mappings.get();
    }

    private static void setCurrentEventExecutor(SimpleEventExecutor executor) {
        mappings.set(executor);
    }

    public static Executor apply(final Executor executor, final SimpleEventExecutor eventExecutor) {
        return new Executor() {
            public void execute(Runnable command) {
                executor.execute(SimpleThreadExecutorMap.apply(command, eventExecutor));
            }
        };
    }

    public static Runnable apply(final Runnable command, final SimpleEventExecutor eventExecutor) {
        return new Runnable() {
            public void run() {
                SimpleThreadExecutorMap.setCurrentEventExecutor(eventExecutor);

                try {
                    command.run();
                } finally {
                    SimpleThreadExecutorMap.setCurrentEventExecutor(null);
                }

            }
        };
    }

    public static ThreadFactory apply(final ThreadFactory threadFactory, final SimpleEventExecutor eventExecutor) {
        return new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return threadFactory.newThread(SimpleThreadExecutorMap.apply(r, eventExecutor));
            }
        };
    }
}
