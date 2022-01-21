package com.yibo.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @Author: huangyibo
 * @Date: 2021/8/28 18:56
 * @Description:
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "gateway-center";

    /**
     * 设置资源服务器id，请求token中需要有此资源id才能通过
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID).stateless(true);
                //配置表达式处理器，用于解析下面方法的配置规则字符串
                //.expressionHandler(gatewayWebSecurityExpressionHandler)
                //403 没有权限 访问被拒绝的处理器
                //.accessDeniedHandler(gatewayAccessDeniedHandler)
                //401 认证失败 认证失败的处理器
                //.authenticationEntryPoint(gatewayAuthenticationEntryPoint);
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
                //.addFilterBefore(new GatewayRateLimitFilter(), SecurityContextPersistenceFilter.class)
                //表示在ExceptionTranslationFilter过滤器之前执行
                //.addFilterBefore(new GatewayAuditLogFilter(), ExceptionTranslationFilter.class)
                .authorizeRequests()
                //开启/oauth/**验证端口无权限可以访问，即申请令牌的请求不需要带token令牌
                .antMatchers("/token/**").permitAll()
                //其他的请求需要带令牌才可以访问网关
                .anyRequest().authenticated();
                //手动指定访问规则
                //.anyRequest().access("#permissionService.hasPermission(request,authentication)");
    }
}
