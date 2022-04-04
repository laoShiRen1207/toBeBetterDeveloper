package com.laoshiren.hello.spring.transaction.service;

/**
 * ProjectName:     toBeBetterDeveloper
 * Package:         com.laoshiren.hello.spring.transaction.service
 * ClassName:       TestService
 * Author:          laoshiren
 * Git:             xiangdehua@pharmakeyring.com
 * Description:     TBD
 * Date:            2022/4/4 13:53
 * Version:         1.0.0
 */
public interface TestService {

    void saveTwiceThrowEx();

    void saveTwice();


    void saveTwiceNoTransaction();
}
