package com.ibg.receipt.config.refresh;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.ibg.receipt.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yushijun
 * @date 2020/4/7
 * @description 获取配置，并增加对应转化、默认、固定类型
 */
@Slf4j
@Component
public class ConfigStaticService {

    private static ConfigStaticService configStaticService;

    private Environment environment;

    @Autowired
    public ConfigStaticService(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initial() {
        configStaticService = this;
    }

    private static String getProperty(String key) {
        return configStaticService.environment.getProperty(key);

    }

    public static String getConfig(String key) {
        return getProperty(key);
    }

    public static Integer getConfigAsInteger(String key, Integer defaultValue) {
        try {
            String config = getConfig(key);
            return StringUtils.isBlank(config) ? defaultValue : Integer.valueOf(config);
        } catch (Exception e) {
            log.error("获取配置异常key:{}", key, e);
            return defaultValue;
        }
    }

    public static Integer getConfigAsInteger(String key) {
        try {

            return Integer.valueOf(getConfig(key));
        } catch (Exception e) {
            log.error("获取Environment配置异常：{}", e);
            return null;
        }
    }

    public static Long getConfigAsLong(String key, Long defaultValue) {
        try {
            String config = getConfig(key);
            return StringUtils.isBlank(config) ? defaultValue : Long.valueOf(config);
        } catch (Exception e) {
            log.error("获取配置异常key:{}",key, e);
            return defaultValue;
        }
    }

    public static Long getConfigAsLong(String key) {
        try {

            String config = getConfig(key);
            return StringUtils.isBlank(config) ? null : Long.valueOf(config);
        } catch (Exception e) {
            log.error("获取Environment配置异常：{}", e);
            return null;
        }
    }

    public static Boolean getConfigAsBoolean(String key, Boolean defaultValue) {
        try {
            String config = getConfig(key);
            return config != null ? Boolean.valueOf(config) : defaultValue;
        } catch (Exception e) {
            log.error("获取Environment配置异常：{}", e);
            return defaultValue;
        }
    }

    public static Boolean getConfigAsBoolean(String key) {
        try {
            return Boolean.valueOf(getConfig(key));
        } catch (Exception e) {
            log.error("获取Environment配置异常：{}", e);
            return null;
        }
    }

    public static <T> List<T> getConfigAsList(String key, Class<T> clazz) {
        try {
            return JSONArray.parseArray(getConfig(key)).toJavaList(clazz);
        } catch (Exception var4) {
            log.info("获取配置失败{}", key, var4);
            return null;
        }
    }

    public static <T> List<T> getConfigAsList(String key, Class<T> clazz, List<T> defaultList) {
        try {
            List<T> result = JSONArray.parseArray(getConfig(key)).toJavaList(clazz);
            return CollectionUtils.isEmpty(result) ? defaultList : result;
        } catch (Exception var5) {
            log.info("获取配置失败{}", key);
            return defaultList;
        }
    }

    public static <T> T getConfigAsObject(String key, Class<T> clazz) {
        try {
            return JSON.parseObject(getConfig(key), clazz);
        } catch (Exception var4) {
            log.info("获取配置失败{}", key, var4);
            return null;
        }
    }

    public static <T> T getConfigAsObjectWithGenerics(String key, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(getConfig(key), typeReference);
        } catch (Exception var4) {
            log.info("获取配置失败{}", key, var4);
            return null;
        }
    }

    public static String getConfigAsString(String key, String defaultValue) {
        try {
            String config = getConfig(key);
            return config != null ? config : defaultValue;
        } catch (Exception e) {
            log.error("获取Environment配置异常:", e);
            return defaultValue;
        }
    }

}
