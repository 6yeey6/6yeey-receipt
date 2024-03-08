package com.ibg.receipt.api.interceptor;

import com.alibaba.fastjson.JSON;
import com.ibg.receipt.api.interceptor.annotation.PreventDupSubmit;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.util.MD5Utils;
import lombok.extern.slf4j.Slf4j;
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

import java.lang.reflect.Method;

/**
 * 防重复提交
 */
@Component
@Aspect
@Slf4j
public class PreventDupSubmitAspect {
    private static final String PREFIX = "CORE_FUND:PREVENT_DUP_SUBMIT:";
    private static ExpressionParser parser = new SpelExpressionParser();
    private static LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    @Autowired
    private RedisService redisService;

    @Around(value = "@annotation(anno)")
    public Object process(final ProceedingJoinPoint joinPoint, final PreventDupSubmit anno) throws Throwable {
        log.info("JoinPoint: " + joinPoint);
        final Object[] args = joinPoint.getArgs();
        final String signature = joinPoint.getSignature().toLongString();
        String preventKey = anno.key();
        if (StringUtils.isBlank(preventKey)) {
            preventKey = MD5Utils.md5Str(JSON.toJSONString(args));
        } else if (preventKey.startsWith("#")) {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            EvaluationContext context = new StandardEvaluationContext();
            String[] params = discoverer.getParameterNames(method);
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], args[len]);
            }
            preventKey = parseSpel(context, preventKey);
            // el表达式解析错误的时候，为了防止阻塞正常提交，使用入参的json串md5作为key
            if (StringUtils.isBlank(preventKey)) {
                preventKey = MD5Utils.md5Str(JSON.toJSONString(args));
            }
        }
        String key = PreventDupSubmitAspect.PREFIX + signature + ":" + preventKey;

        if (!redisService.acquireLockWithTTL(key, anno.intervalSec())) {
            log.warn("防重复提交:{}, key:{}, expire: {} seconds", new Object[] { signature, key, anno.intervalSec() });
            throw new ServiceException(CodeConstants.C_10101011.getCode(),"请求已在处理中，请勿重复提交！");
        }
        // executing
        try {
            return joinPoint.proceed(args);
        } catch (Exception e) {
            throw e;
        }finally {
            this.redisService.del(key);
        }
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
            if (value != null) {
                if (value.getClass().isAssignableFrom(Number.class)
                        || value.getClass().isAssignableFrom(String.class)) {
                    return String.valueOf(value);
                }
            }
        } catch (ParseException e) {
            // just ignore
            log.warn("parse spel {} error.", key, e);
        }
        return value == null ? null : JSON.toJSONString(value);
    }

}
