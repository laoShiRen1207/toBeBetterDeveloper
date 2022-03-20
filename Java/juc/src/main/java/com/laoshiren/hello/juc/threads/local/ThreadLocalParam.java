package com.laoshiren.hello.juc.threads.local;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.juc.threads.local
 * ClassName:       ThreadLocalParam
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 22:11
 * Version:         1.0.0
 */
public class ThreadLocalParam {


    public static void main(String[] args) throws InterruptedException {
        parentThread();
    }

    /**
     * 获得父线程工作内存中的变量
     */
    public static void parentThread() {
        ThreadLocal<String> local = new InheritableThreadLocal<>();
        Thread t = new Thread(() -> {
            local.set("lbwnb");
            new Thread(() -> {
                System.out.println(local.get());
            }).start();
        });
        t.start();
    }

    public static void diffThread() throws InterruptedException {
        //注意这是一个泛型类，存储类型为我们要存放的变量类型
        ThreadLocal<String> local = new ThreadLocal<>();
        Thread t1 = new Thread(() -> {
            //将变量的值给予ThreadLocal
            local.set("lbwnb");
            System.out.println("变量值已设定！");
            //尝试获取ThreadLocal中存放的变量
            System.out.println(local.get());
        });
        Thread t2 = new Thread(() -> {
            //尝试获取ThreadLocal中存放的变量
            System.out.println(local.get());
        });
        t1.start();
        //间隔三秒
        Thread.sleep(3000);
        t2.start();
    }


}
