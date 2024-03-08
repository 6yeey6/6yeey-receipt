package com.ibg.receipt.sensitive;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.ibg.receipt.sensitive.annotation.SensitiveInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;


/**
 * Description: fastJson脱敏,为工具类所用（不需要有开关控制）
 *
 * @author lining
 * @date 20-7-21
 */
@Slf4j
public class FastJsonSensitizeForUtilFilter implements ValueFilter {


    @Override
    public Object process(Object object, String name, Object value) {
        if (null == value || !(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }
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
        //脱敏*
        SensitiveType type = desensitization.value();
        valueStr = SensitiveType.sensitiveConvert(type, valueStr);
        return valueStr;

    }
}

