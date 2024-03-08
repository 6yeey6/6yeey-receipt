package com.ibg.receipt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class JacksonUtil {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转为json字符串
     *
     * @param obj
     * @return
     */
    public static String writeValue(Object obj) {
        try {
            MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("json对象转换字符串错误", e);
        }
        return null;
    }

    /**
     * 将json字符串转为对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("字符串转json对象错误", e);
        }
        return null;
    }
}
