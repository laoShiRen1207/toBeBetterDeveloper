package com.laoshiren.hello.juc.threads.daemon;


/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.juc.threads.daemon
 * ClassName:       ThreadDaemon
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 22:28
 * Version:         1.0.0
 */
public class ThreadDaemon {


    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            Thread it = new Thread(() -> {
                while (true) {
                    try {
                        System.out.println("程序正常运行中...");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            it.start();
        });
        //设置为守护线程（必须在开始之前，中途是不允许转换的）
        t.setDaemon(true);
        t.start();
        for (int i = 0; i < 6; i++) {
            Thread.sleep(1000);
        }
    }
}
