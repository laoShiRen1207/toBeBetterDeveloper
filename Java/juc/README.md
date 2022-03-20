# JUC

JUC向B站UP主[**青空の霞光**](https://space.bilibili.com/37737161)学习

[多线程课程-Java SE 教程 已完结 (IDEA 2021.2版本) 4K蓝光画质 入门到入土P86-P96](https://www.bilibili.com/video/BV1Gv411T7pi?p=87&spm_id_from=333.1007.top_right_bar_window_history.content.click)

[JUC课程-JUC 并发编程 已完结（IDEA 2021.3最新版）4K蓝光画质 玩转多线程](https://www.bilibili.com/video/BV1JT4y1S7K8?spm_id_from=333.999.0.0)

## 多线程


**注意：**本章节会涉及到 **操作系统** 相关知识。

在了解多线程之前，让我们回顾一下`操作系统`中提到的进程概念：

![img](https://img0.baidu.com/it/u=2613039280,4140201323&fm=26&fmt=auto)

进程是程序执行的实体，每一个进程都是一个应用程序（比如我们运行QQ、浏览器、LOL、网易云音乐等软件），都有自己的内存空间，CPU一个核心同时只能处理一件事情，当出现多个进程需要同时运行时，CPU一般通过`时间片轮转调度`算法，来实现多个进程的同时运行。

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhiphotos.baidu.com%2Fdoc%2Fpic%2Fitem%2Faec379310a55b3193e6caaf24aa98226cefc179b.jpg&refer=http%3A%2F%2Fhiphotos.baidu.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637499744&t=1df3c2095bc9a8cbe8cd9d0974644b7c)

在早期的计算机中，进程是拥有资源和独立运行的最小单位，也是程序执行的最小单位。但是，如果我希望两个任务同时进行，就必须运行两个进程，由于每个进程都有一个自己的内存空间，进程之间的通信就变得非常麻烦（比如要共享某些数据）而且执行不同进程会产生上下文切换，非常耗时，那么能否实现在一个进程中就能够执行多个任务呢？

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fs2.51cto.com%2Fwyfs02%2FM00%2F84%2F3A%2FwKiom1eIqY7il2J7AAAyvcssSjs721.gif&refer=http%3A%2F%2Fs2.51cto.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637474421&t=aef9a39ea3a09d6d67e8d4b769036446)

后来，线程横空出世，一个进程可以有多个线程，线程是程序执行中一个单一的顺序控制流程，现在线程才是程序执行流的最小单元，各个线程之间共享程序的内存空间（也就是所在进程的内存空间），上下文切换速度也高于进程。

在Java中，我们从开始，一直以来编写的都是单线程应用程序（运行`main()`方法的内容），也就是说只能同时执行一个任务（无论你是调用方法、还是进行计算，始终都是依次进行的，也就是同步的），而如果我们希望同时执行多个任务（两个方法**同时**在运行或者是两个计算同时在进行，也就是异步的），就需要用到Java多线程框架。实际上一个Java程序启动后，会创建很多线程，不仅仅只运行一个主线程：

```java
public static void main(String[] args) {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    long[] ids = bean.getAllThreadIds();
    ThreadInfo[] infos = bean.getThreadInfo(ids);
    for (ThreadInfo info : infos) {
        System.out.println(info.getThreadName());
    }
}
```

关于除了main线程默认以外的线程，涉及到JVM相关底层原理，在这里不做讲解，了解就行。

***

### 线程的创建和启动

通过创建Thread对象来创建一个新的线程，Thread构造方法中需要传入一个Runnable接口的实现（其实就是编写要在另一个线程执行的内容逻辑）同时Runnable只有一个未实现方法，因此可以直接使用lambda表达式：

```java
@FunctionalInterface
public interface Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    public abstract void run();
}
```

创建好后，通过调用`start()`方法来运行此线程：

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {    //直接编写逻辑
        System.out.println("我是另一个线程！");
    });
    t.start();   //调用此方法来开始执行此线程
}
```

可能上面的例子看起来和普通的单线程没两样，那我们先来看看下面这段代码的运行结果：

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {
        System.out.println("我是线程："+Thread.currentThread().getName());
        System.out.println("我正在计算 0-10000 之间所有数的和...");
        int sum = 0;
        for (int i = 0; i <= 10000; i++) {
            sum += i;
        }
        System.out.println("结果："+sum);
    });
    t.start();
    System.out.println("我是主线程！");
}
```

我们发现，这段代码执行输出结果并不是按照从上往下的顺序了，因为他们分别位于两个线程，他们是同时进行的！如果你还是觉得很疑惑，我们接着来看下面的代码运行结果：

```java
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 50; i++) {
            System.out.println("我是一号线程："+i);
        }
    });
    Thread t2 = new Thread(() -> {
        for (int i = 0; i < 50; i++) {
            System.out.println("我是二号线程："+i);
        }
    });
    t1.start();
    t2.start();
}
```

我们可以看到打印实际上是在交替进行的，也证明了他们是在同时运行！

**注意**：我们发现还有一个run方法，也能执行线程里面定义的内容，但是run是直接在当前线程执行，并不是创建一个线程执行！

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.liuhaihua.cn%2Fwp-content%2Fuploads%2F2019%2F09%2F3AfuQrV.png&refer=http%3A%2F%2Fwww.liuhaihua.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637477978&t=d986b270854b3d7c54f816f9103084bc)

实际上，线程和进程差不多，也会等待获取CPU资源，一旦获取到，就开始按顺序执行我们给定的程序，当需要等待外部IO操作（比如Scanner获取输入的文本），就会暂时处于休眠状态，等待通知，或是调用`sleep()`方法来让当前线程休眠一段时间：

```java
public static void main(String[] args) throws InterruptedException {
    System.out.println("l");
    Thread.sleep(1000);    //休眠时间，以毫秒为单位，1000ms = 1s
    System.out.println("b");
    Thread.sleep(1000);
    System.out.println("w");
    Thread.sleep(1000);
    System.out.println("nb!");
}
```

我们也可以使用`stop()`方法来强行终止此线程：

```java
public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(() -> {
        Thread me = Thread.currentThread();   //获取当前线程对象
        for (int i = 0; i < 50; i++) {
            System.out.println("打印:"+i);
            if(i == 20) me.stop();  //此方法会直接终止此线程
        }
    });
    t.start();
}
```

虽然`stop()`方法能够终止此线程，但是并不是所推荐的做法，有关线程中断相关问题，我们会在后面继续了解。

**思考**：猜猜以下程序输出结果：

```java
private static int value = 0;

public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) value++;
        System.out.println("线程1完成");
    });
    Thread t2 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) value++;
        System.out.println("线程2完成");
    });
    t1.start();
    t2.start();
    Thread.sleep(1000);  //主线程停止1秒，保证两个线程执行完成
    System.out.println(value);
}
```

我们发现，value最后的值并不是我们理想的结果，有关为什么会出现这种问题，在我们学习到线程锁的时候，再来探讨。

***

### 线程的休眠和中断

我们前面提到，一个线程处于运行状态下，线程的下一个状态会出现以下情况：

* 当CPU给予的运行时间结束时，会从运行状态回到就绪（可运行）状态，等待下一次获得CPU资源。
* 当线程进入休眠 / 阻塞(如等待IO请求) / 手动调用`wait()`方法时，会使得线程处于等待状态，当等待状态结束后会回到就绪状态。
* 当线程出现异常或错误 / 被`stop()` 方法强行停止 / 所有代码执行结束时，会使得线程的运行终止。

