package top.kwseeker.nettycomponent;

import io.netty.util.concurrent.*;

public class DefaultPromiseDemo {

    public static void main(String[] args) {

        GlobalEventExecutor gee = GlobalEventExecutor.INSTANCE;

        final Promise<?> terminationFuture = new DefaultPromise<Void>(gee);                 //Void标识无返回值
        final FutureListener<Object> terminationListener = new FutureListener<Object>() {
            @Override
            public void operationComplete(Future<Object> future) throws Exception {
                System.out.println("operationComplete");
            }
        };
        terminationFuture.addListener(terminationListener);

        gee.execute(() -> {
            System.out.println("A simple test task");
            terminationFuture.setSuccess(null);
        });

        GlobalEventExecutor.INSTANCE.shutdownGracefully();
    }
}
