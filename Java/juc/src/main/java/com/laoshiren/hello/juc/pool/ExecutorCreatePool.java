package com.laoshiren.hello.juc.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.pool
 * @className: ExecutorCreatePool
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/24 11:19
 */
@Deprecated
public class ExecutorCreatePool {

    /**
     * 线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方式，这样
     * 的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
    }
}
