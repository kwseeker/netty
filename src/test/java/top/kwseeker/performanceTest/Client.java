package top.kwseeker.performanceTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class Client {

    private static final String SERVER_HOST = "192.168.1.100";
    private static final int SERVERT_PORT_COUNT = 100;

    public static void start(final int beginPort) {
        start(beginPort, SERVERT_PORT_COUNT);
    }

    public static void start(final int beginPort, int portCount) {
        log.info("Clients starting ...");
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {

                    }
                });

        int index = 0;
        int port = beginPort;
        while (!Thread.interrupted()) {
            port = beginPort + index;
            try {
                ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, port);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(!future.isSuccess()) {
                            log.error("Connect to sever failed, exit!");
                            System.exit(0);
                        }
                    }
                });
                channelFuture.get();
            } catch (InterruptedException e) {
                log.error("Exception: e=" + e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                log.error("Exception: e=" + e.getMessage());
                e.printStackTrace();
            }
            if(++index == portCount) {
                index = 0;
            }
        }
    }
}
