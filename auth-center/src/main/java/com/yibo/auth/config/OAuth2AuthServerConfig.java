package com.yibo.auth.config;

import com.yibo.auth.exception.CustomWebResponseExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.security.KeyPair;

/**
 * @Author: huangyibo
 * @Date: 2021/8/18 23:04
 * @Description: AuthorizationServerConfigurerAdapter OAuth2授权服务器配置的适配器类
 */

@Configuration
@EnableAuthorizationServer//当前的应用为认证授权服务器
public class OAuth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator;

    @Autowired
    private CustomUserAuthenticationConverter customUserAuthenticationConverter;

    /**
     * 客户端密码加密规则
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore(){
        //这是将token存储到数据库中
//        return new JdbcTokenStore(dataSource);
        //使用jwt存储token
        return new JwtTokenStore(this.jwtTokenEnhancer());
    }

    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //使用非对称加密
        //KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("xundu.keystore"),"xundukeystore".toCharArray());
        jwtAccessTokenConverter.setKeyPair(keyPair());

        //配置自定义的CustomUserAuthenticationConverter，拓展JWT的存储信息，JWT默认只存了用户信息的username
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) jwtAccessTokenConverter.getAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter);
        return jwtAccessTokenConverter;
    }

    /**
     * 从classpath下的**库中获取**对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        //使用非对称加密
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("xundu.keystore"),"xundukeystore".toCharArray());
        return keyStoreKeyFactory.getKeyPair("xundu");
    }

    /**
     * 让认证服务器知道哪些用户可以来访问认证服务器
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //userDetailsService是专门给refresh_Token使用的，因为刷新token的时候只传上来用户名
                .userDetailsService(userDetailsService)//用户信息service
                .tokenStore(this.tokenStore())  //令牌存储
                .tokenEnhancer(this.jwtTokenEnhancer())
                .authenticationManager(authenticationManager) //认证管理器
                .exceptionTranslator(customWebResponseExceptionTranslator); //认证管理器
                //.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    /**
     * 将客户端信息存储到数据库
     * @return
     */
    @Bean
    public ClientDetailsService clientDetailsService() {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }

    /**
     * 客户端详情服务配置，让认证服务器知道有哪些客户端应用来请求令牌
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        //默认值InMemoryTokenStore对于单个服务器是完全正常的（即，在发生故障的情况下，低流量和热备份备份服务器）。大多数项目可以从这里开始，也可以在开发模式下运行，以便轻松启动没有依赖关系的服务器。
        //这JdbcTokenStore是同一件事的JDBC版本，它将令牌数据存储在关系数据库中。如果您可以在服务器之间共享数据库，则可以使用JDBC版本，如果只有一个，则扩展同一服务器的实例，或者如果有多个组件，则授权和资源服务器。要使用JdbcTokenStore你需要“spring-jdbc”的类路径。

        //这个地方指的是从jdbc查出数据来存储

        // 使用基于 JDBC 存储模式
        //JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        // client_secret 加密
        //clientDetailsService.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(clientDetailsService());

        //客户端信息 Memory配置方式
//        clients.inMemory()
//                .withClient("order-app") //客户端应用的用户名
//                .secret(passwordEncoder.encode("123456"))   //应用的密码
//                .scopes("read","write") //orderApp应用可以获取到的权限的集合
//                .accessTokenValiditySeconds(3600)   //发出去的token有效期，单位秒
//                .resourceIds("order-server") //发给orderApp的token可以访问哪些资源服务器，可以配置多个
//                .authorizedGrantTypes("password") //针对orderApp使用哪种授权模式，有密码模式，授权码模式等
//                .and()
//                //将资源服务orderServer也暴露给认证服务
//                .withClient("order-center")  //客户端应用的名称
//                .secret(passwordEncoder.encode("123456"))   //应用的密码
//                .scopes("read","write") //orderApp应用可以获取到的权限的集合
//                .accessTokenValiditySeconds(3600)   //发出去的token有效期，单位秒
//                .resourceIds("order-server") //发给orderApp的token可以访问哪些资源服务器，可以配置多个
//                .authorizedGrantTypes("password");
    }

    /**
     * 授权服务器的安全配置
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //开启/oauth/token_key验证端口认证权限访问
                //springOAuth会往外暴露/oauth/token_key服务，只有认证通过的请求能访问这个服务拿到tokenKey，通过字符串去验token签名
                //.tokenKeyAccess("isAuthenticated()")
                .tokenKeyAccess("permitAll()")

                // 开启/oauth/check_token验证端口认证权限访问
                .checkTokenAccess("isAuthenticated()")//校验token需要认证通过，可采用http basic认证
                //主要是让/oauth/token支持client_id以及client_secret作登录认证
                .allowFormAuthenticationForClients();
    }
}
