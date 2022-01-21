package com.yibo.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: huangyibo
 * @Date: 2019/11/13 19:22
 * @Description:
 */

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    /**
     * 此方法需要远程调用权限系统查询用户权限，可以查询数据库，也可以查询redis(将权限缓存到redis)，
     * 也可以在启动的时候就调用此方法将权限数据缓存到内存中
     * @param request
     * @param authentication
     * @return
     */
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        log.info("request uri={}",request.getRequestURI());
        log.info("authentication={}",ReflectionToStringBuilder.toString(authentication));

        //如果用户访问接口没有携带令牌，那么就是一个匿名用户
        if(authentication instanceof AnonymousAuthenticationToken){
            log.error("用户访问接口未携带token令牌，为匿名用户");
            throw new AccessTokenRequiredException(null);
        }

        //这里设置有一半的几率有权限访问服务
        return RandomUtils.nextInt() % 2 == 0;
    }
}
