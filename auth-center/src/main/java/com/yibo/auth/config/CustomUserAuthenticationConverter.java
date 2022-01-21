package com.yibo.auth.config;

import com.yibo.auth.service.UserJwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: huangyibo
 * @Date: 2021/8/26 1:53
 * @Description: 拓展JWT的存储信息，JWT默认只存了用户信息的username
 */

@Component
@Slf4j
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user_name", authentication.getName());

        Object principal = authentication.getPrincipal();
        UserJwt userJwt = null;
        if(principal instanceof  UserJwt){
            userJwt = (UserJwt) principal;
        }else{
            //refresh_token连接默认不去调用userdetailService获取用户信息，这里我们手动去调用，得到 UserJwt
            //因为实际中refresh_token中也包含用户信息，测试没有进到这里面来
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            log.info("客户端调用refresh_token刷新token");
            userJwt = (UserJwt) userDetails;
        }
        response.put("id", userJwt.getId());
        response.put("nickname", userJwt.getNickname());
        response.put("phone",userJwt.getPhone());
        response.put("headPortrait",userJwt.getHeadPortrait());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }
}
