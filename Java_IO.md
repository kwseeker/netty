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
+ 字节流
+ 字符流

数据的基本操作：

### Java IO 包（java.io）

核心类：

抽象读写工具类，在其基础上实现了众多的读写器

+ Writer

+ Reader

+ InputStream

+ OutputStream

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

### Java IO 包的使用

+ 文件读写、随机访问、路径控制

    - 相关类
    
        File （指代一个文件或目录，继承 Serializable 和 Comparable<File> 接口）  
        FileSystem  
        FileDescriptor  
        FileFilter  
        FilenameFilter
        FilePermission
        FileInputStream （字节流的方式写文件）  
        FileOutputStream （字节流的方式读文件）  
        FileReader （字符流的方式读文件）   
        FileWriter （字符流的方式写文件）  
       
    
+ 线程间的通信  

+ 应用间的网络通信

+ 内存的读写

+ 标准IO输入输出

+ 



