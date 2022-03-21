package com.laoshiren.hello.juc.rethread.resort;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.resort
 * @className: Main
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/21 14:14
 */
public class Main {
    private static int a = 0;
    private static int b = 0;

    public static void main(String[] args) {
        new Thread(() -> {
            if (b == 1) {
                if (a == 0) {
                    System.out.print("A");
                } else {
                    System.out.print("B");
                }
            }
        }).start();
        new Thread(() -> {
            b = 1;
            a = 1;
        }).start();
    }
}
