/**
 * Create time 2019-06-05 9:51
 * Create by wangkai kiilin@kiilin.com
 * Copyright 2019 kiilin http://www.kiilin.com
 */

package com.ibg.receipt.sensitive;


/**
 * 脱敏全局执行条件
 *
 */
public interface SensitiveExecute {

    /**
     * 全局配置 - 脱敏执行的先决条件
     *
     * @return
     */
    boolean execute();

}
