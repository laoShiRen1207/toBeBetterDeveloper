package com.laoshiren.hello.aop;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.aop
 * ClassName:       Test
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/27 0:19
 * Version:         1.0.0
 */
//@Component
public class TestAOP {

    @PostConstruct
    void init(){
        System.out.println("初始化了");
    }

}
