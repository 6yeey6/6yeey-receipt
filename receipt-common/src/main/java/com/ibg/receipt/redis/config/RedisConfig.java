package com.ibg.receipt.redis.config;

import com.ibg.receipt.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Value("${redis.master.host}")
    private String masterHost;

    @Value("${redis.master.port}")
    private int masterPort;

    @Value("${redis.master.password}")
    private String masterPassword;

    @Value("${redis.slave.host}")
    private String slaveHost;

    @Value("${redis.slave.port}")
    private int slavePort;

    @Value("${redis.slave.password}")
    private String slavePassword;

    @Value("${redis.timeout}")
    private int timeout;

    @Value("${redis.pool.max-total}")
    private int maxTotal;

    @Value("${redis.pool.max-idle}")
    private int maxIdle;

    @Value("${redis.pool.max-wait}")
    private long maxWait;

    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(this.maxTotal);
        jedisPoolConfig.setMaxIdle(this.maxIdle);
        jedisPoolConfig.setMaxWaitMillis(this.maxWait);
        jedisPoolConfig.setTestOnBorrow(true);
        return jedisPoolConfig;
    }

    @Bean(name = "rJedisPool")
    public JedisPool rJedisPool() {
        return new JedisPool(jedisPoolConfig(), this.slaveHost, this.slavePort, timeout,
                StringUtils.defaultIfBlank(this.slavePassword, null));
    }

    @Bean(name = "wJedisPool")
    public JedisPool wJedisPool() {
        return new JedisPool(jedisPoolConfig(), this.masterHost, this.masterPort, timeout,
                StringUtils.defaultIfBlank(this.masterPassword, null));
    }

    @Bean(name = "redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig());
        redisConnectionFactory.setHostName(this.masterHost);
        redisConnectionFactory.setPort(this.masterPort);
        redisConnectionFactory.setTimeout(timeout);
        redisConnectionFactory.setPassword(StringUtils.defaultIfBlank(this.masterPassword, null));
        return redisConnectionFactory;
    }


}
