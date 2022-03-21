package com.laoshiren.hello.juc.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.lock
 * @className: Main
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/21 15:05
 */
public class LockReentrantLock {

    /*
    public interface Lock {
  	    //获取锁，拿不到锁会阻塞，等待其他线程释放锁，获取到锁后返回
        void lock();
  	    //同上，但是等待过程中会响应中断
        void lockInterruptibly() throws InterruptedException;
  	    //尝试获取锁，但是不会阻塞，如果能获取到会返回true，不能返回false
        boolean tryLock();
  	    //尝试获取锁，但是可以限定超时时间，如果超出时间还没拿到锁返回false，否则返回true，可以响应中断
        boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
  	    //释放锁
        void unlock();
  	    //暂时可以理解为替代传统的Object的wait()、notify()等操作的工具
        Condition newCondition();
    }
     */

    private static int i = 0;
    public static void main(String[] args) throws InterruptedException {
        //可重入锁ReentrantLock类是Lock类的一个实现，我们后面会进行介绍
        Lock testLock = new ReentrantLock();
        Runnable action = () -> {
            for (int j = 0; j < 100000; j++) {
                //加锁，加锁成功后其他线程如果也要获取锁，会阻塞，等待当前线程释放
                testLock.lock();
                i++;
                //解锁，释放锁之后其他线程就可以获取这把锁了（注意在这之前一定得加锁，不然报错）
                testLock.unlock();
            }
        };
        new Thread(action).start();
        new Thread(action).start();
        //等上面两个线程跑完
        Thread.sleep(1000);
        System.out.println(i);
    }

}
