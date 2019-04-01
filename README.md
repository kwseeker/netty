## Netty
[User guide for 4.x](https://github.com/kwseeker/netty.git)  
[Netty 4.x User Guide 中文翻译](https://waylau.gitbooks.io/netty-4-user-guide/)
[Netty API Reference (4.1.33.Final)](https://netty.io/4.1/api/index.html)

参考开源项目：  
[UncleCatMySelf/InChat](https://github.com/UncleCatMySelf/InChat)  
参考上述开源项目，封装成自己的日后可以复用的模版项目。

#### 预备知识
+ 事件驱动  
    参考：[为什么基于事件驱动的服务器能实现高并发？](https://www.zhihu.com/question/64727674)  
    
    - 最高分的回答解释了事件驱动模型出现的原因？  
        即通信层连接数很多，但是业务逻辑层连接相对较少（差两三个数量级）且每秒的并发请求却远低于通信层维持的连接数（业务逻辑层依赖通信层），
        如果通信层使用多线程为每一个连接（通常是长连接）创建一个线程，会导致大量的内存消耗，所以通信层使用事件驱动模型替代多线程，
        而业务逻辑层本身并发请求比较少，且是短连接请求结束就释放的那种，所以还是使用多线程(线程池)实现。
        使用事件事件驱动模型仅用几百个甚至区区几十个线程/进程，就满足了几万甚至几十万的并发连接。
    
        因为看到了事件驱动模型的优势，第二代驱动模型甚至将业务逻辑层也做成了事件驱动。  
        如：
        ```aidl
        Go的goroutine
        Python 3的coroutine
        Kotlin的coroutine
        nodejs的异步回调
        swoole 1的异步回调和swoole 2的coroutine
        erlang/elixir的process也算是coroutine
        VertX的异步回调
        ```
    
    - TCP epoll/iocp 等新模式

    - 事件驱动的原理  
        好多资料不说人话，还是图清晰明了
        ![事件驱动模型图](https://upload-images.jianshu.io/upload_images/11222983-71d582050fe05761.png?imageMogr2/auto-orient/)
        事件发生时主线程把事件放入事件队列，在另外线程不断循环消费事件列表中的事件，调用事件对应的处理逻辑处理事件。
        事件驱动方式也被称为消息通知方式，其实是设计模式中观察者模式的思路。    
        
        事件队列（event queue）：接收事件的入口，存储待处理事件  
        分发器（event mediator）：将不同的事件分发到不同的业务逻辑单元    
        事件通道（event channel）：分发器与处理器之间的联系渠道  
        事件处理器（event processor）：实现业务逻辑，处理完成后会发出事件，触发下一步操作  
    
    - TODO  
        Netty事件驱动源码实现分析  
        参考：
        [netty之事件驱动原理](https://blog.csdn.net/qq_26562641/article/details/50392308) 根据这篇文章调试跟踪一下执行流程  
        [这可能是目前最透彻的Netty原理架构解析](http://developer.51cto.com/art/201811/586203.htm) 根据这篇文章全面总结一下Netty原理与NIO  
        
+ 同步非阻塞（NIO） 
    - 四种IO模型：  
        具体判断某种IO实例是属于哪种模型需要看其java到内核的整体实现。  
        
        * 同步阻塞
        * 同步非阻塞   
            用户进程发起一个 IO 操作以后 边可 返回做其它事情，但是用户进程需要时不时的询问 IO 操作是否就绪，这就要求用户进程不停的去询问，
            从而引入不必要的 CPU 资源浪费
        * 异步阻塞  
            此种方式下是指应用发起一个 IO 操作以后，不等待内核 IO 操作的完成，等内核完成 IO 操作以后会通知应用程序，
            那么为什么说是阻塞的呢？因为此时是通过 select 系统调用来完成的，而 select 函数本身的实现方式是阻塞的
        * 异步非阻塞
        
    - 四种IO模型实例：BIO，伪异步IO，NIO，AIO  
        * BIO  
            同步阻塞IO；一个线程通过轮询的方式接收连接；处理连接请求的线程可能一个或多个； 
            为每一个连接请求创建一个线程处理虽然避免了处理阻塞但是请求较多的情况下会造成资源占用很高；  
            适合处理连接不多，单个连接任务量大的场景。 
        * 伪异步IO (Pseudo asynchronous IO)  
            同步阻塞IO：在BIO的基础上，使用线程池代替为每个请求创建一个请求线程，减少了资源占用，但是高并发请求情况下，
            仍然会发生处理阻塞。  
            适合处理连接相对BIO较多，单个连接任务相对较小的场景。
        * NIO  
            同步非阻塞IO，NIO之所以是同步，是因为它的accept/read/write方法的内核I/O操作都会阻塞当前线程，
            而这些方法为何会阻塞当前线程需要看其内核调用C语言的实现。 
            适合处理连接数多，单个连接任务相对较小的场景。
        * AIO  
            异步非阻塞IO，与NIO区别在于AIO是等读写过程完成后再去调用回调函数；  
            适合处理单个连接任务读写过程长的场景。  
            
    - NIO详解
        参考：Java_NIO.md
    
+ Netty中事件驱动与NIO的关系  

+ Reactor 模型  
    主从多线程模型

+ Netty高性能的原理  

+ Netty使用场景  
    高性能领域  
    多线程并发领域 
    异步通信领域  
    
+ WebSocket  
    是一个H5协议规范，通过握手机制建立一个类似TCP的连接（其实本质上是基于HTTP协议）；是解决客户端与服务器实时通信而产生的技术；
    WebSocket出现之前Web服务器和客户端一般基于Http的短连接和长连接；
    
    WebSocket声明周期  
    - 打开事件
    - 消息事件
    - 错误事件
    - 关闭事件
    
    
#### Netty框架  

是实现高性能、高可靠性的网络客户端和服务器端最基础的通信组件。
广泛应用于Web服务器分布式节点通信，游戏服务器，以及大数据等领域。

##### Netty与JDK NIO对比  
+ JDK原生NIO框架的缺陷   
    1）类库和API繁杂  
    2）工作量和难度大  
    3）存在Bug  
+ Netty优势  
    1）API简单  
    2）性能高  
    3）成熟稳定   

##### Netty逻辑架构

![Netty逻辑架构](https://upload-images.jianshu.io/upload_images/1500839-be1dab77a918ff38.jpg?imageMogr2/auto-orient/)

