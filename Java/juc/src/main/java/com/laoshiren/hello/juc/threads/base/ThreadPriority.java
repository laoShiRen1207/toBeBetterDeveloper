package com.laoshiren.hello.juc.threads.base;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.juc.threads
 * ClassName:       ThreadPriority
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 0:43
 * Version:         1.0.0
 */
public class ThreadPriority {

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("线程开始运行！");
        });
        t.start();
        //通过使用setPriority方法来设定优先级
        t.setPriority(Thread.MIN_PRIORITY);
    }

}
