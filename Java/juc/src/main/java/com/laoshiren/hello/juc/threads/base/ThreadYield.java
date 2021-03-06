package com.laoshiren.hello.juc.threads.base;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.juc.threads
 * ClassName:       ThreadYield
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 0:42
 * Version:         1.0.0
 */
public class ThreadYield {

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

}
