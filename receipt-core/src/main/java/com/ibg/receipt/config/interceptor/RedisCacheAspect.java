package com.ibg.receipt.config.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibg.receipt.config.interceptor.annotation.RedisCache;
import com.ibg.receipt.constants.RedisConstants;
import com.ibg.receipt.redis.service.RedisService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yushijun
 * @date 2019/1/18
 * @description
 */
@Slf4j
@Component
@Aspect
public class RedisCacheAspect {

    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    private final ExpressionParser parser = new SpelExpressionParser();

    private final ObjectMapper objectMapper = new ObjectMapper() {
        {
            setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
    };

    @Autowired
    private RedisService redisService;

    @Around("@annotation(redisCache)")
    public Object around(ProceedingJoinPoint joinPoint, RedisCache redisCache) throws Throwable {
        EvaluationContext context = new StandardEvaluationContext();
        String queryKey = "";
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            String[] params = discoverer.getParameterNames(method);
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], joinPoint.getArgs()[len]);
            }

            String value = redisCache.cacheKey();
            if (value.startsWith("#")) {
                value = parseSpel(context, redisCache.cacheKey());
            }
            if (StringUtils.isNotBlank(value) && !"NULL".equals(value)) {
                queryKey = MessageFormat.format(RedisConstants.REDIS_CACHE_ASPECT_KEY, redisCache.redisCacheType().name(), value);
                if (StringUtils.isNotBlank(queryKey)) {
                    String redis = redisService.get(queryKey);
                    if (StringUtils.isNotBlank(redis)) {
                        Type returnType = method.getGenericReturnType();
                        log.info("缓存切面执行完毕,key:{},获取到的数据{}", queryKey, redis);
                        return objectMapper.readValue(redis, objectMapper.constructType(returnType));
                    }
                }
            }
        } catch (Throwable e) {
            log.error("缓存切面出现异常{},redisCache:{}", joinPoint, redisCache, e);
        }

        //执行目标方法
        Object result = joinPoint.proceed();
        if (StringUtils.isNotBlank(queryKey)) {
            try {
                redisService.setex(queryKey, redisCache.cacheSecond(), objectMapper.writeValueAsString(result));
            } catch (Throwable e) {
                log.error("缓存切面操作redis出现异常", e);
            }
        }
        return result;
    }

    /**
     * 解析SPEL表达式
     *
     * @param context
     * @param key
     * @return
     */
    private String parseSpel(EvaluationContext context, String key) {
        Object value = null;
        try {
            Expression expression = parser.parseExpression(key);
            value = expression.getValue(context, Object.class);
        } catch (ParseException e) {
            // just ignore
            log.warn("parse spel {} error.", key, e);
        }
        return value == null ? "NULL" : value.toString();
    }

}
