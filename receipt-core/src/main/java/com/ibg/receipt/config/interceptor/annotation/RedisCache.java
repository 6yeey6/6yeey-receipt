package com.ibg.receipt.config.interceptor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ibg.receipt.enums.operate.RedisCacheType;

/***
 * @Description: 缓存标签
 * @author: wenjianye
 * @date: 2021年3月2日 上午10:36:47
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {

    /** 缓存的秒数   */
    int cacheSecond();

    /** redis的key */
    String cacheKey();
    
    RedisCacheType redisCacheType();

}
