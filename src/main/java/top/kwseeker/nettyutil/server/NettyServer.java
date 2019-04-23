package top.kwseeker.nettyutil.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import lombok.extern.slf4j.Slf4j;

import top.kwseeker.nettyutil.server.handler.ServerHandler;

@Slf4j
public class NettyServer {

    /**
     * main函数中跑的逻辑就是创建 Reactor 线程模型主结构的。
     */
    public static void main(String[] args) {

        EventLoopGroup parentGroup = new NioEventLoopGroup(1);  //处理接收事件的线程组（这里只分配一个线程）
        EventLoopGroup childGroup = new NioEventLoopGroup();              //默认分配1个线程，可以通过代码传参设置，以及通过配置文件 io.netty.eventLoopThreads 配置

        try {
            //创建启动器及两个主要的线程组
            ServerBootstrap serverBootstrap = new ServerBootstrap();         //TODO：子线程池的属性：childOptions, childAttrs, config, childGroup, childHandler 父线程池的属性：group，channelFactory，localAddress，options，attrs，handler
            serverBootstrap.group(parentGroup, childGroup);                  //针对childGroup和group的赋值操作
            serverBootstrap.channel(NioServerSocketChannel.class);           //针对channelFactory的赋值操作
            serverBootstrap.handler(new ServerHandler());
            //启动参数配置
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)       //针对options的赋值操作，SO_BACKLOG用于设置存储排队请求的队列的长度
                    .option(ChannelOption.SO_KEEPALIVE, true)           //针对options的赋值操作，SO_KEEPALIVE设置保持长连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {     //针对childHandler的赋值操作，设置处理连接的channel
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8888).sync();   //使用ChannelFuture同步绑定端口（这步是服务初始化的真正开始，里面最核心的是ServerBootstrap的init()方法）
            future.channel().closeFuture().sync();                              //绑定完成后关闭ChannelFuture
        } catch (InterruptedException e) {
            log.error("NettyServer initial error: ", e);
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}

//parentGroup 和 childGroup 均是线程池；
//parentGroup 线程池只是在 Bind 某个端口后，获得其中一个线程作为 MainReactor，专门处理端口的 Accept 事件，每个端口对应一个 Boss 线程；
//workerGroup 线程池会被各个 SubReactor 和 Worker 线程充分利用。
//Netty 中的 I/O 操作是异步的，包括 Bind、Write、Connect 等操作会简单的返回一个 ChannelFuture。
//调用者并不能立刻获得结果，而是通过 Future-Listener 机制，用户可以方便的主动获取或者通过通知机制获得 IO 操作结果。

//private ChannelFuture doBind(final SocketAddress localAddress) {
//    final ChannelFuture regFuture = initAndRegister();                                        //创建NioServerSocketChannel实例，并按照childOptions中的配置对每个channel进行设置，同理对channel设置attrs；
                                                                                                //然后获取channel的pipeline，并在pipeline的末尾添加channelHandler(即刚才的childHandler)，每添加一个handler就从线程池拿一个线程处理这个handler
//    final Channel channel = regFuture.channel();
//    if (regFuture.cause() != null) {
//        return regFuture;
//    }
//
//    // ...
//}

//TODO: 详细分析init()流程
//@Override
//void init(Channel channel) throws Exception {
//    final Map<ChannelOption<?>, Object> options = options0();
//    synchronized (options) {
//        setChannelOptions(channel, options, logger);
//    }
//
//    final Map<AttributeKey<?>, Object> attrs = attrs0();
//    synchronized (attrs) {
//        for (Entry<AttributeKey<?>, Object> e: attrs.entrySet()) {
//            @SuppressWarnings("unchecked")
//            AttributeKey<Object> key = (AttributeKey<Object>) e.getKey();
//            channel.attr(key).set(e.getValue());
//        }
//    }
//
//    ChannelPipeline p = channel.pipeline();
//
//    final EventLoopGroup currentChildGroup = childGroup;
//    final ChannelHandler currentChildHandler = childHandler;
//    final Entry<ChannelOption<?>, Object>[] currentChildOptions;
//    final Entry<AttributeKey<?>, Object>[] currentChildAttrs;
//    synchronized (childOptions) {
//        currentChildOptions = childOptions.entrySet().toArray(newOptionArray(0));
//    }
//    synchronized (childAttrs) {
//        currentChildAttrs = childAttrs.entrySet().toArray(newAttrArray(0));
//    }
//
//    p.addLast(new ChannelInitializer<Channel>() {
//        @Override
//        public void initChannel(final Channel ch) throws Exception {
//            final ChannelPipeline pipeline = ch.pipeline();
//            ChannelHandler handler = config.handler();
//            if (handler != null) {
//                pipeline.addLast(handler);
//            }
//
//            ch.eventLoop().execute(new Runnable() {
//                @Override
//                public void run() {
//                    pipeline.addLast(new ServerBootstrapAcceptor(
//                            ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
//                }
//            });
//        }
//    });
//}