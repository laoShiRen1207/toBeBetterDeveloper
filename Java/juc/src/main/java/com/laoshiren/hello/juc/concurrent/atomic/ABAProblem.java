package com.laoshiren.hello.juc.concurrent.atomic;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.atomic
 * @className: ABAProblem
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/23 13:36
 */
public class ABAProblem {

    public static void main(String[] args) {
        AtomicStampedReference<Integer> aba = new AtomicStampedReference<>(1,1);
        // 修改版本号
        System.out.println(aba.attemptStamp(1, 2));

        System.out.println("value "+aba.getReference() + ", version " + aba.getStamp());
        System.out.println(aba.compareAndSet(1,2,2,3));
        System.out.println("value "+aba.getReference() + ", version " + aba.getStamp());

    }
}
