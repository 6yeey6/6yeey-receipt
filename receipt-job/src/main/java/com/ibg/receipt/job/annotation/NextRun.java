package com.ibg.receipt.job.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ibg.receipt.job.base.BaseJob;

/**
 * @author yushijun
 * @date 2019/1/7
 * @description 下个节点间唤起注解，只有正常return才会执行，
 * @notice1 不能循环使用会导致死循环 A->B + B->A
 * @notice2 controller需要做到幂等性
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NextRun {

    Class<? extends BaseJob>[] next();

}
