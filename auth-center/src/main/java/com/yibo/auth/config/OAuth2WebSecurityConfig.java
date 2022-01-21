package com.yibo.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: huangyibo
 * @Date: 2021/8/18 23:10
 * @Description: WebSecurityConfigurerAdapter Web应用安全配置的适配器类
 */

@Configuration
@EnableWebSecurity//让安全配置生效
public class OAuth2WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 退出成功之后的处理器
     */
    @Autowired
    private OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        //采用bcrypt对密码进行编码
        return new BCryptPasswordEncoder();
    }

    /**
     * 用于构建AuthenticationManager
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //userDetailsService会会根据用户名获取用户信息
        auth.userDetailsService(userDetailsService)
                //然后通过passwordEncoder将输入的密码和获取到的用户密码进行比对
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 将AuthenticationManager暴露成Bean
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 重写该方法，添加退出成功之后的处理器
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin().and()  //.formLogin().loginPage()可以指定登录的自定义页面
            .httpBasic().and()
            .logout()
            .logoutSuccessHandler(oAuth2LogoutSuccessHandler);
    }
}
