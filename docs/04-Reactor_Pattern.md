# Reactor 模式

英文：Reactor Pattern

要更好地理解Netty代码架构并形成长期记忆还是先理解清Reactor响应式编程模式比较好。



## 基本概念

里面涉及一些抽象容易混淆的概念。

### NIO/Netty Reactor Pattern & Reactive Stream

维基百科关于响应式编程的定义：

**响应式编程**或**反应式编程**（英语：Reactive programming）是一种面向数据[流](https://zh.m.wikipedia.org/wiki/串流)和变化传播的[声明式](https://zh.m.wikipedia.org/wiki/声明式编程)[编程范式](https://zh.m.wikipedia.org/wiki/编程范式)。这意味着可以在编程语言中很方便地表达静态或动态的数据流，而相关的计算模型会自动将变化的值通过数据流进行传播。

> 要完全理解响应式编程概念需要理解流的实现原理，然后看响应式编程是如何处理流和变化传播的。

NIO/Netty Reactor模式和Reactive Stream（响应式编程的一种规范）都称为响应式编程，都包含Reactor这个概念，但是它们两个貌似并没有什么关系。Reactive Stream 的一个框架 Reactor3实现了对Netty符合Reactive Stream规范的改造，叫Reactor-Netty（是基于Reactor Core和Netty构建的反应式网络库）。

**NIO/Netty Reactor模式**源于《[Scalable IO in Java](https://gee.cs.oswego.edu/dl/cpjslides/nio.pdf)》中对于构建可伸缩的高性能IO服务的经验总结。



## NIO/Netty Reactor模式详解



