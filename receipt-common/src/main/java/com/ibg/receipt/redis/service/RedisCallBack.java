package com.ibg.receipt.redis.service;

import redis.clients.jedis.Jedis;

public interface RedisCallBack<T> {

    T doInRequest(Jedis jedis);
}
