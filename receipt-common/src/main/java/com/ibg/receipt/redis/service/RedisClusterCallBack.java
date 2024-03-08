package com.ibg.receipt.redis.service;

import redis.clients.jedis.JedisCluster;

public interface RedisClusterCallBack<T> {

    T doInRequest(JedisCluster cluster);
}
