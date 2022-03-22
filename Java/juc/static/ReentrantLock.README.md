## 代码解析
仅对 ReentrantLock 公平锁进行代码解析
```java  
ReentrantLock lock = new ReentrantLock(true);
lock.lock();

Sync#lock();
```

实际调用这个AQS的acquire的模板

```java
public final void acquire(int arg) {
    //addWaiter 节点为独占模式Node.EXCLUSIVE
    if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)){
        selfInterrupt();
    }
}
```

```java
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    // 先尝试使用CAS直接入队，如果这个时候其他线程也在入队（就是不止一个线程在同一时间争抢这把锁）就进入enq()
    Node pred = tail;
    if (pred != null) {
        node.prev = pred;
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
  	//此方法是CAS快速入队失败时调用
    enq(node);
    return node;
}

private Node enq(final Node node) {
  	//自旋形式入队，可以看到这里是一个无限循环
    for (;;) {
        Node t = tail;
        //这种情况只能说明头结点和尾结点都还没初始化
        if (t == null) {  
            //初始化头结点和尾结点
            if (compareAndSetHead(new Node()))   
                tail = head;
        } else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;   //只有CAS成功的情况下，才算入队成功，如果CAS失败，那说明其他线程同一时间也在入队，并且手速还比当前线程快，刚好走到CAS操作的时候，其他线程就先入队了，那么这个时候node.prev就不是我们预期的节点了，而是另一个线程新入队的节点，所以说得进下一次循环再来一次CAS，这种形式就是自旋
            }
        }
    }
}
```

```java
@ReservedStackAccess
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            //可以看到当此节点位于队首(node.prev == head)时，会再次调用tryAcquire方法获取锁，如果获取成功，会返回此过程中是否被中断的值
            if (p == head && tryAcquire(arg)) {   
                //新的头结点设置为当前结点
                setHead(node);    
                // 原有的头结点没有存在的意义了
                p.next = null; 
                 //没有失败
                failed = false;  
                //直接返回等待过程中是否被中断
                return interrupted;   
            }	
          	//依然没获取成功，
            //将当前节点的前驱节点等待状态设置为SIGNAL，如果失败将直接开启下一轮循环，直到成功为止，如果成功接着往下
            if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
                //挂起线程进入等待状态，等待被唤醒，如果在等待状态下被中断，那么会返回true，直接将中断标志设为true，否则就是正常唤醒，继续自旋
                interrupted = true;
            }
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}

private final boolean parkAndCheckInterrupt() {
    //通过unsafe类操作底层挂起线程（会直接进入阻塞状态）
    LockSupport.park(this);   
    return Thread.interrupted();
}
```



```java
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    if (ws == Node.SIGNAL)
        return true;   //已经是SIGNAL，直接true
    if (ws > 0) {   //不能是已经取消的节点，必须找到一个没被取消的
        do {
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;   //直接抛弃被取消的节点
    } else {
        //不是SIGNAL，先CAS设置为SIGNAL（这里没有返回true因为CAS不一定成功，需要下一轮再判断一次）
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;   //返回false，马上开启下一轮循环
}
```

`tryAcquire`

```java
@ReservedStackAccess
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();   //先获取当前线程的Thread对象
    int c = getState();     //获取当前AQS对象状态（独占模式下0为未占用，大于0表示已占用）
    if (c == 0) {       //如果是0，那就表示没有占用，现在我们的线程就要来尝试占用它
        if (!hasQueuedPredecessors() &&    //等待队列是否不为空且当前线程没有拿到锁，其实就是看看当前线程有没有必要进行排队，如果没必要排队，就说明可以直接获取锁
            compareAndSetState(0, acquires)) {   //CAS设置状态，如果成功则说明成功拿到了这把锁，失败则说明可能这个时候其他线程在争抢，并且还比你先抢到
            setExclusiveOwnerThread(current);    //成功拿到锁，会将独占模式所有者线程设定为当前线程（这个方法是父类AbstractOwnableSynchronizer中的，就表示当前这把锁已经是这个线程的了）
            return true;   //占用锁成功，返回true
        }
    }
    else if (current == getExclusiveOwnerThread()) {   //如果不是0，那就表示被线程占用了，这个时候看看是不是自己占用的，如果是，由于是可重入锁，可以继续加锁
        int nextc = c + acquires;    //多次加锁会将状态值进行增加，状态值就是加锁次数
        if (nextc < 0)   //加到int值溢出了？
            throw new Error("Maximum lock count exceeded");
        setState(nextc);   //设置为新的加锁次数
        return true;
    }
    return false;   //其他任何情况都是加锁失败
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/fc06962d2e2b4ed089a0a1a2b447fcf2.png)




[图片链接ReentrantLock#Lock()](https://img-blog.csdnimg.cn/fc06962d2e2b4ed089a0a1a2b447fcf2.png)


