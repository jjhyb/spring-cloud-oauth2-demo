package com.yibo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author: huangyibo
 * @Date: 2019/11/10 22:08
 * @Description:
 */

@SpringBootApplication
@EnableZuulProxy
public class GatewayServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(GatewayServerApplication.class,args);
    }

    @Bean
    public RestTemplate restTemplate(){

        return new RestTemplate();
    }
}
