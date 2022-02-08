package com.yibo.gateway.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: huangyibo
 * @Date: 2022/1/24 17:33
 * @Description: Redis工具类
 */

@Component
@Slf4j
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    // =============================common============================


    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.expire 方法指定缓存失效时间异常, key:{}, time:{}", key, time, e);
            return false;
        }
    }


    /**
     * 根据 key 获取过期时间
     *
     * @param key 键(不能为 Null)
     * @return 时间(秒) 返回0代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断 key 是否存在
     *
     * @param key 键(不能为 Null)
     * @return true 存在 false 不存在
     */
    public boolean hashKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("RedisUtils.hashKey 方法判断key是否存在异常, key:{}", key, e);
            return false;
        }
    }


    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if(key != null && key.length > 0){
            if(key.length == 1){
                redisTemplate.delete(key[0]);
            }
            if(key.length > 1) {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }


    //==================================String相关操作====================================


    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }


    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true 成功 false 失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.set 设置普通缓存异常, key:{}, value:{}",key, JSON.toJSONString(value), e);
            return false;
        }
    }


    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time > 0 若 time <= 0 将设置无限期
     * @return true 成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.set 设置普通缓存并设置时间异常, key:{}, value:{}, time:{}",key, JSON.toJSONString(value), time, e);
            return false;
        }
    }


    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须小于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }


    // ================================Map相关操作=================================


    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     */
    public Object hGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }


    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmGet(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public boolean hmSet(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.hmSet HashSet设置缓存异常, key:{}, map:{}",key, JSON.toJSONString(map), e);
            return false;
        }
    }


    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmSet(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.hmSet HashSet设置缓存并设置时间异常, key:{}, map:{}, time:{}", key, JSON.toJSONString(map), time, e);
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hSet(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.hSet hash表中设置缓存异常,key:{}, item:{}, value:{}", key, item, JSON.toJSONString(value), e);
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hSet(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.hSet hash表中设置缓存异常, key:{}, item:{}, value:{}, time:{}", key, item, JSON.toJSONString(value), time, e);
            return false;
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hDel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public double hIncr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }


    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public double hDecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    // ============================set相关操作=============================


    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("RedisUtils.sGet 根据key获取Set中的所有值异常, key:{}", key, e);
            return null;
        }
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("RedisUtils.sHasKey 方法异常, key:{}, value:{}", key, JSON.toJSONString(value), e);
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("RedisUtils.sSet 方法异常, key:{}, values:{}", key, JSON.toJSONString(values), e);
            return 0;
        }
    }


    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("RedisUtils.sSetAndTime 方法异常, key:{}, time:{}, values:{}", key, time, JSON.toJSONString(values), e);
            return 0;
        }
    }


    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("RedisUtils.sGetSetSize 方法异常, key:{}", key, e);
            return 0;
        }
    }


    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("RedisUtils.setRemove 方法异常, key:{}, values:{}", key, JSON.toJSONString(values), e);
            return 0;
        }
    }


    // ===============================list相关操作=================================


    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtils.lGet 方法异常, key:{}, start:{}, end:{}", key, start, end, e);
            return null;
        }
    }


    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("RedisUtils.lGetListSize 方法异常, key:{}", key, e);
            return 0;
        }
    }


    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("RedisUtils.lGetIndex 方法异常, key:{}, index:{}", key, index, e);
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.lSet 方法异常, key:{}, value:{}", key, JSON.toJSONString(value), e);
            return false;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.lSet 方法异常, key:{}, value:{}, time:{}", key, JSON.toJSONString(value), time, e);
            return false;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.lSet 方法异常, key:{}, value:{}", key, JSON.toJSONString(value), e);
            return false;
        }

    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.lSet 方法异常, key:{}, value:{}, time:{}", key, JSON.toJSONString(value), time, e);
            return false;
        }
    }


    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("RedisUtils.lUpdateIndex 方法异常, key:{}, index:{}, value:{}", key, index, JSON.toJSONString(value), e);
            return false;
        }
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error("RedisUtils.lRemove 方法异常, key:{}, count:{}, value:{}", key, count, JSON.toJSONString(value), e);
            return 0;
        }

    }


    // ===============================ZSet相关操作=================================


    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key 键
     * @param value 值
     * @param score score值
     * @return
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }


    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     * @param key 键
     * @param values 值集合
     * @return
     */
    public Long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }


    /**
     *  删除元素，可以删除多个
     * @param key 键
     * @param values 值集合
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }


    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key 键
     * @param value 值
     * @param delta 增加的score值
     * @return
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }


    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key 键
     * @param value 值
     * @return 0表示第一位
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }


    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }


    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置, -1查询所有
     * @return
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }


    /**
     * 获取集合元素, 并且把score值也获取
     *
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置, -1查询所有
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }


    /**
     * 根据Score值查询集合元素
     *
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }


    /**
     * 根据Score值查询集合元素, 从小到大排序
     *
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }


    /**
     * 根据Score值查询集合元素, 从小到大排序
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @param start 开始位置
     * @param end 结束位置, -1查询所有
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end);
    }


    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置, -1查询所有
     * @return
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }


    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置, -1查询所有
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }


    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }


    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(
            String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }


    /**
     * 根据Score值查询集合元素, 指定下标并从大到小排序
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @param start 开始位置
     * @param end 结束位置, -1查询所有
     * @return
     */
    public Set<Object> zReverseRangeByScore(String key, double min,
                                            double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
    }


    /**
     * 根据score值获取集合元素数量
     *
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }


    /**
     * 获取集合大小
     *
     * @param key 键
     * @return
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }


    /**
     * 获取集合大小
     *
     * @param key 键
     * @return
     */
    public Long zZCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }


    /**
     * 获取集合中value元素的score值
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }


    /**
     * 移除指定索引位置的成员
     *
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置, -1查询所有
     * @return
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }


    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key 键
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }


    /**
     * 获取key和otherKey的并集并存储在destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }


    /**
     * 获取key和otherKeys的并集并存储在destKey中
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
    }


    /**
     * 获取key和otherKey的交集并存储在destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
    }


    /**
     * 取key和otherKeys的交集并存储在destKey中
     *
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
    }


    /**
     * 匹配获取键值对，ScanOptions.NONE为获取全部键值对；
     * ScanOptions.scanOptions().match("C").build()匹配获取键位map1的键值对,不能模糊匹配。
     * @param key
     * @param options
     * @return
     */
    public Cursor<ZSetOperations.TypedTuple<Object>> zScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }


    // ===============================HyperLogLog相关操作=================================


    /**
     * 将任意数量的元素添加到指定的 HyperLogLog 里面。
     * 时间复杂度： 每添加一个元素的复杂度为 O(1) 。
     * 如果 HyperLogLog 估计的近似基数（approximated cardinality）在命令执行之后出现了变化， 那么命令返回1， 否则返回0 。
     * 如果命令执行时给定的键不存在， 那么程序将先创建一个空的 HyperLogLog 结构， 然后再执行命令。
     * @param key
     * @param value
     * @return
     */
    public long pfAdd(String key, String value) {
        return redisTemplate.opsForHyperLogLog().add(key, value);
    }


    /**
     * 返回给定 HyperLogLog 的基数估算值。
     * PFCOUNT作用于单个key的时候，返回该key的基数。
     * PFCOUNT命令作用于多个key时，返回并集的近似数。
     * @param key
     * @return
     */
    public long pfCount(String key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }


    public void pfRemove(String key) {
        redisTemplate.opsForHyperLogLog().delete(key);
    }

    /**
     * 将多个 HyperLogLog 合并为一个 HyperLogLog
     * 将多个HyperLogLog合并为一个HyperLogLog。
     * 合并后的HyperLogLog的基数接近于全部输入的可见集合的并集。合并得出的hyperLogLog会被存储到destkey中去。
     *
     * 返回值：成功返回ok。
     * @param key1
     * @param key2
     */
    public void pfMerge(String key1, String key2) {
        redisTemplate.opsForHyperLogLog().union(key1, key2);
    }

}
