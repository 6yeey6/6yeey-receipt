package com.ibg.receipt.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {
    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public String set(final String key, final String value);

    /**
     * 设置值，有超时时间
     *
     * @param key
     * @param seconds
     * @param value
     */
    public String setex(final String key, final int seconds, final String value);

    /**
     * 当且仅当 key 不存在时设置值
     *
     * @param key
     * @param value
     */
    public Long setnx(final String key, final String value);

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public String get(final String key);

    /**
     * 设置对象，有超时时间
     *
     * @param key
     * @param seconds
     * @param value
     */
    public void setObject(final String key, final int seconds, final Object object);

    /**
     * 设置对象，无超时时间 慎用
     *
     * @param key
     * @param seconds
     * @param value
     */
    public void setObject(final String key, final Object object);

    /**
     * 获取对象
     *
     * @param key
     * @return
     */
    public Object getObject(final String key);

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(final String key);

    /**
     * 为key设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(final String key, final int seconds);

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(final byte[] key);

    /**
     * 设置哈希值
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hset(final String key, final String field, final String value);

    /**
     * 获取哈希值
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(final String key, final String field);

    /**
     * 删除哈希值
     *
     * @param key
     * @param field
     */
    public void hdel(final String key, final String field);

    /**
     * 一次设置多个hash值
     * @param key
     * @param map
     * @return
     */
    String hmset(final String key, final Map<String, String> map);

    /**
     * 一次获取多个hash值
     * @param key
     * @param fields
     * @return
     */
    List<String> hmget(final String key, final String... fields);

    /**
     * 加一的原子操作
     *
     * @param key
     * @param field
     */
    public Long incr(final String key);

    /**
     * 减一的原子操作
     *
     * @param key
     * @param field
     */
    public Long decr(final String key);

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public String set(final byte[] key, final byte[] value);

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public String setex(final byte[] key, final int seconds, final byte[] value);

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public byte[] get(final byte[] key);

    /**
     * 删除制定key 值
     *
     * @param key
     */
    public Long del(final String key);

    /**
     * set add
     *
     * @param key
     */
    public Long sadd(final String key, final String... member);

    /**
     * set member key
     *
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(final String key, final String member);

    /**
     * list lpush
     *
     * @param key
     */
    public Long lpush(final String key, final String... member);

    /**
     * list lpop
     *
     * @param key
     */
    public String lpop(final String key);

    /**
     * list llen
     *
     * @param key
     */
    public Long llen(final String key);

    /**
     * list rpush
     *
     * @param key
     */
    public Long rpush(final String key, final String... member);

    /**
     * 原子性的设置该Key为指定的Value，同时返回该Key的原有值。
     *
     * @param key
     * @param value
     */
    public String getSet(final String key, final String value);

    /**
     * 设置哈希值
     *
     * @param key
     * @param field
     * @param value
     */
    public void hsetObject(final String key, final String field, final Object value);

    /**
     * 获取哈希值
     *
     * @param key
     * @param field
     * @param <T>
     * @return
     */
    public <T> T hgetObject(final String key, final String field);

    /**
     * 获得符合给定模式 pattern 的 key
     *
     * @param keyPattern
     * @return
     */
    public Set<String> keys(final String keyPattern);

    /**
     * 返回集合 key 中的所有成员
     *
     * @param key
     * @return
     */
    public Set<String> smembers(final String key);

    /**
     * 删除哈希值
     *
     * @param key
     * @param field
     */
    public void hdelObject(final String key, final String field);

    /**
     * 获取哈希所有key值集合
     *
     * @param key
     * @return
     */
    public Set<String> hkeys(final String key);

    /**
     * 加锁
     *
     * @param lock
     * @param expired
     * @return
     */
    public Boolean acquireLock(final String lock, final long expired);

    /**
     * 释放锁
     *
     * @param lock
     */
    public void releaseLock(final String lock);

    public long rpushx(final String key, final String value, final int seconds);

    public long rpush(final String key, final int seconds, final String... value);

    public void ltrim(final String key, final int start, final int end);

    public List<String> lrange(final String key, final int start, final int end);

    public Long ttl(final String key);

    public Long sdiffStore(final String dstkey, final String... keys);

    public String rename(final String oldkey, final String newKey);

    /**
     * redis加锁带TTL
     *
     * @param lock
     * @param expired
     * @return
     */
    public Boolean acquireLockWithTTL(final String lock, final int expired, int ttl);

    /**
     * redis加锁带TTL jedis 新版本可以setnx和expire原子操作
     *
     * @param lock
     * @param expired
     *            单位秒
     * @return
     */
    public Boolean acquireLockWithTTL(final String lock, int expired);

    /**
     * redis加锁带TTL jedis 新版本可以setnx和expire原子操作
     *
     * @param lock
     * @param expired
     * @param value
     *            单位秒
     * @return
     */
    public Boolean acquireLockWithTTL(final String lock,String value, int expired);

    /**
     * 释放锁 和 requireLockWithTTL搭配
     *
     * @param lock
     */
    public void releaseLockWithTTL(final String lock);

    /**
     * 是否在锁着
     *
     * @param lockKey
     * @return
     */
    public Boolean isExistLock(final String lockKey);

    /**
     * 带有等待时间的获取锁方法
     *
     * @param lockKey
     *      锁的Key
     * @return 获取锁成功返回ture，超时返回false
     */
    boolean lock(String lockKey);

    /**
     * 释放锁
     * @param lockKey
     *      锁的Key
     */
    void unlock(String lockKey, String originValue);


    /**
     * hash field的原子加操作
     * <P>只能加整形的数字，有符号</P>
     * @param key
     * @param field
     * @param value 整形数值，当为负数时是减操作
     * @return
     */
    Long hincrBy(final String key, final String field, long value);

    /**
     * hash field的原子加操作
     * <P>使用浮点型的数字，有符号</P>
     * @param key
     * @param field
     * @param value  浮点数值，当为负数时是减操作
     * @return
     */
    Double hincrByFloat(final String key, final String field, Double value);

    /**
     * 测试hash中是否存在指定字段
     * @param key
     * @param field
     * @return  存在返回true，否则返回false
     */
    Boolean hexists(String key, String field);


    /**
     * 执行lua脚本
     * @param script 执行脚本
     * @param keys 脚本参数
     * @param values 脚本值
     * @return  Object 脚本执行结果
     */
    Object eval(String script, List<String> keys,List<String> values);

}
