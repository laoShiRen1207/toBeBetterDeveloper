package com.laoshiren.hello.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.spring
 * ClassName:       SpringMain
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/25 23:15
 * Version:         1.0.0
 */
public class SpringMain {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 由于ApplicationContext 本身要去实现BeanFactory 的方法所以我们在后去 context 就能获取到Bean
        // 所以BeanFactory 的实现类必须是一个IoC容器
        applicationContext.refresh();
        Object xxx = applicationContext.getBean("xxx");

    }

}
