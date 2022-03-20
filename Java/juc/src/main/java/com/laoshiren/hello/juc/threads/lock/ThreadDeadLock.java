package com.laoshiren.hello.juc.threads.lock;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.juc.threads.lock
 * ClassName:       ThreadDeadLock
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 21:41
 * Version:         1.0.0
 */
public class ThreadDeadLock {

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

}
