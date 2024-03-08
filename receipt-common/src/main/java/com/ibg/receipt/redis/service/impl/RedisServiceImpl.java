package com.ibg.receipt.redis.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.ibg.receipt.base.exception.ServiceException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibg.receipt.redis.service.RedisCallBack;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.util.SerializeUtil;
import com.ibg.receipt.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private JedisPool rJedisPool;

    @Autowired
    private JedisPool wJedisPool;

    private RedisManager rRedisManager;

    private RedisManager wRedisManager;

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SET_WITH_EXPIRE_TIME = "PX";

    /** 重试时间 */
    private static final int DEFAULT_ACQUIRY_RETRY_MILLIS = 100;
    /** 锁超时时间，防止线程在入锁以后，防止阻塞后面的线程无法获取锁 */
    private int expireMsecs = 60 * 1000;
    /** 线程获取锁的等待时间 */
    private int timeoutMsecs = 10 * 1000;
    /** 是否锁定标志 */
    private volatile boolean locked = false;

    @PostConstruct
    public void initialize() {
        this.wRedisManager = new RedisManager(wJedisPool);
        this.rRedisManager = new RedisManager(rJedisPool);
    }

    @Override
    public String set(final String key, final String value) {
        return wRedisManager.request(new RedisCallBack<String>() {
            public String doInRequest(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    @Override
    public String setex(final String key, final int seconds, final String value) {
        return wRedisManager.request(new RedisCallBack<String>() {
            public String doInRequest(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    @Override
    public Long setnx(final String key, final String value) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            public Long doInRequest(Jedis jedis) {
                return jedis.setnx(key, value);
            }
        });
    }

    @Override
    public String get(final String key) {
        return rRedisManager.request(new RedisCallBack<String>() {
            public String doInRequest(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    @Override
    public void setObject(final String key, final int seconds, final Object object) {
        wRedisManager.request(new RedisCallBack<String>() {
            public String doInRequest(Jedis jedis) {
                return jedis.setex(key.getBytes(), seconds, SerializeUtil.serialize(object));
            }
        });
    }

    @Override
    public void setObject(final String key, final Object object) {
        wRedisManager.request(new RedisCallBack<String>() {
            public String doInRequest(Jedis jedis) {
                return jedis.set(key.getBytes(), SerializeUtil.serialize(object));
            }
        });
    }

    @Override
    public Object getObject(final String key) {

        return rRedisManager.request(new RedisCallBack<Object>() {
            public Object doInRequest(Jedis jedis) {

                return SerializeUtil.unserialize(jedis.get(key.getBytes()));
            }
        });
    }

    @Override
    public Boolean exists(final String key) {
        return rRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    @Override
    public Long expire(final String key, final int seconds) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    @Override
    public Boolean exists(final byte[] key) {
        return rRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    @Override
    public Long hset(final String key, final String field, final String value) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    @Override
    public String hmset(final String key, final Map<String, String> map) {
        return wRedisManager.request(new RedisCallBack<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.hmset(key, map);
            }
        });
    }

    @Override
    public List<String> hmget(final String key, final String... fields) {
        return rRedisManager.request(new RedisCallBack<List<String>>() {
            @Override
            public List<String> doInRequest(Jedis jedis) {
                return jedis.hmget(key, fields);
            }
        });
    }

    @Override
    public Long hincrBy(final String key, final String field, long value){
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hincrBy(key, field, value);
            }
        });
    }

    @Override
    public Double hincrByFloat(final String key, final String field, Double value){
        return wRedisManager.request(new RedisCallBack<Double>() {
            @Override
            public Double doInRequest(Jedis jedis) {
                return jedis.hincrByFloat(key, field, value);
            }
        });
    }

    @Override
    public Boolean hexists(String key, String field){
        return rRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return jedis.hexists(key, field);
            }
        });
    }



    @Override
    public String hget(final String key, final String field) {
        return rRedisManager.request(new RedisCallBack<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }

    @Override
    public void hdel(final String key, final String field) {
        wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    @Override
    public Long incr(final String key) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    @Override
    public Long decr(final String key) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    @Override
    public String set(final byte[] key, final byte[] value) {
        return wRedisManager.request(new RedisCallBack<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    @Override
    public String setex(final byte[] key, final int seconds, final byte[] value) {
        return wRedisManager.request(new RedisCallBack<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    @Override
    public byte[] get(final byte[] key) {
        return rRedisManager.request(new RedisCallBack<byte[]>() {
            @Override
            public byte[] doInRequest(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    @Override
    public Long del(final String key) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.del(key);
            };
        });
    }

    @Override
    public Long sadd(final String key, final String... member) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.sadd(key, member);
            };
        });
    }

    @Override
    public Boolean sismember(final String key, final String member) {
        return rRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return jedis.sismember(key, member);
            }
        });
    }

    @Override
    public Long lpush(final String key, final String... member) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.lpush(key, member);
            };
        });
    }

    @Override
    public String lpop(final String key) {
        return wRedisManager.request(new RedisCallBack<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.lpop(key);
            };
        });
    }

    @Override
    public Long llen(final String key) {
        return rRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.llen(key);
            };
        });
    }

    @Override
    public Long rpush(final String key, final String... member) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.rpush(key, member);
            };
        });
    }

    @Override
    public String getSet(final String key, final String value) {
        return wRedisManager.request(new RedisCallBack<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.getSet(key, value);
            };
        });
    }

    @Override
    public void hsetObject(final String key, final String field, final Object value) {
        wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hset(key.getBytes(), field.getBytes(), SerializeUtil.serialize(value));
            }
        });
    }

    @Override
    public <T> T hgetObject(final String key, final String field) {
        return rRedisManager.request(new RedisCallBack<T>() {
            public T doInRequest(Jedis jedis) {
                return (T) SerializeUtil.unserialize(jedis.hget(key.getBytes(), field.getBytes()));
            }
        });
    }

    @Override
    public Set<String> keys(final String keyPattern) {
        return rRedisManager.request(new RedisCallBack<Set<String>>() {
            public Set<String> doInRequest(Jedis jedis) {
                return jedis.keys(keyPattern);
            }
        });
    }

    @Override
    public Set<String> smembers(final String key) {
        return rRedisManager.request(new RedisCallBack<Set<String>>() {
            @Override
            public Set<String> doInRequest(Jedis jedis) {
                return jedis.smembers(key);
            }
        });
    }

    @Override
    public void hdelObject(final String key, final String field) {
        wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hdel(key.getBytes(), field.getBytes());
            }
        });
    }

    @Override
    public Set<String> hkeys(final String key) {
        return rRedisManager.request(new RedisCallBack<Set<String>>() {
            @Override
            public Set<String> doInRequest(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    @Override
    public Boolean acquireLock(final String lock, final long expired) {
        return wRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {

                boolean success = false;

                // expired 单位为秒
                long value = System.currentTimeMillis() + expired * 1000 + 1;

                long acquired = jedis.setnx(lock, String.valueOf(value));
                if (acquired == 1) {
                    success = true;
                } else {
                    String oldStringVaule = jedis.get(lock);
                    if (StringUtils.isBlank(oldStringVaule)) {// 双重判断，可能TTL超时
                        jedis.set(lock, String.valueOf(value));
                        return true;
                    }
                    long oldValue = Long.valueOf(jedis.get(lock));

                    if (oldValue < System.currentTimeMillis()) {
                        String getValue = jedis.getSet(lock, String.valueOf(value));
                        if (Long.valueOf(getValue) == oldValue) {
                            success = true;
                        } else {
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                }
                return success;
            };
        });
    }

    @Override
    public void releaseLock(final String lock) {
        wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                long current = System.currentTimeMillis();
                if (current < Long.valueOf(jedis.get(lock))) {
                    return jedis.del(lock);
                }
                return 0L;
            };
        });
    }

    @Override
    public long rpushx(final String key, final String value, final int seconds) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                long length = jedis.rpushx(key, value);
                jedis.expire(key, seconds);
                return length;
            }
        });
    }

    @Override
    public long rpush(final String key, final int seconds, final String... value) {
        return wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                long length = jedis.rpush(key, value);
                jedis.expire(key, seconds);
                return length;
            }
        });
    }

    @Override
    public void ltrim(final String key, final int start, final int end) {
        wRedisManager.request(new RedisCallBack<Object>() {
            @Override
            public Object doInRequest(Jedis jedis) {
                jedis.ltrim(key, start, end);
                return null;
            }
        });
    }

    @Override
    public List<String> lrange(final String key, final int start, final int end) {
        return wRedisManager.request(new RedisCallBack<List<String>>() {
            @Override
            public List<String> doInRequest(Jedis jedis) {
                return jedis.lrange(key, start, end);
            }
        });
    }

    @Override
    public Long ttl(final String key) {
        return rRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.ttl(key);
            }
        });
    }

    @Override
    public Long sdiffStore(final String dstkey, final String... keys) {
        return rRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.sdiffstore(dstkey, keys);
            }
        });
    }

    @Override
    public String rename(final String oldkey, final String newKey) {
        return rRedisManager.request(new RedisCallBack<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.rename(oldkey, newKey);
            }
        });
    }

    @Override
    public Boolean acquireLockWithTTL(final String lock, final int expired, int ttl) {
        return wRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return acquireLockWithTTL(lock, expired);
            };
        });
    }

    @Override
    public Boolean acquireLockWithTTL(final String lock, int expired) {
        return wRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                String result = jedis.set(lock, "", SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expired * 1000);
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
                return false;
            };
        });
    }

    @Override
    public Boolean acquireLockWithTTL(String lock, String value, int expired) {
        return wRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                String result = jedis.set(lock, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expired * 1000);
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
                return false;
            };
        });    }

    @Override
    public void releaseLockWithTTL(final String lock) {
        wRedisManager.request(new RedisCallBack<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                try {

                    return jedis.del(lock);

                } catch (Exception e) {
                    return 0L;
                }
            }
        });
    }

    @Override
    public Boolean isExistLock(final String lockKey) {
        return wRedisManager.request(new RedisCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {

                Boolean isExist = jedis.exists(lockKey);
                if (!isExist) {
                    return false;
                } else {
                    String oldStringVaule = jedis.get(lockKey);
                    if (StringUtils.isBlank(oldStringVaule)) {// 双重判断，可能TTL超时
                        return false;
                    }
                    long oldValue = Long.valueOf(oldStringVaule);

                    if (oldValue < System.currentTimeMillis()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            };
        });
    }

    @Override
    public boolean lock(String lockKey) {
        int timeout = timeoutMsecs;
        int count = 0;
        long startTime = System.currentTimeMillis();
        log.info("开始获取Redis锁...lockKey:{}", lockKey);
        while (timeout >= 0) {
            ++count;
            long expires = System.currentTimeMillis() + expireMsecs + 1;
            // 锁到期时间
            String expiresStr = String.valueOf(expires);
            if (this.setnx(lockKey, expiresStr) == 1) {
                log.info("在第[{}]次成功获取到锁，用时[{}]ms, lockKey:{}", count, System.currentTimeMillis() - startTime, lockKey);
                locked = true;
                return true;
            }
            // redis里key的时间
            String currentValue = this.get(lockKey);
            // 判断锁是否已经过期，过期则重新设置并获取
            if (currentValue != null && Long.parseLong(currentValue) < System.currentTimeMillis()) {
                // 设置锁并返回旧值
                String oldValue = this.getSet(lockKey, expiresStr);
                // 比较锁的时间，如果不一致则可能是其他锁已经修改了值并获取
                if (oldValue != null && oldValue.equals(currentValue)) {
                    log.info("在第[{}]次成功获取到锁，用时[{}]ms, lockKey:{}", count, System.currentTimeMillis() - startTime, lockKey);
                    locked = true;
                    return true;
                }
            }
            timeout -= DEFAULT_ACQUIRY_RETRY_MILLIS;
            // 延时
            try {
                Thread.sleep(DEFAULT_ACQUIRY_RETRY_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.warn("尝试获取锁[]次后失败，退出... 用时[{}]ms, lockKey:{}", count, System.currentTimeMillis() - startTime, lockKey);
        return false;
    }

    @Override
    public void unlock(String lockKey, String originValue) {
        if (locked) {
            String currentValue = this.get(lockKey);
            //判断这个锁是不是自己的
            if (currentValue != null && currentValue.equals(originValue)){
                this.del(lockKey);
                this.locked = false;
            }else {
                log.warn("lockKey:{}的值已被其它线程修改，不能执行解锁操作", lockKey);
            }
        }
    }



    @Override
    public Object eval(String script, List<String> keys, List<String> values) {
        return wRedisManager.request(new RedisCallBack<Object>() {
            @Override
            public Object doInRequest(Jedis jedis) {
                try {
                    return jedis.eval(script, keys, values);
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }

}
