package com.ibg.receipt.job.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.ibg.receipt.context.ContextContainer;
import com.ibg.receipt.job.annotation.NextRun;
import com.ibg.receipt.job.base.BaseJob;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.NoticeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 唤起下个节点
 *
 * @author yushijun
 * @date 2018/5/9
 */
@Aspect
@Component
@Slf4j
public class NextRunAspect {

    public static ThreadPoolExecutor poolExecutor;

    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(2, 2, 600, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000, true),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 正常返回AOP处理
     *
     * @param joinPoint
     */
    @AfterReturning(value = "pointCut()")
    public void afterReturning(JoinPoint joinPoint) {

        try {
            // 获取切面类
            Class<?> targetClass = joinPoint.getTarget().getClass();
            // 获取切面方法Signature
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method currentMethod = methodSignature.getMethod();
            log.info("当前任务class:{},method:{}执行完毕", targetClass.getSimpleName(), currentMethod.getName());

            // 获取当前AOP切面方法
            Method targetClassMethod = Arrays.stream(targetClass.getMethods())
                    .filter(method -> method.equals(currentMethod)).findFirst().get();

            // 获取 方法上的注解
            NextRun next = targetClassMethod.getAnnotation(NextRun.class);
            List<Class<?>> nextExcute = Arrays.asList(next.next());

            if (CollectionUtils.isNotEmpty(nextExcute)) {
                nextExcute.forEach(baseJob -> {
                    log.info("当前任务执行完毕:class:{},method:{},开始执行下个任务：class:{},method:{}", targetClass.getSimpleName(),
                            currentMethod.getName(), baseJob.getSimpleName(), "run");
                    BaseJob job = (BaseJob) ContextContainer.getBean(baseJob);
                    poolExecutor.execute(job::run);
                });
                log.info("正常执行-:class:{},method:{}", targetClass.getName(), currentMethod.getName());
            }
        } catch (Exception e) {
            log.error("执行下个节点异常--异常Exception", e);
            NoticeUtils.businessError("执行下个节点异常\nexception:" + e.getMessage());
        }

    }

    @Pointcut("@annotation( com.ibg.receipt.job.annotation.NextRun)")
    public void pointCut() {
    }

}
