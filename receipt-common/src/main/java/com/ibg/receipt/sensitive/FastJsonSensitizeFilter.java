package com.ibg.receipt.sensitive;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.ibg.receipt.sensitive.annotation.SensitiveInfo;
import com.ibg.receipt.sensitive.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;


/**
 * Description: fastJson脱敏，依赖spring ，为切片时用，有开关控制
 *
 * @author lining
 * @date 20-7-21
 */
@Slf4j
public class FastJsonSensitizeFilter implements ValueFilter {

    public static final String CALL_SOURCE = "call-source";

    @Override
    public Object process(Object object, String name, Object value) {
        if (null == value || !(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }
        // 读取全局执行接口
        SensitiveExecute bean = SpringContextUtils.getBean(SensitiveExecute.class);
        // 未实现接口 默认开启脱敏
        boolean execute = bean == null || (null != bean && bean.execute());

        // 读取当前请求是否需要脱敏
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object isSensitiveValue = request.getAttribute(SensitiveConstant.IS_SENSITIVE);
        String callSource = request.getHeader(CALL_SOURCE);
        boolean isSensitive = isSensitiveValue != null && (boolean) isSensitiveValue;

        if (execute && isSensitive) {
            Class<?> clazz = object.getClass();
            Field field = null;
            while (clazz != null && !clazz.equals(Object.class)) {
                try {
                    field = clazz.getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                }
                if (field != null) {
                    break;
                }
                clazz = clazz.getSuperclass();
            }
            SensitiveInfo desensitization = null;
            if (field != null && (String.class != field.getType() || (desensitization = field.getAnnotation(SensitiveInfo.class)) == null)) {
                return value;
            }
            String valueStr = (String) value;
            if (desensitization == null) {
                return value;
            }
            SensitiveType type = desensitization.value();
            valueStr = SensitiveType.sensitiveConvert(type, valueStr);

            return valueStr;
        }
        return value;

    }
}

