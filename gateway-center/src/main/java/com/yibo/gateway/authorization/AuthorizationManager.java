package com.yibo.gateway.authorization;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import com.yibo.gateway.constant.AuthConstant;
import com.yibo.gateway.constant.RedisConstant;
import com.yibo.gateway.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: huangyibo
 * @Date: 2022/1/21 18:51
 * @Description: 鉴权管理器，用于判断是否有资源的访问权限
 */
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private Set<String> permitAll = new ConcurrentHashSet<>();

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AuthorizationManager (){
        permitAll.add("/");
        permitAll.add("/error");
        permitAll.add("/favicon.ico");
        permitAll.add("/**/v2/api-docs/**");
        permitAll.add("/**/swagger-resources/**");
        permitAll.add("/webjars/**");
        permitAll.add("/doc.html");
        permitAll.add("/swagger-ui.html");
        permitAll.add("/**/oauth/**");
        permitAll.add("/**/current/get");
    }

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 实现权限验证判断
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {

        ServerWebExchange exchange = authorizationContext.getExchange();
        //请求资源
        String requestPath = exchange.getRequest().getURI().getPath();

        // 是否直接放行
        if (permitAll(requestPath)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        //从Redis中获取当前路径可访问角色列表
        //URI uri = authorizationContext.getExchange().getRequest().getURI();
        //权限数据应该是从redis中获取, 下面两张获取方式都可以
        //String pathAuth = (String)redisTemplate.opsForHash().get(RedisConstant.RESOURCE_ROLES_MAP, requestPath);
        //String pathAuth = (String)redisUtils.hGet(RedisConstant.RESOURCE_ROLES_MAP, requestPath);
        //List<String> authorities = JSONObject.parseArray(pathAuth, String.class);

        //权限数据这里写死, 限数据应该是从redis中获取
        List<String> authorities = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        authorities = authorities.stream().map(i -> i = AuthConstant.AUTHORITY_PREFIX + i).collect(Collectors.toList());

        //认证通过且角色匹配的用户可访问当前路径
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * 校验是否属于静态资源
     * @param requestPath 请求路径
     * @return
     */
    private boolean permitAll(String requestPath) {
        return permitAll.stream()
                .filter(r -> antPathMatcher.match(r, requestPath)).findFirst().isPresent();
    }

}