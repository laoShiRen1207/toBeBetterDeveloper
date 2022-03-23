package com.laoshiren.hello.juc.concurrent.lock.aqs.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.lock.aqs.condition
 * @className: Main
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/23 11:21
 */
public class Main {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject conditionObject = null;
    }

}
