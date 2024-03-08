package com.ibg.receipt.cache;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;

public class RedisCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {

    @Value("#{${cache.cacheExpireMap}}")
    private Map<String, Long> cacheExpireMap;
    @Value("${cache.defaultExpiration}")
    private Long defaultExpiration;
	@Override
	public void customize(RedisCacheManager cacheManager) {
		// 默认过期时间，单位秒
		cacheManager.setDefaultExpiration(defaultExpiration);
		cacheManager.setExpires(cacheExpireMap);
	}

}