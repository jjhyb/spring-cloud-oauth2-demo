package com.yibo.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibo.gateway.result.CommonResult;
import com.yibo.gateway.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: huangyibo
 * @Date: 2022/7/15 16:47
 * @Description: 用于网关的全局异常处理
 * @Order(-1)： 优先级一定要比ResponseStatusExceptionHandler低
 */

@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("GlobalErrorExceptionHandler ", ex);
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        CommonResult<Object> resultMsg = new CommonResult<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(),null);


        // JOSN格式返回
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }

        //处理TOKEN失效的异常
        if (ex instanceof InvalidTokenException){
            resultMsg=new CommonResult<>(ResultCode.INVALID_TOKEN. getCode(),ResultCode.INVALID_TOKEN.getMessage(),null);
        }

        CommonResult<Object> finalResultMsg = resultMsg;
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                //返回响应结果，根据业务需求，自己定制
                return bufferFactory.wrap(new ObjectMapper().writeValueAsBytes(finalResultMsg));
            }
            catch (Exception e) {
                log.error("Error writing response", ex);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}

