package com.yibo.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/11/15 0:54
 * @Description: 401 认证失败 认证失败的处理器，用于tokan校验失败返回信息
 *
 * 1、令牌有问题，直接返回401，不经过此处理器
 *
 * 2、请求没有令牌，用户为anonymousUser匿名用户，网关通过了，但是调用到了后端需要身份认证的微服务，返回了401
 *
 * 3、请求进来在授权的地方没有通过，进入了错误处理里面发现没有登录为匿名用户，所以抛出401
 */

@Component
@Slf4j
public class GatewayAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>();
        //如果用户未携带token令牌访问服务
        if(authException instanceof AccessTokenRequiredException){
            //模拟向数据库更新401日志，请求进来的时候插入过日志
            log.info("update log to  execution stateCode=401");
            //这里设置是为了GatewayAuditLogFilter里面去获取，如果这里记录了日志，那么GatewayAuditLogFilter的结尾就不记录日志
            request.setAttribute("logUpdate","401");
            map.put("message", "用户未通过身份认证，请携带token访问服务");
        }else {
            //如果用户携带的token有问题，请求不会走到GatewayAuditLogFilter里面去，这里直接插入日志
            log.info("add log to  execution stateCode=401");
            map.put("message", "无效的令牌，无法将访问令牌转换为JSON");
        }

        //返回自定义异常信息
        map.put("errorCode", HttpStatus.UNAUTHORIZED.value());
        map.put("path", request.getRequestURI());
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(map));
        writer.flush();

//        super.commence(request, response, authException);
    }
}
