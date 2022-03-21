package com.laoshiren.hello.juc.rethread.vola;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.vola
 * @className: Main
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/21 14:26
 */
public class Main {
    private static int sync_a = 0;
    private static volatile int  volatile_a = 0;

    public static void main(String[] args) throws InterruptedException  {
        testSync();
        testVolatile();
    }

    public static void testVolatile() throws InterruptedException {
        new Thread(() -> {
            while (volatile_a== 0){

            }
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        volatile_a = 1;
    }


    public static void testSync() throws InterruptedException {
        new Thread(() -> {
            while (sync_a == 0) {
                synchronized (Main.class){
                }
            }
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        synchronized (Main.class){
            sync_a = 1;
        }
    }
}
