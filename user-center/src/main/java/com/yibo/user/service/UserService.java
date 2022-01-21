package com.yibo.user.service;

import com.yibo.user.domain.entity.UserDO;
import com.yibo.user.mapper.UserDOMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: huangyibo
 * @Date: 2022/1/17 17:53
 * @Description:
 */

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    public UserDO selectUserByUsername(String userName){
        UserDO user = new UserDO();
        user.setUsername(userName);
        return userDOMapper.selectOne(user);
    }
}
