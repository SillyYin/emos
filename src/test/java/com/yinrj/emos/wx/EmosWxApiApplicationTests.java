package com.yinrj.emos.wx;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmosWxApiApplicationTests {

    @Autowired
    private StringEncryptor encryptor;

    @Test
    void contextLoads() {
        String password = "yrj506489";
        System.out.println(encryptor.encrypt(password));
        String appId = "wxb37d86f1e89e0b20";
        String appSecret = "f50e467f99d72150519ef2a3ca4c5508";
        System.out.println("appId: " + encryptor.encrypt(appId));
        System.out.println("appSecret: " + encryptor.encrypt(appSecret));
    }

}
