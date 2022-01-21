package com.yibo.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * @Author: huangyibo
 * @Date: 2021/8/26 1:57
 * @Description: 自定义 CustomWebResponseExceptionTranslator 实现认证服务器的异常信息处理
 */

@Component
@Slf4j
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    /**
     * 目前实现的OAuth2Exception，定义返回的异常在OAuth2Exception类中
     * @param exception
     * @return
     * @throws Exception
     */
    @Override
    public ResponseEntity translate(Exception exception) throws Exception {
        //OAuth2Exception异常
        if (exception instanceof OAuth2Exception) {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
            if(oAuth2Exception instanceof InvalidGrantException){
                //
                return ResponseEntity
                        .status(oAuth2Exception.getHttpErrorCode())
                        .body(new CustomOAuth2Exception("用户名或密码错误"));
            }
            return ResponseEntity
                    .status(oAuth2Exception.getHttpErrorCode())
                    .body(new CustomOAuth2Exception(oAuth2Exception.getMessage()));
        }
        //username不正确手动抛出的BadCredentialsException会进入到这里来
        else if(exception instanceof AuthenticationException){
            AuthenticationException authenticationException = (AuthenticationException) exception;
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new CustomOAuth2Exception(authenticationException.getMessage()));
        }
        //其余的异常会直接返回
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CustomOAuth2Exception(exception.getMessage()));
    }
}
