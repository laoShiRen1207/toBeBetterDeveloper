package com.laoshiren.hello.juc.concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.lock
 * @className: LockCondition
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/21 15:13
 */
public class LockCondition {


    /*

    public interface Condition {
  	    //与调用锁对象的wait方法一样，会进入到等待状态，但是这里需要调用Condition的signal或signalAll方法进行唤醒（感觉就是和普通对象的wait和notify是对应的）同时，等待状态下是可以响应中断的
 	    void await() throws InterruptedException;
  	    //同上，但不响应中断（看名字都能猜到）
  	    void awaitUninterruptibly();
  	    //等待指定时间，如果在指定时间（纳秒）内被唤醒，会返回剩余时间，如果超时，会返回0或负数，可以响应中断
  	    long awaitNanos(long nanosTimeout) throws InterruptedException;
  	    //等待指定时间（可以指定时间单位），如果等待时间内被唤醒，返回true，否则返回false，可以响应中断
  	    boolean await(long time, TimeUnit unit) throws InterruptedException;
  	    //可以指定一个明确的时间点，如果在时间点之前被唤醒，返回true，否则返回false，可以响应中断
  	    boolean awaitUntil(Date deadline) throws InterruptedException;
  	    //唤醒一个处于等待状态的线程，注意还得获得锁才能接着运行
  	    void signal();
  	    //同上，但是是唤醒所有等待线程
  	    void signalAll();
    }

     */

    public static void main(String[] args) throws InterruptedException {
        Lock testLock = new ReentrantLock();
        Condition condition = testLock.newCondition();
        new Thread(() -> {
            //和synchronized一样，必须持有锁的情况下才能使用await
            testLock.lock();
            System.out.println("线程1进入等待状态！");
            try {
                //进入等待状态
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程1等待结束！");
            testLock.unlock();
        }).start();
        //防止线程2先跑
        Thread.sleep(1000);
        new Thread(() -> {
            testLock.lock();
            System.out.println("线程2开始 唤醒其他等待线程");
            //唤醒线程1，但是此时线程1还必须要拿到锁才能继续运行
            condition.signal();
            System.out.println("线程2结束");
            //这里释放锁之后，线程1就可以拿到锁继续运行了
            testLock.unlock();
        }).start();
    }
}
