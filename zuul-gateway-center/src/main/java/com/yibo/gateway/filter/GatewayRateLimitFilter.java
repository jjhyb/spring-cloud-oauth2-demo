package com.yibo.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
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
 * @Date: 2019/11/13 20:07
 * @Description:
 */

@Slf4j
public class GatewayRateLimitFilter extends OncePerRequestFilter {

    private RateLimiter rateLimiter = RateLimiter.create(1);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("0 rate limit");
        if(rateLimiter.tryAcquire()){
            filterChain.doFilter(request,response);
        }else {
            //被限流了
            //返回自定义异常信息
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> map = new HashMap<>();
            map.put("errorCode", HttpStatus.TOO_MANY_REQUESTS.value());
            map.put("message", "请求太多，服务器繁忙，请稍后再试");
            map.put("path", request.getRequestURI());
            map.put("timestamp", String.valueOf(System.currentTimeMillis()));
            PrintWriter writer = response.getWriter();
            ObjectMapper objectMapper = new ObjectMapper();
            writer.write(objectMapper.writeValueAsString(map));
            writer.flush();
        }
    }
}
