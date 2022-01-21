package com.yibo.auth.service;

import com.yibo.auth.client.UserFeignClient;
import com.yibo.auth.domain.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: huangyibo
 * @Date: 2021/8/18 23:12
 * @Description:
 */

@Component
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

   /* @Autowired
    private ClientDetailsService clientDetailsService;*/

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*//取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //密码 client_secret
                String clientSecret = clientDetails.getClientSecret();
                return new User(username,clientSecret,AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }*/

        //远程调用用户中心查询用户
        UserDTO userDTO = userFeignClient.selectUserByUsername(username);
        if(userDTO == null){
            log.info("用户名：["+username + "] 不存在");
            throw new BadCredentialsException("用户名：["+username + "] 不存在");
        }

        /*GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);*/

        //这里弄一个简单的权限集合，真实的开发中是上面远程调用用户中心查询用户时，一并查询用户的权限集合
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_ADMIN");
        UserJwt userJwt = new UserJwt(username,userDTO.getPassword(),
                userDTO.isEnabled(),
                userDTO.isAccountNonExpired(),
                userDTO.isCredentialsNonExpired(),
                userDTO.isAccountNonLocked()
                ,authorities);
        BeanUtils.copyProperties(userDTO,userJwt);
        return userJwt;
    }

    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encode);
    }
}
