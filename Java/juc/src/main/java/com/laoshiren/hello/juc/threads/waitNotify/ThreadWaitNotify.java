package com.laoshiren.hello.juc.threads.waitNotify;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.juc.threads.waitNotify
 * ClassName:       ThreadWait
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 21:42
 * Version:         1.0.0
 */
public class ThreadWaitNotify {

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
                for (int i = 0; i < 5; i++) {
                    System.out.println(i);
                }
                //唤醒后依然需要等待这里的锁释放之前等待的线程才能继续
            }
        });
        t1.start();
        Thread.sleep(1000);
        t2.start();
    }

}
