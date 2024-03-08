package com.ibg.receipt.redis.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

//@Configuration
public class RedisClusterConfig {
    
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNode;
    
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
    
    @Bean
    public JedisCluster cluster(JedisPoolConfig poolConfig) {
        Set<HostAndPort> nodes = new HashSet<>();
        for (String n : clusterNode.split(",")) {
            String[] arr = n.split(":");
            String host = arr[0];
            int port = Integer.valueOf(arr[1]);
            HostAndPort node = new HostAndPort(host, port);
            nodes.add(node);
        }
        return new JedisCluster(nodes, poolConfig);
    }
}
