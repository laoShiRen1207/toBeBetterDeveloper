package com.laoshiren.hello.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

/**
 * ProjectName:     juc
 * Package:         com.laoshiren.hello.mybatis
 * ClassName:       SpringMyBatisMain
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/27 0:13
 * Version:         1.0.0
 */
public class SpringMyBatisMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfiguration.class);
        TestMapper bean = context.getBean(TestMapper.class);
        System.out.println(bean);
    }

    @ComponentScan("com.laoshiren.hello.mybatis")
    @Configuration
    @MapperScan({"com.laoshiren.hello.mybatis"})
    public static class MainConfiguration {
        // 配置 sqlSessionFactory

        @Bean
        public DataSource dataSource(){
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:mysql://172.31.2.27/laoshiren");
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUsername("root");
            dataSource.setPassword("root");
            return dataSource;
        }

        @Bean
        public SqlSessionFactoryBean sqlSessionFactoryBean(@Autowired DataSource dataSource){
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            return bean;
        }

    }
}
