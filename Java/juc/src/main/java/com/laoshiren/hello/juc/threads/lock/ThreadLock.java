package com.laoshiren.hello.juc.threads.lock;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.juc.threads.lock
 * ClassName:       ThreadLock
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 21:41
 * Version:         1.0.0
 */
public class ThreadLock {

    private static int value = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                synchronized (ThreadLock.class){
                    value++;
                }
            }
            System.out.println("线程1完成");
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                synchronized (ThreadLock.class){
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

}
