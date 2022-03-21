package com.laoshiren.hello.juc.concurrent.lock.readwrite;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.lock.readwrite
 * @className: ReadWriteLock
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/21 16:38
 */
public class MainReadWriteLock {


    /*

    public interface ReadWriteLock {
        //获取读锁
        // 共享锁
        Lock readLock();

  	    //获取写锁
  	    // 排他锁
        Lock writeLock();
    }

    除了可重入锁之外，还有一种类型的锁叫做读写锁，当然它并不是专门用作读写操作的锁，
    它和可重入锁不同的地方在于，可重入锁是一种排他锁，
        当一个线程得到锁之后，另一个线程必须等待其释放锁，否则一律不允许获取到锁。
   而读写锁在同一时间，是可以让多个线程获取到锁的，它其实就是针对于读写场景而出现的。
     */
    public static void main(String[] args)throws InterruptedException   {
//        readRead();
//        writeRead();
//        reentrant();

//        lockDegrade();

        lockUp();

    }

    public static void lockUp() throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        lock.writeLock().lock();
        System.out.println("所升级成功！");
    }

    public static void lockDegrade() throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.writeLock().lock();
        lock.readLock().lock();
        new Thread(() -> {
            System.out.println("开始加读锁！");
            lock.readLock().lock();
            System.out.println("读锁添加成功！");
        }).start();
        TimeUnit.SECONDS.sleep(2);
        lock.writeLock().unlock();
    }


    public static void reentrant() throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.writeLock().lock();
        lock.writeLock().lock();
        new Thread(() -> {
            lock.writeLock().lock();
            System.out.println("成功获取到写锁！");
        }).start();
        System.out.println("释放第一层锁！");
        lock.writeLock().unlock();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("释放第二层锁！");
        lock.writeLock().unlock();
    }

    public static void readRead() throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        new Thread(lock.readLock()::lock).start();
    }

    public static void writeRead() throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.writeLock().lock();
        new Thread(lock.readLock()::lock).start();
    }

}
