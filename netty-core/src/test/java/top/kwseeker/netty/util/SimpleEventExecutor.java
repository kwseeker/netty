package top.kwseeker.netty.util;

import java.util.concurrent.Executor;

public class SimpleEventExecutor implements Executor {

    private final Executor executorProxy;

    public SimpleEventExecutor(Executor executor) {
        this.executorProxy = SimpleThreadExecutorMap.apply(executor, this);
    }

    @Override
    public void execute(Runnable command) {
        executorProxy.execute(command);
    }
}
