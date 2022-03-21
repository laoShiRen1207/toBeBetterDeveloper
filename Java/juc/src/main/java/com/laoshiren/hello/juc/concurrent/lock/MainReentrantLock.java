package com.laoshiren.hello.juc.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.lock
 * @className: MainReentrantLock
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/21 15:28
 */
public class MainReentrantLock {

    public static void main(String[] args) throws InterruptedException {
        // lock()
        // holdCount();
        // testFair(true);
        testFair(true);
    }

    public static void testFair(boolean fair) {
        ReentrantLock lock = new ReentrantLock(fair);

        Runnable action = () -> {
            System.out.println("线程 " + Thread.currentThread().getName() + " 开始获取锁...");
            lock.lock();
            System.out.println("线程 " + Thread.currentThread().getName() + " 成功获取锁！");
            lock.unlock();
        };
        for (int i = 0; i < 5; i++) {   //建立10个线程
            new Thread(action, "T" + i).start();
        }
    }

    public static void holdCount() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        lock.lock();
        System.out.println("当前加锁次数：" + lock.getHoldCount() + "，是否被锁：" + lock.isLocked());
        TimeUnit.SECONDS.sleep(1);
        lock.unlock();
        System.out.println("当前加锁次数：" + lock.getHoldCount() + "，是否被锁：" + lock.isLocked());
        TimeUnit.SECONDS.sleep(1);
        lock.unlock();
        System.out.println("当前加锁次数：" + lock.getHoldCount() + "，是否被锁：" + lock.isLocked());
    }

    public static void lock() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        //连续加锁2次
        lock.lock();
        new Thread(() -> {
            System.out.println("线程2想要获取锁");
            lock.lock();
            System.out.println("线程2成功获取到锁");
        }).start();
        lock.unlock();
        System.out.println("线程1释放了一次锁");
        TimeUnit.SECONDS.sleep(1);
        lock.unlock();
        //释放两次后其他线程才能加锁
        System.out.println("线程1再次释放了一次锁");
    }

}
