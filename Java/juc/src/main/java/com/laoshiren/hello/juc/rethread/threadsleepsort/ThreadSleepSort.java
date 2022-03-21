package com.laoshiren.hello.juc.rethread.threadsleepsort;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.juc.concurrent.threadsleepsort
 * ClassName:       ThreadSleepSort
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/20 22:40
 * Version:         1.0.0
 */
public class ThreadSleepSort {

    public static void main(String[] args) {
        int[] arr = new int[]{3, 1, 5, 2, 4};

        for (int i : arr) {
            new Thread(() -> {
                try {
                    Thread.sleep(i * 1000);   //越小的数休眠时间越短，优先被打印
                    System.out.println(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
