package com.ibg.receipt.redis.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.redis.service.RedisCallBack;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibg.receipt.redis.service.RedisClusterCallBack;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.util.MD5Utils;
import com.ibg.receipt.util.SerializeUtil;
import com.ibg.receipt.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

@Slf4j
//@Service
public class RedisClusterServiceImpl implements RedisService {
    @Autowired
    private JedisCluster cluster;

    private RedisClusterManager rRedisManager;

    private RedisClusterManager wRedisManager;

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
        this.wRedisManager = new RedisClusterManager(cluster);
        this.rRedisManager = new RedisClusterManager(cluster);
    }

    @Override
    public String set(final String key, final String value) {
        return wRedisManager.request(new RedisClusterCallBack<String>() {
            public String doInRequest(JedisCluster cluster) {
                return cluster.set(key, value);
            }
        });
    }

    @Override
    public String setex(final String key, final int seconds, final String value) {
        return wRedisManager.request(new RedisClusterCallBack<String>() {
            public String doInRequest(JedisCluster cluster) {
                return cluster.setex(key, seconds, value);
            }
        });
    }

    @Override
    public Long setnx(final String key, final String value) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            public Long doInRequest(JedisCluster cluster) {
                return cluster.setnx(key, value);
            }
        });
    }

    @Override
    public String get(final String key) {
        return rRedisManager.request(new RedisClusterCallBack<String>() {
            public String doInRequest(JedisCluster cluster) {
                return cluster.get(key);
            }
        });
    }

    @Override
    public void setObject(final String key, final int seconds, final Object object) {
        wRedisManager.request(new RedisClusterCallBack<String>() {
            public String doInRequest(JedisCluster cluster) {
                return cluster.setex(key.getBytes(), seconds, SerializeUtil.serialize(object));
            }
        });
    }

    @Override
    public void setObject(final String key, final Object object) {
        wRedisManager.request(new RedisClusterCallBack<String>() {
            public String doInRequest(JedisCluster cluster) {
                return cluster.set(key.getBytes(), SerializeUtil.serialize(object));
            }
        });
    }

    @Override
    public Object getObject(final String key) {

        return rRedisManager.request(new RedisClusterCallBack<Object>() {
            public Object doInRequest(JedisCluster cluster) {

                return SerializeUtil.unserialize(cluster.get(key.getBytes()));
            }
        });
    }

    @Override
    public Boolean exists(final String key) {
        return rRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {
                return cluster.exists(key);
            }
        });
    }

    @Override
    public Long expire(final String key, final int seconds) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.expire(key, seconds);
            }
        });
    }

    @Override
    public Boolean exists(final byte[] key) {
        return rRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {
                return cluster.exists(key);
            }
        });
    }

    @Override
    public Long hset(final String key, final String field, final String value) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.hset(key, field, value);
            }
        });
    }

    @Override
    public String hget(final String key, final String field) {
        return rRedisManager.request(new RedisClusterCallBack<String>() {
            @Override
            public String doInRequest(JedisCluster cluster) {
                return cluster.hget(key, field);
            }
        });
    }

    @Override
    public String hmset(final String key, final Map<String, String> map) {
        return wRedisManager.request(new RedisClusterCallBack<String>() {
            @Override
            public String doInRequest(JedisCluster cluster) {
                return cluster.hmset(key, map);
            }
        });
    }

    @Override
    public List<String> hmget(final String key, final String... fields) {
        return rRedisManager.request(new RedisClusterCallBack<List<String>>() {
            @Override
            public List<String> doInRequest(JedisCluster cluster) {
                return cluster.hmget(key, fields);
            }
        });
    }



    @Override
    public void hdel(final String key, final String field) {
        wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.hdel(key, field);
            }
        });
    }

    @Override
    public Long incr(final String key) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.incr(key);
            }
        });
    }

    @Override
    public Long decr(final String key) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.decr(key);
            }
        });
    }

    @Override
    public Long hincrBy(final String key, final String field, long value){
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster jedis) {
                return jedis.hincrBy(key, field, value);
            }
        });
    }

    @Override
    public Double hincrByFloat(final String key, final String field, Double value){
        return wRedisManager.request(new RedisClusterCallBack<Double>() {
            @Override
            public Double doInRequest(JedisCluster jedis) {
                return jedis.hincrByFloat(key, field, value);
            }
        });
    }

    @Override
    public Boolean hexists(String key, String field){
        return rRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster jedis) {
                return jedis.hexists(key, field);
            }
        });
    }

    @Override
    public String set(final byte[] key, final byte[] value) {
        return wRedisManager.request(new RedisClusterCallBack<String>() {
            @Override
            public String doInRequest(JedisCluster cluster) {
                return cluster.set(key, value);
            }
        });
    }

    @Override
    public String setex(final byte[] key, final int seconds, final byte[] value) {
        return wRedisManager.request(new RedisClusterCallBack<String>() {
            @Override
            public String doInRequest(JedisCluster cluster) {
                return cluster.setex(key, seconds, value);
            }
        });
    }

    @Override
    public byte[] get(final byte[] key) {
        return rRedisManager.request(new RedisClusterCallBack<byte[]>() {
            @Override
            public byte[] doInRequest(JedisCluster cluster) {
                return cluster.get(key);
            }
        });
    }

    @Override
    public Long del(final String key) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.del(key);
            };
        });
    }

    @Override
    public Long sadd(final String key, final String... member) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.sadd(key, member);
            };
        });
    }

    @Override
    public Boolean sismember(final String key, final String member) {
        return rRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {
                return cluster.sismember(key, member);
            }
        });
    }

    @Override
    public Long lpush(final String key, final String... member) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.lpush(key, member);
            };
        });
    }

    @Override
    public String lpop(final String key) {
        return wRedisManager.request(new RedisClusterCallBack<String>() {
            @Override
            public String doInRequest(JedisCluster cluster) {
                return cluster.lpop(key);
            };
        });
    }

    @Override
    public Long llen(final String key) {
        return rRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.llen(key);
            };
        });
    }

    @Override
    public Long rpush(final String key, final String... member) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.rpush(key, member);
            };
        });
    }

    @Override
    public String getSet(final String key, final String value) {
        return wRedisManager.request(new RedisClusterCallBack<String>() {
            @Override
            public String doInRequest(JedisCluster cluster) {
                return cluster.getSet(key, value);
            };
        });
    }

    @Override
    public void hsetObject(final String key, final String field, final Object value) {
        wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.hset(key.getBytes(), field.getBytes(), SerializeUtil.serialize(value));
            }
        });
    }

    @Override
    public <T> T hgetObject(final String key, final String field) {
        return rRedisManager.request(new RedisClusterCallBack<T>() {
            public T doInRequest(JedisCluster cluster) {
                return (T) SerializeUtil.unserialize(cluster.hget(key.getBytes(), field.getBytes()));
            }
        });
    }

    @Override
    public Set<String> keys(final String keyPattern) {
        return rRedisManager.request(new RedisClusterCallBack<Set<String>>() {
            public Set<String> doInRequest(JedisCluster cluster) {
                log.debug("Start getting keys...");
                TreeSet<String> keys = new TreeSet<>();
                Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
                for(String k : clusterNodes.keySet()){
                    log.debug("Getting keys from: {}", k);
                    JedisPool jp = clusterNodes.get(k);
                    Jedis connection = jp.getResource();
                    try {
                        keys.addAll(connection.keys(keyPattern));
                    } catch(Exception e){
                        log.error("Getting keys error: {}", e);
                    } finally{
                        log.debug("Connection closed.");
                        connection.close();
                    }
                }
                log.debug("Keys gotten!");
                return keys;
            }
        });
    }

    @Override
    public Set<String> smembers(final String key) {
        return rRedisManager.request(new RedisClusterCallBack<Set<String>>() {
            @Override
            public Set<String> doInRequest(JedisCluster cluster) {
                return cluster.smembers(key);
            }
        });
    }

    @Override
    public void hdelObject(final String key, final String field) {
        wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.hdel(key.getBytes(), field.getBytes());
            }
        });
    }

    @Override
    public Set<String> hkeys(final String key) {
        return rRedisManager.request(new RedisClusterCallBack<Set<String>>() {
            @Override
            public Set<String> doInRequest(JedisCluster cluster) {
                return cluster.hkeys(key);
            }
        });
    }

    @Override
    public Boolean acquireLock(final String lock, final long expired) {
        return wRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {

                boolean success = false;

                // expired 单位为秒
                long value = System.currentTimeMillis() + expired * 1000 + 1;

                long acquired = cluster.setnx(lock, String.valueOf(value));
                if (acquired == 1) {
                    success = true;
                } else {
                    String oldStringVaule = cluster.get(lock);
                    if (StringUtils.isBlank(oldStringVaule)) {// 双重判断，可能TTL超时
                        cluster.set(lock, String.valueOf(value));
                        return true;
                    }
                    long oldValue = Long.valueOf(cluster.get(lock));

                    if (oldValue < System.currentTimeMillis()) {
                        String getValue = cluster.getSet(lock, String.valueOf(value));
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
        wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                long current = System.currentTimeMillis();
                if (current < Long.valueOf(cluster.get(lock))) {
                    return cluster.del(lock);
                }
                return 0L;
            };
        });
    }

    @Override
    public long rpushx(final String key, final String value, final int seconds) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                long length = cluster.rpushx(key, value);
                cluster.expire(key, seconds);
                return length;
            }
        });
    }

    @Override
    public long rpush(final String key, final int seconds, final String... value) {
        return wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                long length = cluster.rpush(key, value);
                cluster.expire(key, seconds);
                return length;
            }
        });
    }

    @Override
    public void ltrim(final String key, final int start, final int end) {
        wRedisManager.request(new RedisClusterCallBack<Object>() {
            @Override
            public Object doInRequest(JedisCluster cluster) {
                cluster.ltrim(key, start, end);
                return null;
            }
        });
    }

    @Override
    public List<String> lrange(final String key, final int start, final int end) {
        return wRedisManager.request(new RedisClusterCallBack<List<String>>() {
            @Override
            public List<String> doInRequest(JedisCluster cluster) {
                return cluster.lrange(key, start, end);
            }
        });
    }

    @Override
    public Long ttl(final String key) {
        return rRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.ttl(key);
            }
        });
    }

    @Override
    public Long sdiffStore(final String dstkey, final String... keys) {
        return rRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                return cluster.sdiffstore(dstkey, keys);
            }
        });
    }

    @Override
    public String rename(final String oldkey, final String newKey) {
        return rRedisManager.request(new RedisClusterCallBack<String>() {
            @Override
            public String doInRequest(JedisCluster cluster) {
                return cluster.rename(oldkey, newKey);
            }
        });
    }

    @Override
    public Boolean acquireLockWithTTL(final String lock, final int expired, int ttl) {
        return wRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {
                return acquireLockWithTTL(lock, expired);
            };
        });
    }

    @Override
    public Boolean acquireLockWithTTL(final String lock, int expired) {
        return wRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {
                String result = cluster.set(lock, "", SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expired * 1000);
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
                return false;
            };
        });
    }

    @Override
    public Boolean acquireLockWithTTL(String lock, String value, int expired) {
        return wRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {
                String result = cluster.set(lock, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expired * 1000);
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
                return false;
            };
        });    }

    @Override
    public void releaseLockWithTTL(final String lock) {
        wRedisManager.request(new RedisClusterCallBack<Long>() {
            @Override
            public Long doInRequest(JedisCluster cluster) {
                try {

                    return cluster.del(lock);

                } catch (Exception e) {
                    return 0L;
                }
            }
        });
    }

    @Override
    public Boolean isExistLock(final String lockKey) {
        return wRedisManager.request(new RedisClusterCallBack<Boolean>() {
            @Override
            public Boolean doInRequest(JedisCluster cluster) {

                Boolean isExist = cluster.exists(lockKey);
                if (!isExist) {
                    return false;
                } else {
                    String oldStringVaule = cluster.get(lockKey);
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
    public Object eval(String script, List<String> keys,List<String> values) {
        return wRedisManager.request(new RedisClusterCallBack<Object>() {
            @Override
            public Object doInRequest(JedisCluster cluster) {
                try {
                    return cluster.eval(script,keys,values);
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }


}
