package com.laoshiren.hello.spring.transaction.service.impl;

import com.laoshiren.hello.spring.transaction.mapper.TestMapper;
import com.laoshiren.hello.spring.transaction.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.spring.transaction.service.impl
 * ClassName:       TestServiceImpl
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/4/4 13:53
 * Version:         1.0.0
 */
@Component
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Transactional
    @Override
    public void saveTwiceThrowEx() {
        testMapper.insert(new HashMap());
        if (true) {
            throw new RuntimeException("ex");
        }
        testMapper.insert(new HashMap());
    }

    @Transactional
    @Override
    public void saveTwice() {
        testMapper.insert(new HashMap());
        testMapper.insert(new HashMap());
    }

    @Override
    public void saveTwiceNoTransaction() {
        testMapper.insert(new HashMap());
        if (true) {
            throw new RuntimeException("ex");
        }
        testMapper.insert(new HashMap());
    }
}
