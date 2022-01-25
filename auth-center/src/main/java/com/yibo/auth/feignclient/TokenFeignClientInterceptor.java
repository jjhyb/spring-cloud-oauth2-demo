package com.yibo.auth.feignclient;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: huangyibo
 * @Date: 2022/1/25 16:58
 * @Description:
 *
 * 发送FeignClient设置Header信息
 *
 * 微服务之间通过Feign调用，通过拦截器在feign请求之前，把当前服务的token添加到目标服务的请求头里
 */
@Component
public class TokenFeignClientInterceptor implements RequestInterceptor {

    /**
     * token放在请求头
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //1、从header里面获取token
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String token = request.getHeader("Authorization");
            //2、将token传递
            if(!StringUtils.isEmpty(token)){
                requestTemplate.header("Authorization",token);
            }
        }
    }
}