而这个部分我们着重了解一下线程的休眠和中断，首先我们来了解一下如何使得线程进如休眠状态：

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {
        try {
            System.out.println("l");
            Thread.sleep(1000);   //sleep方法是Thread的静态方法，它只作用于当前线程（它知道当前线程是哪个）
            System.out.println("b");    //调用sleep后，线程会直接进入到等待状态，直到时间结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    t.start();
}
```

通过调用`sleep()`方法来将当前线程进入休眠，使得线程处于等待状态一段时间。我们发现，此方法显示声明了会抛出一个InterruptedException异常，那么这个异常在什么时候会发生呢？

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {
        try {
            Thread.sleep(10000);  //休眠10秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    t.start();
    try {
        Thread.sleep(3000);   //休眠3秒，一定比线程t先醒来
        t.interrupt();   //调用t的interrupt方法
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

我们发现，每一个Thread对象中，都有一个`interrupt()`方法，调用此方法后，会给指定线程添加一个中断标记以告知线程需要立即停止运行或是进行其他操作，由线程来响应此中断并进行相应的处理，我们前面提到的`stop()`方法是强制终止线程，这样的做法虽然简单粗暴，但是很有可能导致资源不能完全释放，而类似这样的发送通知来告知线程需要中断，让线程自行处理后续，会更加合理一些，也是更加推荐的做法。我们来看看interrupt的用法：

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {
        System.out.println("线程开始运行！");
        while (true){   //无限循环
            if(Thread.currentThread().isInterrupted()){   //判断是否存在中断标志
                break;   //响应中断
            }
        }
        System.out.println("线程被中断了！");
    });
    t.start();
    try {
        Thread.sleep(3000);   //休眠3秒，一定比线程t先醒来
        t.interrupt();   //调用t的interrupt方法
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

通过`isInterrupted()`可以判断线程是否存在中断标志，如果存在，说明外部希望当前线程立即停止，也有可能是给当前线程发送一个其他的信号，如果我们并不是希望收到中断信号就是结束程序，而是通知程序做其他事情，我们可以在收到中断信号后，复位中断标记，然后继续做我们的事情：

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {
        System.out.println("线程开始运行！");
        while (true){
            if(Thread.currentThread().isInterrupted()){   //判断是否存在中断标志
                System.out.println("发现中断信号，复位，继续运行...");
                Thread.interrupted();  //复位中断标记（返回值是当前是否有中断标记，这里不用管）
            }
        }
    });
    t.start();
    try {
        Thread.sleep(3000);   //休眠3秒，一定比线程t先醒来
        t.interrupt();   //调用t的interrupt方法
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

复位中断标记后，会立即清除中断标记。那么，如果现在我们想暂停线程呢？我们希望线程暂时停下，比如等待其他线程执行完成后，再继续运行，那这样的操作怎么实现呢？

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {
        System.out.println("线程开始运行！");
        Thread.currentThread().suspend();   //暂停此线程
        System.out.println("线程继续运行！");
    });
    t.start();
    try {
        Thread.sleep(3000);   //休眠3秒，一定比线程t先醒来
        t.resume();   //恢复此线程
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

虽然这样很方便地控制了线程的暂停状态，但是这两个方法我们发现实际上也是不推荐的做法，它很容易导致死锁！有关为什么被弃用的原因，我们会在线程锁继续探讨。

***

### 线程的优先级

实际上，Java程序中的每个线程并不是平均分配CPU时间的，为了使得线程资源分配更加合理，Java采用的是抢占式调度方式，优先级越高的线程，优先使用CPU资源！我们希望CPU花费更多的时间去处理更重要的任务，而不太重要的任务，则可以先让出一部分资源。线程的优先级一般分为以下三种：

* MIN_PRIORITY   最低优先级
* MAX_PRIORITY   最高优先级
* NOM_PRIORITY  常规优先级

```java
public static void main(String[] args) {
    Thread t = new Thread(() -> {
        System.out.println("线程开始运行！");
    });
    t.start();
    t.setPriority(Thread.MIN_PRIORITY);  //通过使用setPriority方法来设定优先级
}
```

优先级越高的线程，获得CPU资源的概率会越大，并不是说一定优先级越高的线程越先执行！

##### 线程的礼让和加入

我们还可以在当前线程的工作不重要时，将CPU资源让位给其他线程，通过使用`yield()`方法来将当前资源让位给其他同优先级线程：

```java
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        System.out.println("线程1开始运行！");
        for (int i = 0; i < 50; i++) {
            if(i % 5 == 0) {
                System.out.println("让位！");
                Thread.yield();
            }
            System.out.println("1打印："+i);
        }
        System.out.println("线程1结束！");
    });
    Thread t2 = new Thread(() -> {
        System.out.println("线程2开始运行！");
        for (int i = 0; i < 50; i++) {
            System.out.println("2打印："+i);
        }
    });
    t1.start();
    t2.start();
}
```

观察结果，我们发现，在让位之后，尽可能多的在执行线程2的内容。

当我们希望一个线程等待另一个线程执行完成后再继续进行，我们可以使用`join()`方法来实现线程的加入：

```java
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        System.out.println("线程1开始运行！");
        for (int i = 0; i < 50; i++) {
            System.out.println("1打印："+i);
        }
        System.out.println("线程1结束！");
    });
    Thread t2 = new Thread(() -> {
        System.out.println("线程2开始运行！");
        for (int i = 0; i < 50; i++) {
            System.out.println("2打印："+i);
            if(i == 10){
                try {
                    System.out.println("线程1加入到此线程！");
                    t1.join();    //在i==10时，让线程1加入，先完成线程1的内容，在继续当前内容
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    t1.start();
    t2.start();
}
```

我们发现，线程1加入后，线程2等待线程1待执行的内容全部执行完成之后，再继续执行的线程2内容。注意，线程的加入只是等待另一个线程的完成，并不是将另一个线程和当前线程合并！我们来看看：

```java
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        System.out.println(Thread.currentThread().getName()+"开始运行！");
        for (int i = 0; i < 50; i++) {
            System.out.println(Thread.currentThread().getName()+"打印："+i);
        }
        System.out.println("线程1结束！");
    });
    Thread t2 = new Thread(() -> {
        System.out.println("线程2开始运行！");
        for (int i = 0; i < 50; i++) {
            System.out.println("2打印："+i);
            if(i == 10){
                try {
                    System.out.println("线程1加入到此线程！");
                    t1.join();    //在i==10时，让线程1加入，先完成线程1的内容，在继续当前内容
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    t1.start();
    t2.start();
}
```

实际上，t2线程只是暂时处于等待状态，当t1执行结束时，t2才开始继续执行，只是在效果上看起来好像是两个线程合并为一个线程在执行而已。

***

### 线程锁和线程同步

在开始讲解线程同步之前，我们需要先了解一下多线程情况下Java的内存管理：

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fvlambda.com%2Fimg%3Furl%3Dhttps%3A%2F%2Fmmbiz.qpic.cn%2Fmmbiz_png%2F2LlmEpiamhyq7hTfsoWa1GMIQlOtRuD8SScvIeB3KD7w4OoGu8wx13lBjMJLhYgYqTHND48X05m901TIEicGg49w%2F640%3Fwx_fmt%3Dpng&refer=http%3A%2F%2Fvlambda.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637562962&t=830ccc4dbe09f2699660bfcc9a292c63)

线程之间的共享变量（比如之前悬念中的value变量）存储在主内存（main memory）中，每个线程都有一个私有的工作内存（本地内存），工作内存中存储了该线程以读/写共享变量的副本。它类似于我们在`计算机组成原理`中学习的多处理器高速缓存机制：

![img](https://note.youdao.com/yws/api/personal/file/WEBb1fa2c9cd0784fb19f0d8ebeb8e00976?method=download&shareKey=8d48a5816e60b026adfa21e6735b5e31)

高速缓存通过保存内存中数据的副本来提供更加快速的数据访问，但是如果多个处理器的运算任务都涉及同一块内存区域，就可能导致各自的高速缓存数据不一致，在写回主内存时就会发生冲突，这就是引入高速缓存引发的新问题，称之为：缓存一致性。

实际上，Java的内存模型也是这样类似设计的，当我们同时去操作一个共享变量时，如果仅仅是读取还好，但是如果同时写入内容，就会出现问题！好比说一个银行，如果我和我的朋友同时在银行取我账户里面的钱，难道取1000还可能吐2000出来吗？我们需要一种更加安全的机制来维持秩序，保证数据的安全性！

#### 悬念破案

我们再来回顾一下之前留给大家的悬念：

```java
private static int value = 0;

public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) value++;
        System.out.println("线程1完成");
    });
    Thread t2 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) value++;
        System.out.println("线程2完成");
    });
    t1.start();
    t2.start();
    Thread.sleep(1000);  //主线程停止1秒，保证两个线程执行完成
    System.out.println(value);
}
```

实际上，当两个线程同时读取value的时候，可能会同时拿到同样的值，而进行自增操作之后，也是同样的值，再写回主内存后，本来应该进行2次自增操作，实际上只执行了一次！

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Faliyunzixunbucket.oss-cn-beijing.aliyuncs.com%2Fjpg%2F3154ff892af3cb3373a3b6b82b501a1d.jpg%3Fx-oss-process%3Dimage%2Fresize%2Cp_100%2Fauto-orient%2C1%2Fquality%2Cq_90%2Fformat%2Cjpg%2Fwatermark%2Cimage_eXVuY2VzaGk%3D%2Ct_100&refer=http%3A%2F%2Faliyunzixunbucket.oss-cn-beijing.aliyuncs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637565388&t=20091d33bae457edc36af7718ef1325b)

那么要去解决这样的问题，我们就必须采取某种同步机制，来限制不同线程对于共享变量的访问！我们希望的是保证共享变量value自增操作的原子性（原子性是指一个操作或多个操作要么全部执行，且执行的过程不会被任何因素打断，包括其他线程，要么就都不执行）

#### 线程锁

通过synchronized关键字来创造一个线程锁，首先我们来认识一下synchronized代码块，它需要在括号中填入一个内容，必须是一个对象或是一个类，我们在value自增操作外套上同步代码块：

```java
private static int value = 0;

