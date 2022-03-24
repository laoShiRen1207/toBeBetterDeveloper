package com.laoshiren.hello.juc.pool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.pool
 * @className: ThreadError
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/23 17:22
 */
public class ThreadError {


    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,   //最大容量和核心容量锁定为1
                0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        executor.execute(() -> {
            System.out.println(Thread.currentThread().getName());
            throw new RuntimeException("我是异常！");
        });
        TimeUnit.SECONDS.sleep(1);
        executor.execute(() -> {
            System.out.println(Thread.currentThread().getName());
        });
    }


}
