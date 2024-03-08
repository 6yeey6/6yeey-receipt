package com.ibg.receipt.redis.service.impl;

import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.redis.service.RequestLimitService;
import com.ibg.receipt.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @desc: 请求限流service
 * @author: lvzhonglin
 * @date: 2022/1/12 11:46
 */
@Slf4j
@Service
public class RequestLimitServiceImpl implements RequestLimitService {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean isLimit(String redisKey, int limitThreshold, int limitSeconds) {
        int default_limit = 30;
        int default_second = 60;
        limitThreshold = limitThreshold == 0 ? default_limit : limitThreshold;
        limitSeconds = limitSeconds == 0 ? default_second : limitSeconds;

        try {
            if (!redisService.exists(redisKey) || StringUtils.isBlank(redisService.get(redisKey))) {
                redisService.acquireLockWithTTL(redisKey, "0", limitSeconds);
            }
            String value = redisService.get(redisKey);
            if (Long.parseLong(value) > limitThreshold) {
                return true;
            }
            return redisService.incr(redisKey) > limitThreshold;
        }catch (Exception e){
            log.warn("限流执行异常：", e);
            return false;
        }
    }
}
