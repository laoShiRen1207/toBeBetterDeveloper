package com.laoshiren.hello.juc.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.pool
 * @className: ThreadPool
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/23 16:07
 */
public class ThreadPool {


    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = baseThreadPool();

        for (int i = 0; i < 10; i++) {   //开始6个任务
            int finalI = i;
            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName()+" 开始执行！（"+ finalI);
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName()+" 已结束！（"+finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        for (int i = 0; i <10 ; i++) {
            TimeUnit.SECONDS.sleep(1);    //看看当前线程池中的线程数量
            System.out.println("线程池中线程数量："+executor.getPoolSize());
        }

        executor.shutdownNow();    //使用完线程池记得关闭，不然程序不会结束，它会取消所有等待中的任务以及试图中断正在执行的任务，关闭后，无法再提交任务，一律拒绝
        //executor.shutdown();     同样可以关闭，但是会执行完等待队列中的任务再关闭


    }

    public static ThreadPoolExecutor baseThreadPool(){
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(2, 4,   //2个核心线程，最大线程数为4个
                        3, TimeUnit.SECONDS,        //最大空闲时间为3秒钟
                        new ArrayBlockingQueue<>(2));     //这里使用容量为2的ArrayBlockingQueue队列
        return executor;
    }

    /**
     * ThreadPoolExecutor.XXXPolicy()
     *
     * - AbortPolicy(默认)：像上面一样，直接抛异常。
     * - CallerRunsPolicy：直接让提交任务的线程运行这个任务，比如在主线程向线程池提交了任务，那么就直接由主线程执行。
     * - DiscardOldestPolicy：丢弃队列中最近的一个任务，替换为当前任务。
     * - DiscardPolicy：什么也不用做。
     * @return
     */
    public static ThreadPoolExecutor discardPolicyThreadPool(){
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(2, 4,
                        3, TimeUnit.SECONDS,
                        new SynchronousQueue<>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }


}