package com.yibo.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * @Author: huangyibo
 * @Date: 2021/8/26 23:38
 * @Description:
 */

@SpringBootApplication
@EnableFeignClients("com.yibo.order.client")
@EnableDiscoveryClient
@EnableResourceServer
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}
