package com.yibo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: huangyibo
 * @Date: 2019/11/14 23:19
 * @Description: 审计日志过滤器应该在认证过滤器之后，授权过滤器之前执行
 */

@Slf4j
public class GatewayAuditLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String requestURI = request.getRequestURI();
        //记录日志，应该向数据库AuditLog表中插入一条日志，并返回主键id，这里用打印log替代
        log.info("add log for username={},requestURI={}",username,requestURI);

        filterChain.doFilter(request,response);

        //更新日志，根据上面的主键id，更新用户username的请求执行状态，这里用打印log替代
        //如果request不能取到值，才执行,如果访问被拒绝"logUpdate"的值在GatewayAccessDeniedHandler里面设置
        //如果认证失败"logUpdate"的值在GatewayAuthenticationEntryPoint里面设置
        if(StringUtils.isEmpty(request.getAttribute("logUpdate"))){
            int status = response.getStatus();
            log.info("update log to success execution stateCode={}",status);
        }

    }
}
