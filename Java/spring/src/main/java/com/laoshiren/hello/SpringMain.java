package com.laoshiren.hello;

import com.laoshiren.hello.spring.transaction.configure.DataSourceTransactionConfiguration;
import com.laoshiren.hello.spring.transaction.service.TestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello
 * ClassName:       SpringMain
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/4/4 13:47
 * Version:         1.0.0
 */
public class SpringMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(DataSourceTransactionConfiguration.class);
        // 由于ApplicationContext 本身要去实现BeanFactory 的方法所以我们在后去 context 就能获取到Bean
        // 所以BeanFactory 的实现类必须是一个IoC容器

        TestService bean = applicationContext.getBean(TestService.class);
        bean.saveTwiceNoTransaction();

    }
}