public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) {
            synchronized (Main.class){
                value++;
            }
        }
        System.out.println("线程1完成");
    });
    Thread t2 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) {
            synchronized (Main.class){
                value++;
            }
        }
        System.out.println("线程2完成");
    });
    t1.start();
    t2.start();
    Thread.sleep(1000);  //主线程停止1秒，保证两个线程执行完成
    System.out.println(value);
}
```

我们发现，现在得到的结果就是我们想要的内容了，因为在同步代码块执行过程中，拿到了我们传入对象或类的锁（传入的如果是对象，就是对象锁，不同的对象代表不同的对象锁，如果是类，就是类锁，类锁只有一个，实际上类锁也是对象锁，是Class类实例，但是Class类实例同样的类无论怎么获取都是同一个），但是注意两个线程必须使用同一把锁！

当一个线程进入到同步代码块时，会获取到当前的锁，而这时如果其他使用同样的锁的同步代码块也想执行内容，就必须等待当前同步代码块的内容执行完毕，在执行完毕后会自动释放这把锁，而其他的线程才能拿到这把锁并开始执行同步代码块里面的内容。（实际上synchronized是一种悲观锁，随时都认为有其他线程在对数据进行修改，后面有机会我们还会讲到乐观锁，如CAS算法）

那么我们来看看，如果使用的是不同对象的锁，那么还能顺利进行吗？

```java
private static int value = 0;

public static void main(String[] args) throws InterruptedException {
    Main main1 = new Main();
    Main main2 = new Main();
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) {
            synchronized (main1){
                value++;
            }
        }
        System.out.println("线程1完成");
    });
    Thread t2 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) {
            synchronized (main2){
                value++;
            }
        }
        System.out.println("线程2完成");
    });
    t1.start();
    t2.start();
    Thread.sleep(1000);  //主线程停止1秒，保证两个线程执行完成
    System.out.println(value);
}
```

当对象不同时，获取到的是不同的锁，因此并不能保证自增操作的原子性，最后也得不到我们想要的结果。

synchronized关键字也可以作用于方法上，调用此方法时也会获取锁：

```java
private static int value = 0;

private static synchronized void add(){
    value++;
}

