package com.yibo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @Author: huangyibo
 * @Date: 2021/8/18 22:46
 * @Description:
 */

@SpringBootApplication
@EnableFeignClients("com.yibo.auth.client")
public class AuthCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthCenterApplication.class,args);
    }
}
