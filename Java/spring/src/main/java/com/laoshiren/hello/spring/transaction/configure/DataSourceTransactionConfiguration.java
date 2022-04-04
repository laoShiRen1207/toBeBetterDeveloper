package com.laoshiren.hello.spring.transaction.configure;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.spring.transaction.configure
 * ClassName:       DataSourceTransactionConfiguration
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/4/4 13:42
 * Version:         1.0.0
 */
@EnableTransactionManagement
@MapperScan("com.laoshiren.hello.spring.transaction.mapper")
@EnableAspectJAutoProxy
@ComponentScan("com.laoshiren.hello.spring.transaction")
@Configuration
public class DataSourceTransactionConfiguration {

    @Bean
    public TransactionManager transactionManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

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
