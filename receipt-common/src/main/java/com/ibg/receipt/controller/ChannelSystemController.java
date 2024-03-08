package com.ibg.receipt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibg.receipt.base.vo.JsonResultVo;
import com.netflix.discovery.DiscoveryManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/channel")
public class ChannelSystemController {
    @Autowired
    private Environment environment;

    @GetMapping("/offLine")
    public JsonResultVo<?> offLine() {
        String appName = null;
        long downCost = System.currentTimeMillis();
        try {
            appName = environment.getProperty("spring.application.name");
            log.info("服务下线开始:{}", appName);
            DiscoveryManager.getInstance().shutdownComponent();
            log.info("服务下线结束:{}", appName);
            return JsonResultVo.success();
        } catch (Exception e) {
            log.error("服务下线异常:{}", appName, e);
            return JsonResultVo.error("-1", "服务下线异常");
        } finally {
            log.info("服务下线耗时统计{},开始:{},结束{}", System.currentTimeMillis() - downCost, downCost,
                    System.currentTimeMillis());
        }
    }
}
