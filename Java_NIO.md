## Java NIO

参考资料：  
《Java NIO》

目的提升IO效率，处理性能已经不是瓶颈，IO性能才是软件性能的瓶颈。

#### 基础概念
+ 缓冲区操作  
    应用进程所谓的"IO"无非是将数据写进或移出缓冲区（主要关注这部分）。
    然后调用内核调用实现内核到硬件的操作（研究这部分去看《Unix系统编程》，有讲内核IO的操作与实现）。
    
+ 内核空间与用户空间
    这个不用说了，主要是隔离，之间通过系统调用通信。
    
+ 虚拟内存  
    了解内核调用特性时的基础。
    
+ 分页技术  
    了解内核调用特性时的基础。
    
+ 面向文件的IO和流IO
  
+ 多工IO

#### java.nio 类组

+ Channel及Selector类

+ Charset类

+ File类

+ Buffer类

    包含了7种基础数据类型的很多Buffer实现类

+ 异常类

+ CharBufferSpliterator

+ ByteOrder

+ Bits

#### NIO 组件

+ Buffer 缓冲区 

  Java NIO缓冲区是如何与通道联系的？

  为何需要缓冲区？

  ![image-20190215111750869](/Users/lee/Library/Application Support/typora-user-images/image-20190215111750869.png)

  缓冲区是包在一个对象内的基本数据元素数组。  

  包含容量（Capacity）、上界（Limit，缓冲区现存元素的计数）、读写位置Position、标记（Mark，备忘位置） 

  如何创建、读写、复制缓冲区？

+ Channel 通道  

  如何打开，执行读写等操作、关闭通道？
  
  Scatter/Gather？
  
  文件通道访问文件以及锁定文件？
  
  内存映射文件的使用？
  
  新的FileChannel类提供了一个名为map()的方法，该方法在一个打开的文件和一个特殊类型的ByteBuffer之间建立一个虚拟内存映射；
  
+ Selector 选择器  

  TODO：结合JVM深入研究实现。

  Mac-OS 默认实现是 KQueueSelectorImpl。
  ```
  KQueueSelectorImpl
    -> SelectorImpl
      -> AbstractSelector
        -> Selector
          -> Closeable
            -> AutoCloseable
  ```
  依赖系统的非阻塞Pipe实现。
  