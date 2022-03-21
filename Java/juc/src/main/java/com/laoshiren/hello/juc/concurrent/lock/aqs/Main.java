package com.laoshiren.hello.juc.concurrent.lock.aqs;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.lock.aqs
 * @className: Main
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/21 17:47
 */
public class Main {

    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock(true);
        reentrantLock.lock();
        // 1 调用 公平锁的 lock 方法
        // 1.1 调用 aqs的 acquire()方法


        try {
            System.out.println(1);
        } finally {
            reentrantLock.unlock();
        }
    }

}
