package com.yibo.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author: huangyibo
 * @Date: 2022/2/8 16:39
 * @Description: StringRedisTemplate实现分布式锁
 */

@Component
@Slf4j
public class RedisLockUtils {

    public static final String LOCK_PREFIX = "redis_lock";

    public static final int LOCK_EXPIRE = 300; // ms

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 加锁
     * @param key 锁名称
     * @param value 当前时间 + 超时时间
     * @param timeout 过期时间 ms
     * @return
     */
    public boolean lock(String key, String value, Integer timeout){
        String lockKey = LOCK_PREFIX + key;
        if(timeout == null || timeout < 0){
            timeout = LOCK_EXPIRE;
        }

        if (redisTemplate.opsForValue().setIfAbsent(lockKey, value, timeout, TimeUnit.MILLISECONDS)){
            return true;
        }

        //解决死锁，且当多个线程同时来时，只会让一个线程拿到锁
        String currentValue = redisTemplate.opsForValue().get(key);

        //如果过期
        if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()){
            //获取上一个锁的时间
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            if (StringUtils.isEmpty(oldValue) || oldValue.equals(currentValue)){
                return true;
            }
        }
        return false;
    }


    /**
     * 解锁
     * @param key 锁名称
     * @param value 当前时间 + 超时时间
     */
    public void unlock(String key, String value){
        try {
            String currentValue = redisTemplate.opsForValue().get(key);

            //加锁的value和解锁的value相同,才能操作解锁
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)){
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception e){
            log.error("【redis锁】解锁失败, key:{}, value:{}", key, value, e);
        }
    }
}
