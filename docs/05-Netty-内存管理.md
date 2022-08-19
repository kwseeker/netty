# Netty内存管理



## jemalloc



## ByteBufAllocator

这里主要说PooledByteBufAllocator，它在Netty中是共用的静态对象



## References

+ https://github.com/jemalloc/jemalloc

+ [A Scalable Concurrent malloc(3) Implementation for FreeBSD](https://people.freebsd.org/~jasone/jemalloc/bsdcan2006/jemalloc.pdf)

+ [他山之石：高性能内存分配器 jemalloc 基本原理](https://learn.lianglianglee.com/专栏/Netty 核心原理剖析与 RPC 实践-完/12  他山之石：高性能内存分配器 jemalloc 基本原理.md))

+ 举一反三：Netty 高性能内存管理设计（上）
+ 举一反三：Netty 高性能内存管理设计（下）
+ 轻量级对象回收站：Recycler 对象池技术解析