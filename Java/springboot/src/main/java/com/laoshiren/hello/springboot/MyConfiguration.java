package com.laoshiren.hello.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.springboot
 * ClassName:       A
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/31 0:42
 * Version:         1.0.0
 */
@Configuration
public class MyConfiguration {

    @Bean
    public Tom tom(){
        return new Tom();
    }

}
