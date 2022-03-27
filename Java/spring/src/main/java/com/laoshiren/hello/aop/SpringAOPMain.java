package com.laoshiren.hello.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.aop
 * ClassName:       SpringAOPMain
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/27 0:17
 * Version:         1.0.0
 */

public class SpringAOPMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
//        ConfigurableApplicationContext
        TestAOP bean2 = context.getBean("lbwnb",TestAOP.class);
        System.out.println(bean2);
    }


    @ComponentScan("com.laoshiren.hello.aop")
    @Configuration
    @EnableAspectJAutoProxy
    @Import(TestProxyBeanDefinitionRegistrar.class)
    public static class MainConfiguration {
    }

    @Component
    public static class TestBeanProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            System.out.println(beanName);  //打印bean的名称
            return bean;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
        }
    }

}
