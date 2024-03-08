package com.ibg.receipt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.StaticScriptSource;

@Configuration
public class DefaultRedisScriptConfig {

    @Bean(name = "defaultRateLimitLua")
    @ConditionalOnMissingBean(DefaultRedisScript.class)
    public DefaultRedisScript<Boolean> defaultRateLimitLua() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new StaticScriptSource("default"));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }
}
