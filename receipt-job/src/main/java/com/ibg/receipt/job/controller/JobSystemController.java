package com.ibg.receipt.job.controller;

import com.ibg.receipt.base.vo.JsonResultVo;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobSystemController {
    @Autowired
    private Environment environment;

    @GetMapping("/offLine")
    public JsonResultVo<?> taskOffLine() {
        String appName = null;
        appName = environment.getProperty("spring.application.name");
        long downCost = System.currentTimeMillis();
        try {
            log.info("job服务-xxljob注销开始:{}", appName);
            ExecutorRegistryThread.getInstance().toStop();
            log.info("job服务-xxljob注销结束:{}", appName);
        } catch (Exception e) {
            log.error("job服务-xxljob注销异常:{}", appName, e);
        } finally {
            log.info("job服务-xxljob注销耗时统计{},开始:{},结束{}", System.currentTimeMillis() - downCost, downCost,
                    System.currentTimeMillis());
        }
        return JsonResultVo.success();
    }
}
