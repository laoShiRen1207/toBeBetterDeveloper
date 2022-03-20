package com.laoshiren.hello.juc.threads.base;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.juc.threads
 * ClassName:       ThreadJoin
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 0:46
 * Version:         1.0.0
 */
public class ThreadJoin {

    /**
     * 线程的加入只是等待另一个线程的完成，并不是将另一个线程和当前线程合并！
     */
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
                        //在i==10时，让线程1加入，先完成线程1的内容，在继续当前内容
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t1.start();
        t2.start();
    }

}
