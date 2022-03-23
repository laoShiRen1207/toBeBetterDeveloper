package com.laoshiren.hello.juc.concurrent.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.atomic
 * @className: AtomicIntegerMain
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/23 13:07
 */
public class AtomicIntegerMain {

    /*
        其实本质上就是封装了一个`volatile`类型的int值，这样能够保证可见性
        在CAS操作的时候不会出现问题
    */
    static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Runnable r = ()->{
            for (int i = 0; i <100000 ; i++) {
                atomicInteger.incrementAndGet();
            }
        };

        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();

        TimeUnit.SECONDS.sleep(1);

        System.out.println(atomicInteger.get());
    }

}
