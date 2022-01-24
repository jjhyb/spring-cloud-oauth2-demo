package com.yibo.gateway.result;

/**
 * @author: huangyibo
 * @Date: 2022/1/21 18:56
 * @Description: 封装API的错误码
 */

public interface IErrorCode {

    long getCode();

    String getMessage();
}
