package com.laoshiren.hello.juc.threads;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.juc.threads
 * ClassName:       CreateThread
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/19 23:47
 * Version:         1.0.0
 */
public class ThreadCreate {


    public static void main(String[] args) throws InterruptedException {
        stop();
    }

    /**
     * 仅仅创建线程并启动线程。
     */
    public static void createThread() {
        Thread thread = new Thread(() -> System.out.println("线程方法"));
        thread.start();
    }


    public static void createThreadLoop() {
        Thread thread = new Thread(() -> {
            System.out.println("线程方法" + Thread.currentThread().getName());
            int sum = 0;
            for (int i = 0; i < 1000; i++) {
                sum = sum + i;
            }
            System.out.println(sum);
        });
        thread.start();
        System.out.println("我是主线程" + Thread.currentThread().getName());
    }

    public static void createThreadSample() {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                System.out.println("线程方法1" + " " + i);
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                System.out.println("线程方法2" + " " + i);
            }
        });
        thread.start();
        thread2.start();
    }


    public static void sleep() throws InterruptedException{
        //休眠时间，以毫秒为单位，1000ms = 1s
        System.out.println("l");
        Thread.sleep(1000);
        System.out.println("b");
        Thread.sleep(1000);
        System.out.println("w");
        Thread.sleep(1000);
        System.out.println("nb!");
    }

    public static void stop(){
        Thread t = new Thread(() -> {
            //获取当前线程对象
            Thread me = Thread.currentThread();
            for (int i = 0; i < 50; i++) {
                System.out.println("打印:"+i);
                if(i == 20) {
                    //此方法会直接终止此线程
                    me.stop();
                }
            }
        });
        t.start();
    }

}
