package com.laoshiren.hello.juc.util;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.util
 * @className: CountDownLatchMain
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/24 15:06
 */
public class CountDownLatchMain {

    public static void main(String[] args) throws Exception {
        //创建一个初始值为20的计数器锁
        CountDownLatch latch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    double sleep = 2000 * new Random().nextDouble();
                    Thread.sleep((long) sleep);
                    System.out.println("子任务"+ finalI +"执行完成！ sleep  " + sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //每执行一次计数器都会-1
                latch.countDown();
            }).start();
        }

        //这个操作可以同时被多个线程执行，一起等待
        latch.await();
        //开始等待所有的线程完成，当计数器为0时，恢复运行
        System.out.println("所有子任务都完成！任务完成！！！");

        //注意这个计数器只能使用一次，用完只能重新创一个，没有重置的说法
    }
}
