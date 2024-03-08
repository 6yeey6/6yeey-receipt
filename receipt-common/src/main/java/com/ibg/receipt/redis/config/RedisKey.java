package com.ibg.receipt.redis.config;

/**
 * 
 * redis key统一定义
 * @author: guojianchang
 * @date:   2018年11月12日 下午5:15:02   
 *
 */
public interface RedisKey {

    /** 统一前缀 */
    String PREFIX = "CORE-FUND-PLATFORM:";
    /** 新网回购中标记key */
    String XW_REPURCHASE_IN_PROGRESS = PREFIX + "XW:REPURCHASE";
}
