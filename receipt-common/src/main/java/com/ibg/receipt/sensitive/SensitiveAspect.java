/**
 * Create time 2019-06-05 10:34
 * Create by wangkai kiilin@kiilin.com
 * Copyright 2019 kiilin http://www.kiilin.com
 */

package com.ibg.receipt.sensitive;


import com.ibg.receipt.sensitive.annotation.SensitiveSupport;
import com.ibg.receipt.sensitive.util.SpringContextUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 注解切面，处理当前请求是否需要脱敏 暂时不用启用
 */
@Aspect
@Component
public class SensitiveAspect {


    @Pointcut("@annotation(sensitive)||@within(sensitive)")
    public void pointcut(SensitiveSupport sensitive) {
    }


    @Before("pointcut(sensitive)")
    public void before(JoinPoint joinPoint, SensitiveSupport sensitive) throws Exception {

        // 获取请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HandlerExecutionChain handler;
        handler = SpringContextUtils.getBean(HandlerMapping.class).getHandler(request);
        HandlerMethod method = (HandlerMethod) handler.getHandler();
        // 获取controller方法上的注解
        SensitiveSupport sensitiveSupport = method.getMethodAnnotation(SensitiveSupport.class);
        if (sensitiveSupport == null) {
            sensitiveSupport = method.getBeanType().getAnnotation(SensitiveSupport.class);
        }
        // 放入标识
        request.setAttribute(SensitiveConstant.IS_SENSITIVE, sensitiveSupport.value());
    }

}

