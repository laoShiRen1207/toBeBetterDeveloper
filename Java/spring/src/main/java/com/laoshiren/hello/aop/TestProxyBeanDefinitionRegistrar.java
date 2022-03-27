package com.laoshiren.hello.aop;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.aop
 * ClassName:       TestProxyBeanDefinitionRegistrar
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/27 0:18
 * Version:         1.0.0
 */
public class TestProxyBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition(TestAOP.class).getBeanDefinition();
        registry.registerBeanDefinition("lbwnb", definition);
    }
}

