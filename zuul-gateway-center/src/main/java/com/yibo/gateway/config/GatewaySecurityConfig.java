package com.yibo.gateway.config;

import com.yibo.gateway.filter.GatewayAuditLogFilter;
import com.yibo.gateway.filter.GatewayRateLimitFilter;
import com.yibo.gateway.handler.GatewayAccessDeniedHandler;
import com.yibo.gateway.handler.GatewayAuthenticationEntryPoint;
import com.yibo.gateway.handler.GatewayWebSecurityExpressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * @author: huangyibo
 * @Date: 2019/11/13 17:06
 * @Description: 网站服务作为资源服务器存在
 */

@Configuration
@EnableResourceServer
public class GatewaySecurityConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "gateway-server";

    @Autowired
    private GatewayWebSecurityExpressionHandler gatewayWebSecurityExpressionHandler;

    @Autowired
    private GatewayAccessDeniedHandler gatewayAccessDeniedHandler;

    @Autowired
    private GatewayAuthenticationEntryPoint gatewayAuthenticationEntryPoint;

    /**
     * 设置资源服务器id，请求token中需要有此资源id才能通过
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID).stateless(true)
                //配置表达式处理器，用于解析下面方法的配置规则字符串
                .expressionHandler(gatewayWebSecurityExpressionHandler)
                //403 没有权限 访问被拒绝的处理器
                .accessDeniedHandler(gatewayAccessDeniedHandler)
                //401 认证失败 认证失败的处理器
                .authenticationEntryPoint(gatewayAuthenticationEntryPoint);
    }

    /**
     * 这里根据业务需求进行灵活配置
     * 如果有一个权限系统，暴露出服务，和网关对接
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            //SecurityContextPersistenceFilter是SpringSecurity过滤器链的第一个过滤器，GatewayRateLimitFilter在它之前执行
            .addFilterBefore(new GatewayRateLimitFilter(), SecurityContextPersistenceFilter.class)
            //表示在ExceptionTranslationFilter过滤器之前执行
            .addFilterBefore(new GatewayAuditLogFilter(), ExceptionTranslationFilter.class)
            .authorizeRequests()
            //开启/oauth/**验证端口无权限可以访问，即申请令牌的请求不需要带token令牌
            .antMatchers("/token/**").permitAll()
            //其他的请求需要带令牌才可以访问网关
//                .anyRequest().authenticated();
            //手动指定访问规则
            .anyRequest().access("#permissionService.hasPermission(request,authentication)");
    }

}
