package com.ibg.receipt.redis.service.impl;

import com.ibg.receipt.redis.service.RedisCallBack;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisManager {
    private JedisPool pool;

    public RedisManager(JedisPool pool) {
        this.pool = pool;
    }

    public <T> T request(RedisCallBack<T> callBack) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.connect();
            return callBack.doInRequest(jedis);
        } catch (JedisConnectionException e) {
            throw e;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }
}
