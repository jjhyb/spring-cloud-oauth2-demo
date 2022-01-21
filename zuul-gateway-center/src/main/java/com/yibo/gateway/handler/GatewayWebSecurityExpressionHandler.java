package com.yibo.gateway.handler;

import com.yibo.gateway.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

/**
 * @author: huangyibo
 * @Date: 2019/11/13 19:35
 * @Description: 表达式处理器
 */

@Component
public class GatewayWebSecurityExpressionHandler extends OAuth2WebSecurityExpressionHandler {

    @Autowired
    private PermissionService permissionService;

    /**
     * 创建一个评估上下文，然后解析GatewaySecurityConfig中的字符串"#permissionService.hasPermission(request,authentication)"
     * @param authentication
     * @param invocation
     * @return
     */
    @Override
    protected StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, FilterInvocation invocation) {
        StandardEvaluationContext sec = super.createEvaluationContextInternal(authentication, invocation);
        sec.setVariable("permissionService",permissionService);
        return sec;
    }
}
