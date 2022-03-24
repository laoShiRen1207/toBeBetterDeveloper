package com.laoshiren.hello.juc.concurrent.container;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.container
 * @className: BlockQueueMain
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/23 15:52
 */
public class BlockQueueMain {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Object> queue = new ArrayBlockingQueue<>(1);
        Runnable supplier = () -> {
            while (true) {
                try {
                    String name = Thread.currentThread().getName();
                    System.err.println(time() + "生产者 " + name + " 正在准备餐品...");
                    TimeUnit.SECONDS.sleep(3);
                    System.err.println(time() + "生产者 " + name + " 已出餐！");
                    queue.put(new Object());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        };
        Runnable consumer = () -> {
            while (true) {
                try {
                    String name = Thread.currentThread().getName();
                    System.out.println(time() + "消费者 " + name + " 正在等待出餐...");
                    queue.take();
                    System.out.println(time() + "消费者 " + name + " 取到了餐品。");
                    TimeUnit.SECONDS.sleep(4);
                    System.out.println(time() + "消费者 " + name + " 已经将饭菜吃完了！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        };
        for (int i = 0; i < 2; i++) {
            new Thread(supplier, "Supplier-" + i).start();
        }
        for (int i = 0; i < 3; i++) {
            new Thread(consumer, "Consumer-" + i).start();
        }
    }

    private static String time() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return "[" + format.format(new Date()) + "] ";
    }
}

