package com.yibo.gateway.filter;

/*import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

*//**
 * @author: huangyibo
 * @Date: 2022/5/24 16:46
 * @Description:
 *//*

//@Component
public class OAuthSignatureFilter implements GlobalFilter, Ordered {


    *//**受权访问用户名*//*
    @Value("${spring.security.user.name}")
    private String securityUserName;

    *//**受权访问密码*//*
    @Value("${spring.security.user.password}")
    private String securityUserPassword;

    *//**
     * OAuth过滤器
     * @param exchange
     * @param chain
     * @return
     *//*
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        *//**oauth受权*//*
        String auth= securityUserName.concat(":").concat(securityUserPassword);
        String encodedAuth = new sun.misc.BASE64Encoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        //注意Basic后面有空格
        String authHeader= "Basic " +encodedAuth;

        //向headers中放受权信息
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, authHeader).build();
        //将如今的request变成change对象
        ServerWebExchange build =exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(build);
    }


    *//**
     * 优先级
     * 数字越大优先级越低
     * @return
     *//*
    @Override
    public int getOrder() {
        return 2;
    }
}*/