public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) add();
        System.out.println("线程1完成");
    });
    Thread t2 = new Thread(() -> {
        for (int i = 0; i < 10000; i++) add();
        System.out.println("线程2完成");
    });
    t1.start();
    t2.start();
    Thread.sleep(1000);  //主线程停止1秒，保证两个线程执行完成
    System.out.println(value);
}
```

我们发现实际上效果是相同的，只不过这个锁不用你去给，如果是静态方法，就是使用的类锁，而如果是普通成员方法，就是使用的对象锁。通过灵活的使用synchronized就能很好地解决我们之前提到的问题了！

#### 死锁

其实死锁的概念在`操作系统`中也有提及，它是指两个线程相互持有对方需要的锁，但是又迟迟不释放，导致程序卡住：

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic4.zhimg.com%2Fv2-9852c978350cc5e8641ba778619351bb_b.png&refer=http%3A%2F%2Fpic4.zhimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637568214&t=7740dd98b8e1c4a3bfbd94a30e7f9ff8)

我们发现，线程A和线程B都需要对方的锁，但是又被对方牢牢把握，由于线程被无限期地阻塞，因此程序不可能正常终止。我们来看看以下这段代码会得到什么结果：

```java
public static void main(String[] args) throws InterruptedException {
    Object o1 = new Object();
    Object o2 = new Object();
    Thread t1 = new Thread(() -> {
        synchronized (o1){
            try {
                Thread.sleep(1000);
                synchronized (o2){
                    System.out.println("线程1");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
    Thread t2 = new Thread(() -> {
        synchronized (o2){
            try {
                Thread.sleep(1000);
                synchronized (o1){
                    System.out.println("线程2");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
    t1.start();
    t2.start();
}
```

那么我们如何去检测死锁呢？我们可以利用jstack命令来检测死锁，首先利用jps找到我们的java进程：

```shell
nagocoler@NagodeMacBook-Pro ~ % jps
51592 Launcher
51690 Jps
14955 
51693 Main
nagocoler@NagodeMacBook-Pro ~ % jstack 51693
...
Java stack information for the threads listed above:
===================================================
"Thread-1":
	at com.test.Main.lambda$main$1(Main.java:46)
	- waiting to lock <0x000000076ad27fc0> (a java.lang.Object)
	- locked <0x000000076ad27fd0> (a java.lang.Object)
	at com.test.Main$$Lambda$2/1867750575.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)
"Thread-0":
	at com.test.Main.lambda$main$0(Main.java:34)
	- waiting to lock <0x000000076ad27fd0> (a java.lang.Object)
	- locked <0x000000076ad27fc0> (a java.lang.Object)
	at com.test.Main$$Lambda$1/396873410.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)

Found 1 deadlock.
```

jstack自动帮助我们找到了一个死锁，并打印出了相关线程的栈追踪信息。

不推荐使用 `suspend() `去挂起线程的原因，是因为` suspend() `在使线程暂停的同时，并不会去释放任何锁资源。其他线程都无法访问被它占用的锁。直到对应的线程执行` resume() `方法后，被挂起的线程才能继续，从而其它被阻塞在这个锁的线程才可以继续执行。但是，如果` resume() `操作出现在` suspend() `之前执行，那么线程将一直处于挂起状态，同时一直占用锁，这就产生了死锁。

#### wait和notify方法

其实我们之前可能就发现了，Object类还有三个方法我们从来没有使用过，分别是`wait()`、`notify()`以及`notifyAll()`，他们其实是需要配合synchronized来使用的，只有在同步代码块中才能使用这些方法，我们来看看他们的作用是什么：

```java
public static void main(String[] args) throws InterruptedException {
    Object o1 = new Object();
    Thread t1 = new Thread(() -> {
        synchronized (o1){
            try {
                System.out.println("开始等待");
                o1.wait();     //进入等待状态并释放锁
                System.out.println("等待结束！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
    Thread t2 = new Thread(() -> {
        synchronized (o1){
            System.out.println("开始唤醒！");
            o1.notify();     //唤醒处于等待状态的线程
          	for (int i = 0; i < 50; i++) {
               	System.out.println(i);   
            }
          	//唤醒后依然需要等待这里的锁释放之前等待的线程才能继续
        }
    });
    t1.start();
    Thread.sleep(1000);
    t2.start();
}
```

我们可以发现，对象的`wait()`方法会暂时使得此线程进入等待状态，**同时会释放当前代码块持有的锁**，这时其他线程可以获取到此对象的锁，当其他线程调用对象的`notify()`方法后，会唤醒刚才变成等待状态的线程（这时并没有立即释放锁）。**注意，必须是在持有锁（同步代码块内部）的情况下使用，否则会抛出异常！**

notifyAll其实和notify一样，也是用于唤醒，但是前者是唤醒所有调用`wait()`后处于等待的线程，而后者是看运气随机选择一个。

#### ThreadLocal的使用

既然每个线程都有一个自己的工作内存，那么能否只在自己的工作内存中创建变量仅供线程自己使用呢？

![img](https://img2018.cnblogs.com/blog/1368768/201906/1368768-20190613220434628-1803630402.png)

我们可以是ThreadLocal类，来创建工作内存中的变量，它将我们的变量值存储在内部（只能存储一个变量），不同的变量访问到ThreadLocal对象时，都只能获取到自己线程所属的变量。

```java
public static void main(String[] args) throws InterruptedException {
    ThreadLocal<String> local = new ThreadLocal<>();  //注意这是一个泛型类，存储类型为我们要存放的变量类型
    Thread t1 = new Thread(() -> {
        local.set("lbwnb");   //将变量的值给予ThreadLocal
        System.out.println("变量值已设定！");
        System.out.println(local.get());   //尝试获取ThreadLocal中存放的变量
    });
    Thread t2 = new Thread(() -> {
        System.out.println(local.get());   //尝试获取ThreadLocal中存放的变量
    });
    t1.start();
    Thread.sleep(3000);    //间隔三秒
    t2.start();
}
```

上面的例子中，我们开启两个线程分别去访问ThreadLocal对象，我们发现，第一个线程存放的内容，第一个线程可以获取，但是第二个线程无法获取，我们再来看看第一个线程存入后，第二个线程也存放，是否会覆盖第一个线程存放的内容：

```java
public static void main(String[] args) throws InterruptedException {
    ThreadLocal<String> local = new ThreadLocal<>();  //注意这是一个泛型类，存储类型为我们要存放的变量类型
    Thread t1 = new Thread(() -> {
        local.set("lbwnb");   //将变量的值给予ThreadLocal
        System.out.println("线程1变量值已设定！");
        try {
            Thread.sleep(2000);    //间隔2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程1读取变量值：");
        System.out.println(local.get());   //尝试获取ThreadLocal中存放的变量
    });
    Thread t2 = new Thread(() -> {
        local.set("yyds");   //将变量的值给予ThreadLocal
        System.out.println("线程2变量值已设定！");
    });
    t1.start();
    Thread.sleep(1000);    //间隔1秒
    t2.start();
}
```

我们发现，即使线程2重新设定了值，也没有影响到线程1存放的值，所以说，不同线程向ThreadLocal存放数据，只会存放在线程自己的工作空间中，而不会直接存放到主内存中，因此各个线程直接存放的内容互不干扰。

我们发现在线程中创建的子线程，无法获得父线程工作内存中的变量：

```java
public static void main(String[] args) {
    ThreadLocal<String> local = new ThreadLocal<>();
    Thread t = new Thread(() -> {
       local.set("lbwnb");
        new Thread(() -> {
            System.out.println(local.get());
        }).start();
    });
    t.start();
}
```

我们可以使用InheritableThreadLocal来解决：

```java
public static void main(String[] args) {
    ThreadLocal<String> local = new InheritableThreadLocal<>();
    Thread t = new Thread(() -> {
       local.set("lbwnb");
        new Thread(() -> {
            System.out.println(local.get());
        }).start();
    });
    t.start();
}
```

在InheritableThreadLocal存放的内容，会自动向子线程传递。

***

### 定时器

我们有时候会有这样的需求，我希望定时执行任务，比如3秒后执行，其实我们可以通过使用`Thread.sleep()`来实现：

```java
public static void main(String[] args) {
    new TimerTask(() -> System.out.println("我是定时任务！"), 3000).start();   //创建并启动此定时任务
}

static class TimerTask{
    Runnable task;
    long time;

    public TimerTask(Runnable runnable, long time){
        this.task = runnable;
        this.time = time;
    }

    public void start(){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                task.run();   //休眠后再运行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
```

我们通过自行封装一个TimerTask类，并在启动时，先休眠3秒钟，再执行我们传入的内容。那么现在我们希望，能否循环执行一个任务呢？比如我希望每隔1秒钟执行一次代码，这样该怎么做呢？

```java
public static void main(String[] args) {
    new TimerLoopTask(() -> System.out.println("我是定时任务！"), 3000).start();   //创建并启动此定时任务
}

static class TimerLoopTask{
    Runnable task;
    long loopTime;

    public TimerLoopTask(Runnable runnable, long loopTime){
        this.task = runnable;
        this.loopTime = loopTime;
    }

    public void start(){
        new Thread(() -> {
            try {
                while (true){   //无限循环执行
                    Thread.sleep(loopTime);
                    task.run();   //休眠后再运行
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
```

现在我们将单次执行放入到一个无限循环中，这样就能一直执行了，并且按照我们的间隔时间进行。

但是终究是我们自己实现，可能很多方面还没考虑到，Java也为我们提供了一套自己的框架用于处理定时任务：

```java
public static void main(String[] args) {
    Timer timer = new Timer();    //创建定时器对象
    timer.schedule(new TimerTask() {   //注意这个是一个抽象类，不是接口，无法使用lambda表达式简化，只能使用匿名内部类
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());    //打印当前线程名称
        }
    }, 1000);    //执行一个延时任务
}
```

我们可以通过创建一个Timer类来让它进行定时任务调度，我们可以通过此对象来创建任意类型的定时任务，包延时任务、循环定时任务等。我们发现，虽然任务执行完成了，但是我们的程序并没有停止，这是因为Timer内存维护了一个任务队列和一个工作线程：

```java
public class Timer {
    /**
     * The timer task queue.  This data structure is shared with the timer
     * thread.  The timer produces tasks, via its various schedule calls,
     * and the timer thread consumes, executing timer tasks as appropriate,
     * and removing them from the queue when they're obsolete.
     */
    private final TaskQueue queue = new TaskQueue();

    /**
     * The timer thread.
     */
    private final TimerThread thread = new TimerThread(queue);
  
		...
}
```

TimerThread继承自Thread，是一个新创建的线程，在构造时自动启动：

```java
public Timer(String name) {
    thread.setName(name);
    thread.start();
}
```

而它的run方法会循环地读取队列中是否还有任务，如果有任务依次执行，没有的话就暂时处于休眠状态：

```java
public void run() {
    try {
        mainLoop();
    } finally {
        // Someone killed this Thread, behave as if Timer cancelled
        synchronized(queue) {
            newTasksMayBeScheduled = false;
            queue.clear();  // Eliminate obsolete references
        }
    }
}

/**
 * The main timer loop.  (See class comment.)
 */
private void mainLoop() {
  try {
       TimerTask task;
       boolean taskFired;
       synchronized(queue) {
         	// Wait for queue to become non-empty
          while (queue.isEmpty() && newTasksMayBeScheduled)   //当队列为空同时没有被关闭时，会调用wait()方法暂时处于等待状态，当有新的任务时，会被唤醒。
                queue.wait();
          if (queue.isEmpty())
             break;    //当被唤醒后都没有任务时，就会结束循环，也就是结束工作线程
                      ...
}
```

`newTasksMayBeScheduled`实际上就是标记当前定时器是否关闭，当它为false时，表示已经不会再有新的任务到来，也就是关闭，我们可以通过调用`cancel()`方法来关闭它的工作线程：

```java
public void cancel() {
    synchronized(queue) {
        thread.newTasksMayBeScheduled = false;
        queue.clear();
        queue.notify();  //唤醒wait使得工作线程结束
    }
}
```

因此，我们可以在使用完成后，调用Timer的`cancel()`方法以正常退出我们的程序：

```java
public static void main(String[] args) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
            timer.cancel();  //结束
        }
    }, 1000);
}
```

***

### 守护线程

不要把守护进程和守护线程相提并论！守护进程在后台运行运行，不需要和用户交互，本质和普通进程类似。而守护线程就不一样了，当其他所有的非守护线程结束之后，守护线程是自动结束，也就是说，Java中所有的线程都执行完毕后，守护线程自动结束，因此守护线程不适合进行IO操作，只适合打打杂：

```java
public static void main(String[] args) throws InterruptedException{
    Thread t = new Thread(() -> {
        while (true){
            try {
                System.out.println("程序正常运行中...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
    t.setDaemon(true);   //设置为守护线程（必须在开始之前，中途是不允许转换的）
    t.start();
    for (int i = 0; i < 5; i++) {
        Thread.sleep(1000);
    }
}
```

在守护线程中产生的新线程也是守护的：

```java
public static void main(String[] args) throws InterruptedException{
    Thread t = new Thread(() -> {
        Thread it = new Thread(() -> {
            while (true){
                try {
                    System.out.println("程序正常运行中...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        it.start();
    });
    t.setDaemon(true);   //设置为守护线程（必须在开始之前，中途是不允许转换的）
    t.start();
    for (int i = 0; i < 5; i++) {
        Thread.sleep(1000);
    }
}
```

***

### 再谈集合类并行方法

其实我们之前在讲解集合类的根接口时，就发现有这样一个方法：

```java
default Stream<E> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
}
```

并行流，其实就是一个多线程执行的流，它通过默认的ForkJoinPool实现（这里不讲解原理），它可以提高你的多线程任务的速度。

```java
public static void main(String[] args) {
    List<Integer> list = new ArrayList<>(Arrays.asList(1, 4, 5, 2, 9, 3, 6, 0));
    list
            .parallelStream()    //获得并行流
            .forEach(i -> System.out.println(Thread.currentThread().getName()+" -> "+i));
}
```

我们发现，forEach操作的顺序，并不是我们实际List中的顺序，同时每次打印也是不同的线程在执行！我们可以通过调用`forEachOrdered()`方法来使用单线程维持原本的顺序：

```java
public static void main(String[] args) {
    List<Integer> list = new ArrayList<>(Arrays.asList(1, 4, 5, 2, 9, 3, 6, 0));
    list
            .parallelStream()    //获得并行流
            .forEachOrdered(System.out::println);
}
```

我们之前还发现，在Arrays数组工具类中，也包含大量的并行方法：

```java
public static void main(String[] args) {
    int[] arr = new int[]{1, 4, 5, 2, 9, 3, 6, 0};
    Arrays.parallelSort(arr);   //使用多线程进行并行排序，效率更高
    System.out.println(Arrays.toString(arr));
}
```

更多地使用并行方法，可以更加充分地发挥现代计算机多核心的优势，但是同时需要注意多线程产生的异步问题！

```java
public static void main(String[] args) {
    int[] arr = new int[]{1, 4, 5, 2, 9, 3, 6, 0};
    Arrays.parallelSetAll(arr, i -> {
        System.out.println(Thread.currentThread().getName());
        return arr[i];
    });
    System.out.println(Arrays.toString(arr));
}
```

通过对Java多线程的了解，我们就具备了利用多线程解决问题的思维！

***

### Java多线程编程实战

这是整个教程最后一个编程实战内容了，下一章节为`反射`一般开发者使用比较少，属于选学内容，不编排编程实战课程。

#### 生产者与消费者

所谓的生产者消费者模型，是通过一个容器来解决生产者和消费者的强耦合问题。通俗的讲，就是生产者在不断的生产，消费者也在不断的消费，可是消费者消费的产品是生产者生产的，这就必然存在一个中间容器，我们可以把这个容器想象成是一个货架，当货架空的时候，生产者要生产产品，此时消费者在等待生产者往货架上生产产品，而当货架有货物的时候，消费者可以从货架上拿走商品，生产者此时等待货架出现空位，进而补货，这样不断的循环。

通过多线程编程，来模拟一个餐厅的2个厨师和3个顾客，假设厨师炒出一个菜的时间为3秒，顾客吃掉菜品的时间为4秒。



## 再谈多线程

> JUC相对于Java应用层的学习难度更大，**开篇推荐掌握的预备知识：**JavaSE多线程部分**（必备）**、操作系统、JVM**（推荐）**、计算机组成原理。掌握预备知识会让你的学习更加轻松，其中，JavaSE多线程部分要求必须掌握，否则无法继续学习本教程！我们不会再去重复教学JavaSE阶段的任何知识了。
>
> 各位小伙伴一定要点击收藏按钮（收藏 = 学会）

还记得我们在JavaSE中学习的多线程吗？让我们来回顾一下：

在我们的操作系统之上，可以同时运行很多个进程，并且每个进程之间相互隔离互不干扰。我们的CPU会通过时间片轮转算法，为每一个进程分配时间片，并在时间片使用结束后切换下一个进程继续执行，通过这种方式来实现宏观上的多个程序同时运行。

由于每个进程都有一个自己的内存空间，进程之间的通信就变得非常麻烦（比如要共享某些数据）而且执行不同进程会产生上下文切换，非常耗时，那么有没有一种更好地方案呢？

后来，线程横空出世，一个进程可以有多个线程，线程是程序执行中一个单一的顺序控制流程，现在线程才是程序执行流的最小单元，各个线程之间共享程序的内存空间（也就是所在进程的内存空间），上下文切换速度也高于进程。

现在有这样一个问题：

```java
public static void main(String[] args) {
    int[] arr = new int[]{3, 1, 5, 2, 4};
    //请将上面的数组按升序输出
}
```

按照正常思维，我们肯定是这样：

```java
public static void main(String[] args) {
    int[] arr = new int[]{3, 1, 5, 2, 4};
		//直接排序吧
    Arrays.sort(arr);
    for (int i : arr) {
        System.out.println(i);
    }
}
```

而我们学习了多线程之后，可以换个思路来实现：

```java
public static void main(String[] args) {
    int[] arr = new int[]{3, 1, 5, 2, 4};

    for (int i : arr) {
        new Thread(() -> {
            try {
                Thread.sleep(i * 1000);   //越小的数休眠时间越短，优先被打印
                System.out.println(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
```

我们接触过的很多框架都在使用多线程，比如Tomcat服务器，所有用户的请求都是通过不同的线程来进行处理的，这样我们的网站才可以同时响应多个用户的请求，要是没有多线程，可想而知服务器的处理效率会有多低。

虽然多线程能够为我们解决很多问题，但是，如何才能正确地使用多线程，如何才能将多线程的资源合理使用，这都是我们需要关心的问题。

在Java 5的时候，新增了java.util.concurrent（JUC）包，其中包括大量用于多线程编程的工具类，目的是为了更好的支持高并发任务，让开发者进行多线程编程时减少竞争条件和死锁的问题！通过使用这些工具类，我们的程序会更加合理地使用多线程。而我们这一系列视频的主角，正是`JUC`。

但是我们先不着急去看这些内容，第一章，我们先来补点基础知识。

***

### 并发与并行

我们经常听到并发编程，那么这个并发代表的是什么意思呢？而与之相似的并行又是什么意思？它们之间有什么区别？

比如现在一共有三个工作需要我们去完成。

![image-20220301213510841](https://tva1.sinaimg.cn/large/e6c9d24ely1gzupjszpjnj21bk06ujrw.jpg)

#### 顺序执行

顺序执行其实很好理解，就是我们依次去将这些任务完成了：

![image-20220301213629649](https://tva1.sinaimg.cn/large/e6c9d24ely1gzupl4sldlj219s06et98.jpg)

实际上就是我们同一时间只能处理一个任务，所以需要前一个任务完成之后，才能继续下一个任务，依次完成所有任务。

#### 并发执行

并发执行也是我们同一时间只能处理一个任务，但是我们可以每个任务轮着做（时间片轮转）：

![image-20220301214032719](https://tva1.sinaimg.cn/large/e6c9d24ely1gzuppchmldj21lm078myf.jpg)

只要我们单次处理分配的时间足够的短，在宏观看来，就是三个任务在同时进行。

而我们Java中的线程，正是这种机制，当我们需要同时处理上百个上千个任务时，很明显CPU的数量是不可能赶得上我们的线程数的，所以说这时就要求我们的程序有良好的并发性能，来应对同一时间大量的任务处理。学习Java并发编程，能够让我们在以后的实际场景中，知道该如何应对高并发的情况。

#### 并行执行

并行执行就突破了同一时间只能处理一个任务的限制，我们同一时间可以做多个任务：

![image-20220301214238743](https://tva1.sinaimg.cn/large/e6c9d24ely1gzuprj83gqj21hw0hqmz2.jpg)

比如我们要进行一些排序操作，就可以用到并行计算，只需要等待所有子任务完成，最后将结果汇总即可。包括分布式计算模型MapReduce，也是采用的并行计算思路。

***

### 再谈锁机制

谈到锁机制，相信各位应该并不陌生了，我们在JavaSE阶段，通过使用`synchronized`关键字来实现锁，这样就能够很好地解决线程之间争抢资源的情况。那么，`synchronized`底层到底是如何实现的呢？

我们知道，使用`synchronized`，一定是和某个对象相关联的，比如我们要对某一段代码加锁，那么我们就需要提供一个对象来作为锁本身：

```java
public static void main(String[] args) {
    synchronized (Main.class) {
        //这里使用的是Main类的Class对象作为锁
    }
}
```

我们来看看，它变成字节码之后会用到哪些指令：

![image-20220302111724784](https://tva1.sinaimg.cn/large/e6c9d24ely1gzvdbajqhfj229a0u0te0.jpg)

其中最关键的就是`monitorenter`指令了，可以看到之后也有`monitorexit`与之进行匹配（注意这里有2个），`monitorenter`和`monitorexit`分别对应加锁和释放锁，在执行`monitorenter`之前需要尝试获取锁，每个对象都有一个`monitor`监视器与之对应，而这里正是去获取对象监视器的所有权，一旦`monitor`所有权被某个线程持有，那么其他线程将无法获得（管程模型的一种实现）。

在代码执行完成之后，我们可以看到，一共有两个`monitorexit`在等着我们，那么为什么这里会有两个呢，按理说`monitorenter`和`monitorexit`不应该一一对应吗，这里为什么要释放锁两次呢？

首先我们来看第一个，这里在释放锁之后，会马上进入到一个goto指令，跳转到15行，而我们的15行对应的指令就是方法的返回指令，其实正常情况下只会执行第一个`monitorexit`释放锁，在释放锁之后就接着同步代码块后面的内容继续向下执行了。而第二个，其实是用来处理异常的，可以看到，它的位置是在12行，如果程序运行发生异常，那么就会执行第二个`monitorexit`，并且会继续向下通过`athrow`指令抛出异常，而不是直接跳转到15行正常运行下去。

![image-20220302114613847](https://tva1.sinaimg.cn/large/e6c9d24ely1gzve59lrkqj21wq0ca76u.jpg)

实际上`synchronized`使用的锁就是存储在Java对象头中的，我们知道，对象是存放在堆内存中的，而每个对象内部，都有一部分空间用于存储对象头信息，而对象头信息中，则包含了Mark Word用于存放`hashCode`和对象的锁信息，在不同状态下，它存储的数据结构有一些不同。

![image-20220302203846868](https://tva1.sinaimg.cn/large/e6c9d24ely1gzvtjfgg91j21e00howh1.jpg)

#### 重量级锁

在JDK6之前，`synchronized`一直被称为重量级锁，`monitor`依赖于底层操作系统的Lock实现，Java的线程是映射到操作系统的原生线程上，切换成本较高。而在JDK6之后，锁的实现得到了改进。我们先从最原始的重量级锁开始：

我们说了，每个对象都有一个monitor与之关联，在Java虚拟机（HotSpot）中，monitor是由ObjectMonitor实现的：

```c++
ObjectMonitor() {
    _header       = NULL;
    _count        = 0; //记录个数
    _waiters      = 0,
    _recursions   = 0;
    _object       = NULL;
    _owner        = NULL;
    _WaitSet      = NULL; //处于wait状态的线程，会被加入到_WaitSet
    _WaitSetLock  = 0 ;
    _Responsible  = NULL ;
    _succ         = NULL ;
    _cxq          = NULL ;
    FreeNext      = NULL ;
    _EntryList    = NULL ; //处于等待锁block状态的线程，会被加入到该列表
    _SpinFreq     = 0 ;
    _SpinClock    = 0 ;
    OwnerIsThread = 0 ;
}
```

每个等待锁的线程都会被封装成ObjectWaiter对象，进入到如下机制：

![img](https://tva1.sinaimg.cn/large/e6c9d24ely1gzvej55r7tj20dw08vjrt.jpg)

ObjectWaiter首先会进入 Entry Set等着，当线程获取到对象的`monitor`后进入 The Owner 区域并把`monitor`中的`owner`变量设置为当前线程，同时`monitor`中的计数器`count`加1，若线程调用`wait()`方法，将释放当前持有的`monitor`，`owner`变量恢复为`null`，`count`自减1，同时该线程进入 WaitSet集合中等待被唤醒。若当前线程执行完毕也将释放`monitor`并复位变量的值，以便其他线程进入获取对象的`monitor`。

虽然这样的设计思路非常合理，但是在大多数应用上，每一个线程占用同步代码块的时间并不是很长，我们完全没有必要将竞争中的线程挂起然后又唤醒，并且现代CPU基本都是多核心运行的，我们可以采用一种新的思路来实现锁。

在JDK1.4.2时，引入了自旋锁（JDK6之后默认开启），它不会将处于等待状态的线程挂起，而是通过无限循环的方式，不断检测是否能够获取锁，由于单个线程占用锁的时间非常短，所以说循环次数不会太多，可能很快就能够拿到锁并运行，这就是自旋锁。当然，仅仅是在等待时间非常短的情况下，自旋锁的表现会很好，但是如果等待时间太长，由于循环是需要处理器继续运算的，所以这样只会浪费处理器资源，因此自旋锁的等待时间是有限制的，默认情况下为10次，如果失败，那么会进而采用重量级锁机制。

![image-20220302163246988](https://tva1.sinaimg.cn/large/e6c9d24ely1gzvmffuq1hj21dm0ae75f.jpg)

在JDK6之后，自旋锁得到了一次优化，自旋的次数限制不再是固定的，而是自适应变化的，比如在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行，那么这次自旋也是有可能成功的，所以会允许自旋更多次。当然，如果某个锁经常都自旋失败，那么有可能会不再采用自旋策略，而是直接使用重量级锁。

#### 轻量级锁

> 从JDK 1.6开始，为了减少获得锁和释放锁带来的性能消耗，就引入了轻量级锁。

轻量级锁的目标是，在无竞争情况下，减少重量级锁产生的性能消耗（并不是为了代替重量级锁，实际上就是赌一手同一时间只有一个线程在占用资源），包括系统调用引起的内核态与用户态切换、线程阻塞造成的线程切换等。它不像是重量级锁那样，需要向操作系统申请互斥量。它的运作机制如下：

在即将开始执行同步代码块中的内容时，会首先检查对象的Mark Word，查看锁对象是否被其他线程占用，如果没有任何线程占用，那么会在当前线程中所处的栈帧中建立一个名为锁记录（Lock Record）的空间，用于复制并存储对象目前的Mark Word信息（官方称为Displaced Mark Word）。

接着，虚拟机将使用CAS操作将对象的Mark Word更新为轻量级锁状态（数据结构变为指向Lock Record的指针，指向的是当前的栈帧）

> CAS（Compare And Swap）是一种无锁算法（我们之前在Springboot阶段已经讲解过了），它并不会为对象加锁，而是在执行的时候，看看当前数据的值是不是我们预期的那样，如果是，那就正常进行替换，如果不是，那么就替换失败。比如有两个线程都需要修改变量`i`的值，默认为10，现在一个线程要将其修改为20，另一个要修改为30，如果他们都使用CAS算法，那么并不会加锁访问`i`，而是直接尝试修改`i`的值，但是在修改时，需要确认`i`是不是10，如果是，表示其他线程还没对其进行修改，如果不是，那么说明其他线程已经将其修改，此时不能完成修改任务，修改失败。
>
> 在CPU中，CAS操作使用的是`cmpxchg`指令，能够从最底层硬件层面得到效率的提升。

如果CAS操作失败了的话，那么说明可能这时有线程已经进入这个同步代码块了，这时虚拟机会再次检查对象的Mark Word，是否指向当前线程的栈帧，如果是，说明不是其他线程，而是当前线程已经有了这个对象的锁，直接放心大胆进同步代码块即可。如果不是，那确实是被其他线程占用了。

这时，轻量级锁一开始的想法就是错的（这时有对象在竞争资源，已经赌输了），所以说只能将锁膨胀为重量级锁，按照重量级锁的操作执行（注意锁的膨胀是不可逆的）

![image-20220302210830272](https://tva1.sinaimg.cn/large/e6c9d24ely1gzvuebbr7ej21b20ba763.jpg)

所以，轻量级锁 -> 失败 -> 自适应自旋锁 -> 失败 -> 重量级锁

解锁过程同样采用CAS算法，如果对象的MarkWord仍然指向线程的锁记录，那么就用CAS操作把对象的MarkWord和复制到栈帧中的Displaced Mark Word进行交换。如果替换失败，说明其他线程尝试过获取该锁，在释放锁的同时，需要唤醒被挂起的线程。

#### 偏向锁

偏向锁相比轻量级锁更纯粹，干脆就把整个同步都消除掉，不需要再进行CAS操作了。它的出现主要是得益于人们发现某些情况下某个锁频繁地被同一个线程获取，这种情况下，我们可以对轻量级锁进一步优化。

偏向锁实际上就是专门为单个线程而生的，当某个线程第一次获得锁时，如果接下来都没有其他线程获取此锁，那么持有锁的线程将不再需要进行同步操作。

可以从之前的MarkWord结构中看到，偏向锁也会通过CAS操作记录线程的ID，如果一直都是同一个线程获取此锁，那么完全没有必要在进行额外的CAS操作。当然，如果有其他线程来抢了，那么偏向锁会根据当前状态，决定是否要恢复到未锁定或是膨胀为轻量级锁。

如果我们需要使用偏向锁，可以添加`-XX:+UseBiased`参数来开启。

所以，最终的锁等级为：未锁定 < 偏向锁 < 轻量级锁 < 重量级锁

值得注意的是，如果对象通过调用`hashCode()`方法计算过对象的一致性哈希值，那么它是不支持偏向锁的，会直接进入到轻量级锁状态，因为Hash是需要被保存的，而偏向锁的Mark Word数据结构，无法保存Hash值；如果对象已经是偏向锁状态，再去调用`hashCode()`方法，那么会直接将锁升级为重量级锁，并将哈希值存放在`monitor`（有预留位置保存）中。

![image-20220302214647735](https://tva1.sinaimg.cn/large/e6c9d24ely1gzvvi5l9jhj21cy0bwjtl.jpg)

#### 锁消除和锁粗化

锁消除和锁粗化都是在运行时的一些优化方案，比如我们某段代码虽然加了锁，但是在运行时根本不可能出现各个线程之间资源争夺的情况，这种情况下，完全不需要任何加锁机制，所以锁会被消除。锁粗化则是我们代码中频繁地出现互斥同步操作，比如在一个循环内部加锁，这样明显是非常消耗性能的，所以虚拟机一旦检测到这种操作，会将整个同步范围进行扩展。

***

### JMM内存模型

注意这里提到的内存模型和我们在JVM中介绍的内存模型不在同一个层次，JVM中的内存模型是虚拟机规范对整个内存区域的规划，而Java内存模型，是在JVM内存模型之上的抽象模型，具体实现依然是基于JVM内存模型实现的，我们会在后面介绍。

#### Java内存模型

我们在`计算机组成原理`中学习过，在我们的CPU中，一般都会有高速缓存，而它的出现，是为了解决内存的速度跟不上处理器的处理速度的问题，所以CPU内部会添加一级或多级高速缓存来提高处理器的数据获取效率，但是这样也会导致一个很明显的问题，因为现在基本都是多核心处理器，每个处理器都有一个自己的高速缓存，那么又该怎么去保证每个处理器的高速缓存内容一致呢？

![image-20220303113148313](https://tva1.sinaimg.cn/large/e6c9d24ely1gzwjckl9pfj20x60cqdgt.jpg)

为了解决缓存一致性的问题，需要各个处理器访问缓存时都遵循一些协议，在读写时要根据协议来进行操作，这类协议有MSI、MESI（Illinois Protocol）、MOSI、Synapse、Firefly及Dragon Protocol等。

而Java也采用了类似的模型来实现支持多线程的内存模型：

![image-20220303114228749](https://tva1.sinaimg.cn/large/e6c9d24ely1gzwjnodcejj20xs0ewaba.jpg)

JMM（Java Memory Model）内存模型规定如下：

* 所有的变量全部存储在主内存（注意这里包括下面提到的变量，指的都是会出现竞争的变量，包括成员变量、静态变量等，而局部变量这种属于线程私有，不包括在内）
* 每条线程有着自己的工作内存（可以类比CPU的高速缓存）线程对变量的所有操作，必须在工作内存中进行，不能直接操作主内存中的数据。
* 不同线程之间的工作内存相互隔离，如果需要在线程之间传递内容，只能通过主内存完成，无法直接访问对方的工作内存。

也就是说，每一条线程如果要操作主内存中的数据，那么得先拷贝到自己的工作内存中，并对工作内存中数据的副本进行操作，操作完成之后，也需要从工作副本中将结果拷贝回主内存中，具体的操作就是`Save`（保存）和`Load`（加载）操作。

那么各位肯定会好奇，这个内存模型，结合之前JVM所讲的内容，具体是怎么实现的呢？

* 主内存：对应堆中存放对象的实例的部分。
* 工作内存：对应线程的虚拟机栈的部分区域，虚拟机可能会对这部分内存进行优化，将其放在CPU的寄存器或是高速缓存中。比如在访问数组时，由于数组是一段连续的内存空间，所以可以将一部分连续空间放入到CPU高速缓存中，那么之后如果我们顺序读取这个数组，那么大概率会直接缓存命中。

前面我们提到，在CPU中可能会遇到缓存不一致的问题，而Java中，也会遇到，比如下面这种情况：

```java
public class Main {
    private static int i = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            for (int j = 0; j < 100000; j++) i++;
            System.out.println("线程1结束");
        }).start();
        new Thread(() -> {
            for (int j = 0; j < 100000; j++) i++;
            System.out.println("线程2结束");
        }).start();
        //等上面两个线程结束
        Thread.sleep(1000);
        System.out.println(i);
    }
}
```

可以看到这里是两个线程同时对变量`i`各自进行100000次自增操作，但是实际得到的结果并不是我们所期望的那样。

那么为什么会这样呢？在之前学习了JVM之后，相信各位应该已经知道，自增操作实际上并不是由一条指令完成的（注意一定不要理解为一行代码就是一个指令完成的）：

![image-20220303143131899](https://tva1.sinaimg.cn/large/e6c9d24ely1gzwojklg4fj224y0oktfi.jpg)

包括变量`i`的获取、修改、保存，都是被拆分为一个一个的操作完成的，那么这个时候就有可能出现在修改完保存之前，另一条线程也保存了，但是当前线程是毫不知情的。

![image-20220303144344450](https://tva1.sinaimg.cn/large/e6c9d24ely1gzwow9xzb6j21kg0ayq54.jpg)

所以说，我们当时在JavaSE阶段讲解这个问题的时候，是通过`synchronized`关键字添加同步代码块解决的，当然，我们后面还会讲解另外的解决方案（原子类）

#### 重排序

在编译或执行时，为了优化程序的执行效率，编译器或处理器常常会对指令进行重排序，有以下情况：

1. 编译器重排序：Java编译器通过对Java代码语义的理解，根据优化规则对代码指令进行重排序。
2. 机器指令级别的重排序：现代处理器很高级，能够自主判断和变更机器指令的执行顺序。

指令重排序能够在不改变结果（单线程）的情况下，优化程序的运行效率，比如：

```java
public static void main(String[] args) {
    int a = 10;
    int b = 20;
    System.out.println(a + b);
}
```

我们其实可以交换第一行和第二行：

```java
public static void main(String[] args) {
    int b = 10;
    int a = 20;
    System.out.println(a + b);
}
```

即使发生交换，但是我们程序最后的运行结果是不会变的，当然这里只通过代码的形式演示，实际上JVM在执行字节码指令时也会进行优化，可能两个指令并不会按照原有的顺序进行。

虽然单线程下指令重排确实可以起到一定程度的优化作用，但是在多线程下，似乎会导致一些问题：

```java
public class Main {
    private static int a = 0;
    private static int b = 0;
    public static void main(String[] args) {
        new Thread(() -> {
            if(b == 1) {
                if(a == 0) {
                    System.out.println("A");
                }else {
                    System.out.println("B");
                }   
            }
        }).start();
        new Thread(() -> {
            a = 1;
            b = 1;
        }).start();
    }
}
```

上面这段代码，在正常情况下，按照我们的正常思维，是不可能输出`A`的，因为只要b等于1，那么a肯定也是1才对，因为a是在b之前完成的赋值。但是，如果进行了重排序，那么就有可能，a和b的赋值发生交换，b先被赋值为1，而恰巧这个时候，线程1开始判定b是不是1了，这时a还没来得及被赋值为1，可能线程1就已经走到打印那里去了，所以，是有可能输出`A`的。

#### volatile关键字

好久好久都没有认识新的关键字了，今天我们来看一个新的关键字`volatile`，开始之前我们先介绍三个词语：

* 原子性：其实之前讲过很多次了，就是要做什么事情要么做完，要么就不做，不存在做一半的情况。
* 可见性：指当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值。
* 有序性：即程序执行的顺序按照代码的先后顺序执行。

我们之前说了，如果多线程访问同一个变量，那么这个变量会被线程拷贝到自己的工作内存中进行操作，而不是直接对主内存中的变量本体进行操作，下面这个操作看起来是一个有限循环，但是是无限的：

```java
public class Main {
    private static int a = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (a == 0);
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        a = 1;   //很明显，按照我们的逻辑来说，a的值被修改那么另一个线程将不再循环
    }
}
```

实际上这就是我们之前说的，虽然我们主线程中修改了a的值，但是另一个线程并不知道a的值发生了改变，所以循环中依然是使用旧值在进行判断，因此，普通变量是不具有可见性的。

要解决这种问题，我们第一个想到的肯定是加锁，同一时间只能有一个线程使用，这样总行了吧，确实，这样的话肯定是可以解决问题的：

```java
public class Main {
    private static int a = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (a == 0) {
                synchronized (Main.class){}
            }
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        synchronized (Main.class){
            a = 1;
        }
    }
}
```

但是，除了硬加一把锁的方案，我们也可以使用`volatile`关键字来解决，此关键字的第一个作用，就是保证变量的可见性。当写一个`volatile`变量时，JMM会把该线程本地内存中的变量强制刷新到主内存中去，并且这个写会操作会导致其他线程中的`volatile`变量缓存无效，这样，另一个线程修改了这个变时，当前线程会立即得知，并将工作内存中的变量更新为最新的版本。

那么我们就来试试看：

```java
public class Main {
    //添加volatile关键字
    private static volatile int a = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (a == 0);
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        a = 1;
    }
}
```

结果还真的如我们所说的那样，当a发生改变时，循环立即结束。

当然，虽然说`volatile`能够保证可见性，但是不能保证原子性，要解决我们上面的`i++`的问题，以我们目前所学的知识，还是只能使用加锁来完成：

```java
public class Main {
    private static volatile int a = 0;
    public static void main(String[] args) throws InterruptedException {
        Runnable r = () -> {
            for (int i = 0; i < 10000; i++) a++;
            System.out.println("任务完成！");
        };
        new Thread(r).start();
        new Thread(r).start();

        //等待线程执行完成
        Thread.sleep(1000);
        System.out.println(a);
    }
}
```

不对啊，`volatile`不是能在改变变量的时候其他线程可见吗，那为什么还是不能保证原子性呢？还是那句话，自增操作是被瓜分为了多个步骤完成的，虽然保证了可见性，但是只要手速够快，依然会出现两个线程同时写同一个值的问题（比如线程1刚刚将a的值更新为100，这时线程2可能也已经执行到更新a的值这条指令了，已经刹不住车了，所以依然会将a的值再更新为一次100）

那要是真的遇到这种情况，那么我们不可能都去写个锁吧？后面，我们会介绍原子类来专门解决这种问题。

最后一个功能就是`volatile`会禁止指令重排，也就是说，如果我们操作的是一个`volatile`变量，它将不会出现重排序的情况，也就解决了我们最上面的问题。那么它是怎么解决的重排序问题呢？若用volatile修饰共享变量，在编译时，会在指令序列中插入`内存屏障`来禁止特定类型的处理器重排序

>  内存屏障（Memory Barrier）又称内存栅栏，是一个CPU指令，它的作用有两个：
>
>  1. 保证特定操作的顺序
>  2. 保证某些变量的内存可见性（volatile的内存可见性，其实就是依靠这个实现的）
>
>  由于编译器和处理器都能执行指令重排的优化，如果在指令间插入一条Memory Barrier则会告诉编译器和CPU，不管什么指令都不能和这条Memory Barrier指令重排序。
>
>  ![image-20220303172519404](https://tva1.sinaimg.cn/large/e6c9d24ely1gzwtkeydk7j2194068jsd.jpg)
>
>  | 屏障类型   | 指令示例                 | 说明                                                         |
>  | ---------- | ------------------------ | ------------------------------------------------------------ |
>  | LoadLoad   | Load1;LoadLoad;Load2     | 保证Load1的读取操作在Load2及后续读取操作之前执行             |
>  | StoreStore | Store1;StoreStore;Store2 | 在Store2及其后的写操作执行前，保证Store1的写操作已刷新到主内存 |
>  | LoadStore  | Load1;LoadStore;Store2   | 在Store2及其后的写操作执行前，保证Load1的读操作已读取结束    |
>  | StoreLoad  | Store1;StoreLoad;Load2   | 保证load1的写操作已刷新到主内存之后，load2及其后的读操作才能执行 |

所以`volatile`能够保证，之前的指令一定全部执行，之后的指令一定都没有执行，并且前面语句的结果对后面的语句可见。

最后我们来总结一下`volatile`关键字的三个特性：

* 保证可见性
* 不保证原子性
* 防止指令重排

在之后我们的设计模式系列视频中，还会讲解单例模式下`volatile`的运用。

#### happens-before原则

经过我们前面的讲解，相信各位已经了解了JMM内存模型以及重排序等机制带来的优点和缺点，综上，JMM提出了`happens-before`（先行发生）原则，定义一些禁止编译优化的场景，来向各位程序员做一些保证，只要我们是按照原则进行编程，那么就能够保持并发编程的正确性。具体如下：

* **程序次序规则：**同一个线程中，按照程序的顺序，前面的操作happens-before后续的任何操作。
  * 同一个线程内，代码的执行结果是有序的。其实就是，可能会发生指令重排，但是保证代码的执行结果一定是和按照顺序执行得到的一致，程序前面对某一个变量的修改一定对后续操作可见的，不可能会出现前面才把a修改为1，接着读a居然是修改前的结果，这也是程序运行最基本的要求。
* **监视器锁规则：**对一个锁的解锁操作，happens-before后续对这个锁的加锁操作。
  * 就是无论是在单线程环境还是多线程环境，对于同一个锁来说，一个线程对这个锁解锁之后，另一个线程获取了这个锁都能看到前一个线程的操作结果。比如前一个线程将变量`x`的值修改为了`12`并解锁，之后另一个线程拿到了这把锁，对之前线程的操作是可见的，可以得到`x`是前一个线程修改后的结果`12`（所以synchronized是有happens-before规则的）
* **volatile变量规则：**对一个volatile变量的写操作happens-before后续对这个变量的读操作。
  * 就是如果一个线程先去写一个`volatile`变量，紧接着另一个线程去读这个变量，那么这个写操作的结果一定对读的这个变量的线程可见。
* **线程启动规则：**主线程A启动线程B，线程B中可以看到主线程启动B之前的操作。
  * 在主线程A执行过程中，启动子线程B，那么线程A在启动子线程B之前对共享变量的修改结果对线程B可见。
* **线程加入规则：**如果线程A执行操作`join()`线程B并成功返回，那么线程B中的任意操作happens-before线程A`join()`操作成功返回。
* **传递性规则：**如果A happens-before B，B happens-before C，那么A happens-before C。

那么我们来从happens-before原则的角度，来解释一下下面的程序结果：

```java
public class Main {
    private static int a = 0;
  	private static int b = 0;
    public static void main(String[] args) {
        a = 10;
        b = a + 1;
        new Thread(() -> {
          if(b > 10) System.out.println(a); 
        }).start();
    }
}
```

首先我们定义以上出现的操作：

* **A：**将变量`a`的值修改为`10`
* **B：**将变量`b`的值修改为`a + 1`
* **C：**主线程启动了一个新的线程，并在新的线程中获取`b`，进行判断，如果为`true`那么就打印`a`

首先我们来分析，由于是同一个线程，并且**B**是一个赋值操作且读取了**A**，那么按照**程序次序规则**，A happens-before B，接着在B之后，马上执行了C，按照**线程启动规则**，在新的线程启动之前，当前线程之前的所有操作对新的线程是可见的，所以 B happens-before C，最后根据**传递性规则**，由于A happens-before B，B happens-before C，所以A happens-before C，因此在新的线程中会输出`a`修改后的结果`10`。