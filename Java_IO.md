# IO

参考： 

[Java IO Tutorial](http://tutorials.jenkov.com/java-io/index.html)  

### Java IO的作用

用于处理数据的输入输出，对应的就有数据的来源和去向，以及数据传输的形式。

数据源（也是数据去向）：
+ 文件
+ 其他线程
+ 网络
+ 内存
+ 标准IO（in/out/err）

数据传输形式：  
除非可以共享内存，否则线程之间数据共享（传输）都是以数据序列的形式传输的，即流。
RPC对象的序列化就是在内部把一块内存数据转化为字符序列。

+ 字节流
    以字节为单位
+ 字符流
    以字符为单位

数据的基本操作：

### Java IO 包（java.io）

Java IO 类UML：  
![Java io](picture/java-io.png)

核心抽象类：

字符流依赖字节流实现；  
抽象读写工具类，在其基础上实现了众多的读写器。

+ Writer  

    field:  
    &emsp; char[] writeBuffer;  (默认1024字符大小)  
    &emsp; Object lock;         (写方法都加了synchronized锁，都是线程安全的)  
    
    method:   
    &emsp; 定义或实现了 append() write() flush() close() 等方法；
    
+ Reader

    field:   
    &emsp; Object lock;         (skip()是线程安全的)  
    
    method:   
    &emsp; 定义或实现了 read() mark() markSupported() ready() reset() skip() close() 等方法；  
    &emsp; mark() 标记流当前位置；  
    &emsp; ready() 流是否可读；  
    &emsp; skip() 跳过N个字符，并把跳过的字符存到skipBuffer中；  
    &emsp; reset() 如果有mark操作则指定当前位置为mark的位置，否则根据具体实现类重置当前位置；  
    
+ OutputStream

    field：  
    &emsp; Nan
    
    method:  
    &emsp; 和 Writer 方法相同；但是调用了内核的 write 方法，即这个类提供了与内核交互的接口。  

+ InputStream

    field：  
    &emsp; Nan
    
    method:  
    &emsp; 和 Reader 方法基本相同，available()替代了ready()；
    调用了内核的 read 方法，即这个类提供了与内核交互的接口。  

其他类：

按I/O类型来总体分类：  

1.++从/向内存数组读写数据++: **CharArrayReader**、 **CharArrayWriter**、**ByteArrayInputStream**、**ByteArrayOutputStream**  
++从/向内存字符串读写数据++： **StringReader**、**StringWriter**、**StringBufferInputStream**  

2.++Pipe管道  实现管道的输入和输出（进程间通信）++: **PipedReader**、**PipedWriter**、**PipedInputStream**、**PipedOutputStream**  

3.++File 文件流。对文件进行读、写操作++ ：**FileReader**、**FileWriter**、**FileInputStream**、**FileOutputStream**  

4.++ObjectSerialization 对象输入、输出++ ：**ObjectInputStream**、**ObjectOutputStream**  

5.++DataConversion数据流 按基本数据类型读、写（处理的数据是Java的基本类型（如布尔型，字节，整数和浮点数））++：**DataInputStream**、**DataOutputStream**  

6.++Printing包含方便的打印方法++ ：**PrintWriter**、**PrintStream**  

7.++Buffering缓冲 在读入或写出时，对数据进行缓存，以减少I/O的次数++：**BufferedReader**、**BufferedWriter**、**BufferedInputStream**、**BufferedOutputStream**  

8.++Filtering 滤流，在数据进行读或写时进行过滤++：**FilterReader**、**FilterWriter**、**FilterInputStream**、**FilterOutputStream过**  

9.++Concatenation合并输入 把多个输入流连接成一个输入流++ ：**SequenceInputStream**  

10.++Counting计数  在读入数据时对行记数++ ：**LineNumberReader**、**LineNumberInputStream**      

11.++Peeking Ahead 通过缓存机制，进行预读++ ：**PushbackReader**、**PushbackInputStream**  

12.++Converting between Bytes and Characters 按照一定的编码/解码标准将字节流转换为字符流，或进行反向转换（Stream到Reader,Writer的转换类）++：**InputStreamReader**、**OutputStreamWriter**  

重点讲解：  

+ OutputStreamWriter

+ FileWriter


### Java IO 包的使用

+ 文件读写、随机访问、路径控制

    - 相关类
    
        File （指代一个文件或目录，继承 Serializable 和 Comparable<File> 接口）  
        FileSystem  
        &emsp; WinNTFileSystem  
        FileDescriptor  
        FileFilter   
        FilenameFilter  
        FilePermission  
        FileDescriptor  
        FileInputStream （字节流的方式写文件）  
        FileOutputStream （字节流的方式读文件）  
        FileReader （字符流的方式读文件，继承InputStreamReader）   
        FileWriter （字符流的方式写文件）  
        RandomAccessFile  
 
+ 内存的读写

    这里的内存指的是应用临时存储数据的空间，如一个数组一个String对象。
    
    - 相关类
        
        CharArrayReader  （可以以字符流的方式读取char数组）  
        CharArrayWriter  
        BufferedReader  
        BufferedWriter  (提供Writer实例的缓冲)
        ByteArrayInputStream
        ByteArrayOutputStream
        BufferedInputStream
        BufferedOutputStream
        StringReader  
        StringWriter  
    
+ 线程间的通信  
    
    - 相关类
        
        PipedInputStream （字节流的形式）
        PipedOutputStream
        PipedReader （字符流的形式） 
        PipedWriter  
        
+ 应用间的网络通信

+ 标准IO输入输出

+ 过滤流
    
    - 相关类
    
        FilterInputStream
        FilterOutputStream

+ 工具类流
    
    - 相关类
        
        PushbackInputStream (预读并推回)  
        PushbackReader  
        SequenceInputStream (多个流的数据合并到一个流)  
        DataInputStream (从InputStream中读取Primitives类型数据)  
        DataOutputStream  
        PrintStream  
        PrintWriter  
        ObjectInputStream  
        ObjectOutputStream  
        InputStreamReader  
        OutputStreamWriter  
        LineNumberReader  
        StreamTokenizer  

+ 各种流的相互转换
    
    - 转换的原理
    
        BufferedReader  
        BufferedWriter  (提供Writer实例的缓冲)
        BufferedInputStream
        BufferedOutputStream
        InputStreamReader  
        OutputStreamWriter  
        
    - 案例
    
### LinuxIO子系统及内核IO模型 (TODO)

目标：  
理解常见IO系统调用的工作原理以及内核5种IO模型的工作原理,
理解内核系统调用的阻塞原因，这几种IO模型做了什么优化，又有哪些缺陷？  

参考：  
[Anatomy of the Linux file system](https://www.ibm.com/developerworks/library/l-linux-filesystem/index.html)
《Linux系统编程》系统IO章节，Java IO是包装的系统调用，要全面理解IO，理解系统调用的工作原理必不可少。  
而且每个系统的IO系统调用不同，主要研究Linux。
[Linux IO子系统和文件系统读写流程分析](https://blog.csdn.net/kidd_3/article/details/6909097)

这里有几个个概念需要区分清楚：
Java IO、系统调用、内核IO子系统、内核IO模型。

Java IO是对系统调用的封装，系统调用是系统用户空间对内核空间的FileOperator的那一套函数指针的封装不仅针对存储设备还针对其他IO设备（这里IO设备指鼠标，键盘，显示器等外部设备），
内核IO模型是对系统调用使用上拓展设计的一些优化模板。    

从Android系统看  
Linux设备驱动提供的都是FileOperator那套函数指针的针对设备的具体实现，然后HAL层（C++）对这些函数指针做了抽象封装然后接口看上去更易于理解，
Android系统服务层对HAL接口则封装成服务根据应用需求及场景特异化出很多接口使接口更易用供应用使用。  
对于Linux操作系统系统调用应该相当于Android的HAL层。  

