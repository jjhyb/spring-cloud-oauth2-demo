package com.yibo.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/11/15 0:01
 * @Description: 403 没有权限 访问被拒绝的处理器，授权失败(forbidden)时返回信息
 */

@Component
@Slf4j
public class GatewayAccessDeniedHandler extends OAuth2AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException authException) throws IOException, ServletException {
//        super.handle(request, response, authException);

        //向数据库插入执行异常日志
        log.info("update log to  execution stateCode=403");
        //这里设置是为了GatewayAuditLogFilter里面去获取，如果这里记录了日志，那么GatewayAuditLogFilter的结尾就不记录日志
        request.setAttribute("logUpdate","403");
        //返回自定义异常信息
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 403);
        map.put("message", "访问被拒绝");
        map.put("path", request.getRequestURI());
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(map));
        writer.flush();
    }
}
