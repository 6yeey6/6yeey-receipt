package com.ibg.receipt.redis.service.impl;

import com.ibg.receipt.redis.service.RedisClusterCallBack;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisException;

public class RedisClusterManager {
    private JedisCluster cluster;

    public RedisClusterManager(JedisCluster cluster) {
        this.cluster = cluster;
    }

    public <T> T request(RedisClusterCallBack<T> callBack) {
        try {
            return callBack.doInRequest(cluster);
        } catch (JedisException e) {
            throw e;
        }
    }
}
