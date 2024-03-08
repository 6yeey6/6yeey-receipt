package com.ibg.receipt.redis.service;

/**
 * @desc: 请求限流service
 * @author: lvzhonglin
 * @date: 2022/1/12 11:45
 */
public interface RequestLimitService {

    /**
     * 请求限流判断，eg：需要限制1分钟内最大请求30次，则limit=30，second=60
     * @param key    业务标识key
     * @param limit  指定时间（秒）内最大允许请求次数，默认5
     * @param second 限流控制时间,单位：秒
     * @return true=禁止访问，false=允许访问
     */
    boolean isLimit(String key, int limit, int second);
}
