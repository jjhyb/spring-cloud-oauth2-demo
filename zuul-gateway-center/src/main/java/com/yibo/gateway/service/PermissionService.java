package com.yibo.gateway.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: huangyibo
 * @Date: 2019/11/13 19:21
 * @Description:
 */
public interface PermissionService {

    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
