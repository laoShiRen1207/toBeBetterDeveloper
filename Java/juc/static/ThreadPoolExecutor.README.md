```java
package com.laoshiren.hello.juc.pool;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadPoolExecutor extends AbstractExecutorService {

    // 这个变量比较关键，用到了原子AtomicInteger，用于同时保存线程池运行状态和线程数量
    // 它是通过拆分32个bit位来保存数据的，前3位保存状态，后29位保存工作线程数量
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    //29位，线程数量位 Integer.SIZE = 32
    private static final int COUNT_BITS = Integer.SIZE - 3;
    // 1 左移29位 就是 10000000000...
    private static final int CAPACITY = (1 << COUNT_BITS) - 1;


    // 所有的运行状态，注意都是只占用前3位，不会占用后29位
    // 接收新任务，并等待执行队列中的任务
    private static final int RUNNING = -1 << COUNT_BITS;   //111 | 0000... (后29数量位，下同)
    // 不接收新任务，但是依然等待执行队列中的任务
    private static final int SHUTDOWN = 0 << COUNT_BITS;   //000 | 数量位
    // 不接收新任务，也不执行队列中的任务，并且还要中断正在执行中的任务
    private static final int STOP = 1 << COUNT_BITS;   //001 | 数量位
    // 所有的任务都已结束，线程数量为0，即将完全关闭
    private static final int TIDYING = 2 << COUNT_BITS;   //010 | 数量位
    // 完全关闭
    private static final int TERMINATED = 3 << COUNT_BITS;   //011 | 数量位


    // 封装和解析ctl变量的一些方法
    //对CAPACITY取反就是后29位全部为0，前三位全部为1，接着与c进行与运算，这样就可以只得到前三位的结果了，所以这里是取运行状态
    private static int runStateOf(int c) {
        return c & ~CAPACITY;
    }

    // 获取工作线程数量
    private static int workerCountOf(int c) {
        return c & CAPACITY;
    }

    //同上，这里是为了得到后29位的结果，所以这里是取线程数量
    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }


    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    private final BlockingQueue<Runnable> workQueue = null;

    @Override
    public void execute(Runnable command) {
        //如果任务为null，那执行个寂寞，所以说直接空指针
        if (command == null) {
            throw new NullPointerException();
        }
        //获取ctl的值，一会要读取信息的
        int c = ctl.get();
        // 如果工作线程数量小于核心线程数
        if (workerCountOf(c) < corePoolSize) {
            // 添加一个新线程去执行
            if (addWorker(command, true)) {
                return;
            }
            //如果线程添加失败（有可能其他线程也在对线程池进行操作），那就更新一下c的值
            c = ctl.get();
        }
        //继续判断，如果当前线程池是运行状态，那就尝试向阻塞队列中添加一个新的等待任务
        if (isRunning(c) && workQueue.offer(command)) {
            //再次获取ctl的值
            int recheck = ctl.get();
            //这里是再次确认当前线程池是否关闭，如果添加等待任务后线程池关闭了，那就把刚刚加进去任务的又拿出来
            if (!isRunning(recheck) && remove(command)) {
                // 然后直接拒绝当前任务的提交（会根据我们的拒绝策略决定如何进行拒绝操作）
                reject(command);

            } else if (workerCountOf(recheck) == 0) {
                //如果这个时候线程池依然在运行状态，那么就检查一下当前工作线程数是否为0，如果是那就直接添加新线程执行
                addWorker(null, false);
            }
        } else if (!addWorker(command, false)) {
            //这种情况要么就是线程池没有运行，要么就是队列满了，这里再尝试添加一个非核心线程碰碰运气
            // 要是实在不行就拒绝
            reject(command);
        }
    }


    private boolean addWorker(Runnable firstTask, boolean core) {
        // 外层循环打标签
        retry:
        for (; ; ) {
            //获取ctl值
            int c = ctl.get();
            //解析当前的运行状态
            int rs = runStateOf(c);
            // RUNNING < SHUTDOWN < STOP < TIDYING < TERMINATED
            // 判断线程池是否不是处于运行状态
            if (rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())) {
                return false;
            }
            for (; ; ) {
                // 获取当前工作线程的数量
                int wc = workerCountOf(c);
                // 判断一下还装得下不，如果装得下，看看是核心线程还是非核心线程，如果是核心线程，不能大于核心线程数的限制，如果是非核心线程，不能大于最大线程数限制
                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize)) {
                    return false;
                }
                // cas 自增线程计数，如果增加成功，任务完成，直接跳出继续
                if (compareAndIncrementWorkerCount(c)) {
                    // 直接跳出最外层循环
                    break retry;
                }
                // 如果CAS失败，更新一下c的值
                c = ctl.get();
                //如果CAS失败的原因是因为线程池状态和一开始的不一样了，那么就重新从外层循环再来一次
                if (runStateOf(c) != rs) {
                    continue retry;
                }
            }
        }
        // 工作线程是否已启动
        boolean workerStarted = false;
        // 工作线程是否已添加
        boolean workerAdded = false;
        // 暂时理解为工作线程
        Worker w = null;
        try {
            // 创建新的工作线程，传入我们提交的任务
            w = new Worker(firstTask);
            //拿到工作线程中封装的Thread对象
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                // 只有一个线程能进入
                mainLock.lock();
                try {
                    // 获取当前线程的运行状态
                    int rs = runStateOf(ctl.get());
                    //只有当前线程池是正在运行状态，或是SHUTDOWN状态且firstTask为空，那么就继续
                    if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                        // 检查一下线程是否正在运行状态
                        if (t.isAlive()) {
                            throw new IllegalThreadStateException();
                        }
                        //直接将新创建的Work丢进 workers 集合中
                        workers.add(w);
                        int s = workers.size();
                        // 更新历史最大线程池
                        if (s > largestPoolSize) {
                            largestPoolSize = s;
                        }
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    //启动线程
                    t.start();
                    //工作线程已启动
                    workerStarted = true;
                }
            }
        } finally {
            if (!workerStarted) {
                // 如果线程在上面的启动过程中失败了
                // 将w移出workers并将计数器-1，最后如果线程池是终止状态，会尝试加速终止线程池
                addWorkerFailed(w);
            }
        }
        return workerStarted;
    }


    private class Worker extends AbstractQueuedSynchronizer implements Runnable {

        //用来干活的线程
        final Thread thread;
        //要执行的第一个任务，构造时就确定了的
        Runnable firstTask;
        //干活数量计数器，也就是这个线程完成了多少个任务
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            // 执行Task之前不让中断，将AQS的state设定为-1
            setState(-1);
            this.firstTask = firstTask;
            //通过预定义或是我们自定义的线程工厂创建线程
            this.thread = getThreadFactory().newThread(this);
        }

        @Override
        protected boolean isHeldExclusively() {
            //0就是没加锁，1就是已加锁
            return getState() != 0;
        }

        @Override
        public void run() {
            runWorker(this);
        }

        public void lock(){ acquire(1); }

        public void unlock() { release(1); }

        final void runWorker(Worker w) {
            // 获取当前线程
            Thread wt = Thread.currentThread();
            Runnable task = w.firstTask;
            w.firstTask = null;
            // 因为一开始为-1，这里是通过unlock操作将其修改回0，只有state大于等于0才能响应中断
            w.unlock();
            boolean completedAbruptly = true;
            try {
                // 只要任务不为null，或是任务为空但是可以从等待队列中取出任务不为空，
                // 那么就开始执行这个任务，注意这里是无限循环，也就是说如果当前没有任务了，
                // 那么会在getTask方法中卡住，因为要从阻塞队列中等着取任务
                while (task != null || (task = getTask()) != null) {
                    w.lock();
                    // 由于线程池在STOP状态及以上会禁止新线程加入并且中断正在进行的线程
                    // 只要线程池是STOP及以上的状态，那肯定是不能开始新任务的
                    // 线程是否已经被打上中断标记并且线程一定是STOP及以上
                    // 再次确保线程被没有打上中断标记
                    if ((runStateAtLeast(ctl.get(), STOP) || (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) && !wt.isInterrupted()){
                        //打中断标记
                        wt.interrupt();
                    }
                    try {
                        //开始之前的准备工作，这里暂时没有实现
                        beforeExecute(wt, task);
                        Throwable thrown = null;
                        try {
                            //OK，开始执行任务
                            task.run();
                        } catch (RuntimeException x) {
                            thrown = x;
                            throw x;
                        } catch (Error x) {
                            thrown = x;
                            throw x;
                        } catch (Throwable x) {
                            thrown = x;
                            throw new Error(x);
                        } finally {
                            afterExecute(task, thrown);
                        }
                    } finally {
                        task = null;    //任务已完成，不需要了
                        w.completedTasks++;   //任务完成数++
                        w.unlock();    //解锁
                    }
                }
                completedAbruptly = false;
            } finally {
                // 如果能走到这一步，那说明上面的循环肯定是跳出了，也就是说这个Worker可以丢弃了
                //所以这里会直接将 Worker 从 workers 里删除掉
                processWorkerExit(w, completedAbruptly);
            }
        }



    }

    private Runnable getTask() {
        boolean timedOut = false;
        //无限循环获取
        for (;;) {
            //获取ctl
            int c = ctl.get();
            //解析线程池运行状态
            int rs = runStateOf(c);
            // 线程池的状态
            // 判断是不是没有必要再执行等待队列中的任务了，也就是处于关闭线程池的状态了
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }
            //如果线程池运行正常，那就获取当前的工作线程数量
            int wc = workerCountOf(c);
            //如果线程数大于核心线程数或是允许核心线程等待超时，那么就标记为可超时的
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
            //超时或maximumPoolSize在运行期间被修改了，并且线程数大于1或等待队列为空，那也是不能获取到任务的
            if ((wc > maximumPoolSize || (timed && timedOut)) && (wc > 1 || workQueue.isEmpty())) {
                //如果CAS减少工作线程成功
                if (compareAndDecrementWorkerCount(c)){
                    return null;
                }
                continue;
            }
            try {
                //如果可超时，那么最多等到超时时间

                Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :  workQueue.take();
                //如果不可超时，那就一直等着拿任务
                if (r != null){
                    return r;
                }
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }
}

```

