package com.laoshiren.hello.juc.future;

import java.util.concurrent.*;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.future
 * @className: FutureMain
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/24 11:35
 */
public class FutureMain {


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //直接用Executors创建，方便就完事了
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //使用submit提交任务，会返回一个Future对象，注意提交的对象可以是Runable也可以是Callable，这里使用的是Callable能够自定义返回值

        Future<String> future = executor.submit(() -> {
            TimeUnit.SECONDS.sleep(1);
            return "我是字符串!";
        });
        //如果任务未完成，get会被阻塞，任务完成返回Callable执行结果返回值
        System.out.println(future.get());
        executor.shutdown();
    }
}

