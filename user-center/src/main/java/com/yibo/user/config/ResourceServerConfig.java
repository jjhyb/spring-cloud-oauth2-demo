package com.yibo.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author: huangyibo
 * @Date: 2022/1/25 17:06
 * @Description:
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//EnableGlobalMethodSecurity开户方法级别的保护
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()//去掉获取token会被拦截
                ///auth/** 授权中心远程调用的服务不用授权
                .antMatchers("/auth/**").permitAll()//开放的资源不用授权
                .antMatchers("/user/**").authenticated();
    }
}
