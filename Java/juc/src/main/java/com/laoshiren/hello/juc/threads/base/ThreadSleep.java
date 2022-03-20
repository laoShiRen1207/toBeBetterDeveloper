package com.laoshiren.hello.juc.threads.base;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.juc.threads
 * ClassName:       ThreadSleep
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 0:27
 * Version:         1.0.0
 */
public class ThreadSleep {

    public static void main(String[] args) {
        interrupted();
    }


    public static void sleep() {
        Thread t = new Thread(() -> {
            try {
                System.out.println("l");
                //sleep方法是Thread的静态方法，它只作用于当前线程（它知道当前线程是哪个）
                Thread.sleep(1000);
                //调用sleep后，线程会直接进入到等待状态，直到时间结束
                System.out.println("b");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public static void interruptEx() {
        Thread t = new Thread(() -> {
            try {
                //休眠10秒
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            //休眠3秒，一定比线程t先醒来
            Thread.sleep(3000);
            //调用t的interrupt方法
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void interrupt() {
        Thread t = new Thread(() -> {
            System.out.println("线程开始运行！");
            //无限循环
            while (true) {
                //判断是否存在中断标志
                if (Thread.currentThread().isInterrupted()) {
                    //响应中断
                    break;
                }
            }
            System.out.println("线程被中断了！");
        });
        t.start();
        try {
            //休眠3秒，一定比线程t先醒来
            Thread.sleep(3000);
            //调用t的interrupt方法
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void interrupted(){
        Thread t = new Thread(() -> {
            System.out.println("线程开始运行！");
            while (true){
                //判断是否存在中断标志
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("发现中断信号，复位，继续运行...");
                    //复位中断标记（返回值是当前是否有中断标记，这里不用管）
                    Thread.interrupted();
                }
            }
        });
        t.start();
        try {
            Thread.sleep(3000);
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
