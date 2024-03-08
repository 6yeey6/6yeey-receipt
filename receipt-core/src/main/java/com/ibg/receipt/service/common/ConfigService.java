package com.ibg.receipt.service.common;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.ibg.receipt.config.refresh.ConfigStaticService;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.StringUtils;

/**
 * @author yushijun
 * @date 2019/3/20
 * @description
 */
@Service
public class ConfigService {

    /**
     * 配置值为数组!!! 判断配置和目标值是否一致 targetKey.contain(configKeys)
     *
     * @param configKey
     * @param targetKey
     * @return
     */
    public boolean hitKey(String configKey, String targetKey) {
        if (StringUtils.isBlank(targetKey)) {
            return false;
        }
        List<String> config = ConfigStaticService.getConfigAsList(configKey, String.class,
                Lists.newArrayList());

        return config.stream().anyMatch(s -> targetKey.contains(s));
    }

    /**
     * 配置值为数组!!! 判断配置和目标值是否一致 targetKey.contain(configKeys)
     *
     * @param configKey
     * @param targetKey
     * @return
     */
    public boolean hitKeyNew(String configKey, String targetKey) {
        if (StringUtils.isBlank(targetKey)) {
            return false;
        }
        List<String> config = ConfigStaticService.getConfigAsList(configKey, String.class,
                Lists.newArrayList());

        return config.stream().anyMatch(s -> targetKey.equals(s));
    }

    /**
     * 配置值为数组!!! 判断配置和目标值是否一致--并且
     *
     * @param configKey
     * @param targetKeys
     * @return
     */
    public boolean hitConfigKeys(String configKey, List<String> targetKeys) {
        if (CollectionUtils.isEmpty(targetKeys)) {
            return false;
        }

        List<String> config = ConfigStaticService.getConfigAsList(configKey, String.class,
                Lists.newArrayList());
        boolean result = false;

        if (CollectionUtils.isNotEmpty(targetKeys)) {
            return targetKeys.stream().allMatch(s -> config.contains(s));
        }
        return result;
    }

    /**
     * 配置值为数组!!! 判断配置和目标值是否一致--或
     *
     * @param configKey
     * @param keys
     * @return
     */
    public boolean hitConfigKeysOr(String configKey, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return false;
        }

        List<String> config = ConfigStaticService.getConfigAsList(configKey, String.class,
                Lists.newArrayList());
        boolean result = false;

        if (CollectionUtils.isNotEmpty(keys)) {
            return keys.stream().anyMatch(s -> config.contains(s));
        }
        return result;
    }

}
