package com.ibg.receipt.api.interceptor;

import com.google.common.base.Stopwatch;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.util.JacksonUtil;
import com.ibg.receipt.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * api拦截打印日志及执行时间
 */
@Order
@Slf4j
@Aspect
@Component
public class ApiAspect {

    @Around("controllerMethod()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        if (null == args || args.length != 1) {
            return joinPoint.proceed();
        }
        Object obj = joinPoint.getArgs()[0];

        RequestMapping classRequestMapping = joinPoint.getTarget().getClass().getAnnotation(RequestMapping.class);

        String classPath = "";
        if (null != classRequestMapping) {
            classPath = classRequestMapping.value()[0];
        }
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        RequestMapping methodRequestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        String methodPath = methodRequestMapping.value()[0];

        String path = classPath + methodPath;

        try {
            Stopwatch watch = Stopwatch.createStarted();
            //请求报文过滤
            String resResult = JacksonUtil.writeValue(obj);
            log.info("请求url:{}开始，请求参数：{}", path, resResult);
            Object result = joinPoint.proceed();
            long time = watch.elapsed(TimeUnit.MILLISECONDS);
            //响应报文过滤
            String respResult;
            //TODO 响应报文加密
            if (path.endsWith("/list") || path.endsWith("/loanDetail")) {
                respResult = JsonUtils.getDecodedResult(result);
            } else {
                respResult = JacksonUtil.writeValue(result);
            }
            log.info("请求url:{}结束，响应结果：{}, 用时：{}ms", path, respResult, time);
            return result;
        } catch (ServiceException e) {
            return JsonResultVo.error(e.getCode(), e.getMessage());
        }
    }

    @Pointcut("execution(* com.ibg.receipt.api.controller..*.*(..))")
    public void controllerMethod() {

    }
}
