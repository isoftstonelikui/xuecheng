package com.xuecheng.auth;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Auther: likui
 * @Date: 2019/7/13 11:01
 * @Description: bcypt加密测试
 */
public class BcyptTest {

    //bcypt加密测试
    @Test
    public void testPasswrodEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123";
        String hashWord = encoder.encode(password);
        System.out.println(hashWord);
        encoder.matches(password, hashWord);
    }

    @Test
    public void revortTest(){
        Object object="ab";
        boolean equals = "abc".equals(object);
        System.out.println(equals);
    }
}
