# Java 安全控制

参考：  
《深入解析JVM实现》  
[Java 安全模型介绍](https://www.ibm.com/developerworks/cn/java/j-lo-javasecurity/)

## Java最新的安全模型

![](https://www.ibm.com/developerworks/cn/java/j-lo-javasecurity/image009.gif)

虚拟机会把所有代码加载到不同的系统域和应用域，系统域部分专门负责与关键资源进行交互，而各个应用域部分则通过系统域的部分代理来对各种需要的资源进行访问。
虚拟机中不同的受保护域 (Protected Domain)，对应不一样的权限 (Permission)。存在于不同域中的类文件就具有了当前域的全部权限。

在应用开发中还有一些关于安全的复杂用法，其中最常用到的 API 就是 doPrivileged。
doPrivileged 方法能够使一段受信任代码获得更大的权限，甚至比调用它的应用程序还要多，可做到临时访问更多的资源。
有时候这是非常必要的，可以应付一些特殊的应用场景。例如，应用程序可能无法直接访问某些系统资源，但这样的应用程序必须得到这些资源才能够完成功能。
针对这种情况，Java SDK 给域提供了 doPrivileged 方法，让程序突破当前域权限限制，临时扩大访问权限。

## Java AccessController机制原理实现

