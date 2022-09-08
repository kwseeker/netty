# Netty Tips



+ **DefaultChannelPromise sync() & syncUninterruptibly() 区别**

  最终实现是DefaultPromise的await()和awaitUninterruptibly()，代码比较简单；

  区别就是 sync() 实现的同步等待主线程被中断后会立即退出等待，而syncUninterruptibly() 实现的同步等待即使等待过程中被中断也要继续等待Promise完成，然后主线程再中断自己。

  syncUninterruptibly 不能按字面意思理解，而应该理解为Promise未返回前不可被中断。

  Demo: netty-core/src/test/java/top/kwseeker/netty/promise/PromiseSyncTest.java

  ```java
  @Override
  public Promise<V> await() throws InterruptedException {
      if (isDone()) {
          return this;
      }
  
      if (Thread.interrupted()) {		//线程已经被中断也不用wait()了，直接抛异常退出
          throw new InterruptedException(toString());
      }
  
      checkDeadLock();
  
      synchronized (this) {
          while (!isDone()) {
              incWaiters();
              try {
                  wait();		//如果被终端，这里会直接退出
              } finally {
                  decWaiters();
              }
          }
      }
      return this;
  }
  
  @Override
  public Promise<V> awaitUninterruptibly() {
      if (isDone()) {
          return this;
      }
  
      checkDeadLock();
  
      boolean interrupted = false;
      synchronized (this) {
          while (!isDone()) {
              incWaiters();
              try {
                  wait();
              } catch (InterruptedException e) {
                  // Interrupted while waiting.	即使被中断也直接catch掉，不退出，继续等待Promise返回
                  interrupted = true;
              } finally {
                  decWaiters();
              }
          }
      }
  
      if (interrupted) {		//前面等待Promise返回过程中有被中断，那么这里自己中断自己
          Thread.currentThread().interrupt();
      }
  
      return this;
  }
  ```

  

