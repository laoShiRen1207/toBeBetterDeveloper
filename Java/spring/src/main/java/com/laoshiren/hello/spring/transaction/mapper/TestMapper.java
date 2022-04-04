package com.laoshiren.hello.spring.transaction.mapper;

import org.apache.ibatis.annotations.Insert;

import java.util.Map;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.mybatis
 * ClassName:       TestMapper
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/3/27 23:18
 * Version:         1.0.0
 */
public interface TestMapper {

    @Insert("insert into test(name) values ('a')")
    void insert(Map map);

}
