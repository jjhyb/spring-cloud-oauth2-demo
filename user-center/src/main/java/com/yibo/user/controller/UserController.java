package com.yibo.user.controller;

import com.yibo.user.domain.entity.UserDO;
import com.yibo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: huangyibo
 * @Date: 2022/1/17 15:43
 * @Description:
 */
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/selectUserByUsername")
    public UserDO selectUserByUsername(String userName){
        return userService.selectUserByUsername(userName);
    }
}
