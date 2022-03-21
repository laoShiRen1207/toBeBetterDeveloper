## 代码解析
仅对 ReentrantLock 公平锁进行代码解析
```java  
ReentrantLock lock = new ReentrantLock(true);
lock.lock();

Sync#lock();
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/4d9521b571004f9faf3fc3a7bed0d0fc.png)

[图片链接ReentrantLock#Lock()](https://img-blog.csdnimg.cn/4d9521b571004f9faf3fc3a7bed0d0fc.png)

