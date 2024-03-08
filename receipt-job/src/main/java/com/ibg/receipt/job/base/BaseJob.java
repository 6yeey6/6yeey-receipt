package com.ibg.receipt.job.base;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ibg.receipt.context.ContextContainer;
import com.ibg.receipt.job.util.JobUtils;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.util.UniqueKeyUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public abstract class BaseJob implements Job {
    private ThreadLocal<String> dataParam = new ThreadLocal<String>();

    //锁key集合
    private static final ConcurrentMap<String, String> LOCK_MAP = new ConcurrentHashMap<>();
    private static volatile boolean isStopping = false;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (isStopping) {
            log.warn("应用正在停止...");
            return;
        }
        // redis
        RedisService redisService = ContextContainer.getAc().getBean(RedisService.class);
        // lock
        String classSimpleName = JobUtils.getClassSimpleName(getClass());
        String lock = JobUtils.getJobRunningKey(classSimpleName);
        // get lock
        if (!redisService.acquireLock(lock, JobUtils.JOB_RUNNING_KEY_EXPIRED)) {
            log.info("Job：{}，上次运行未结束，此次退出", classSimpleName);
            return;
        }
        LOCK_MAP.putIfAbsent(lock, "");
        String jobUniqueKey = UniqueKeyUtils.uniqueKey();
        try {
            log.info("Job：{}, UniqueKey：{}， 开始运行", classSimpleName, jobUniqueKey);

            if (context.getMergedJobDataMap() != null && StringUtils.isNotBlank(context.getMergedJobDataMap().getString("jobDataParam"))) {
                this.setDataParam(context.getMergedJobDataMap().getString("jobDataParam"));
            } else {
                this.setDataParam(null);
            }
            this.run();
        } finally {
            // release lock
            redisService.releaseLock(lock);
            LOCK_MAP.remove(lock);
        }
        log.info("Job：{}, UniqueKey：{}， 运行结束", getClass().getSimpleName(), jobUniqueKey);
    }

    public abstract void run();

    public String getDataParam() {
        return dataParam.get();
    }

    public void setDataParam(String dataParam) {
        this.dataParam.set(dataParam);
    }

    public static void releaseAllLock() {
        isStopping = true;
        log.info("释放所有未释放的锁：{}", LOCK_MAP.keySet());
        RedisService redisService = ContextContainer.getAc().getBean(RedisService.class);
        LOCK_MAP.keySet().forEach(lock -> redisService.releaseLock(lock));
        log.info("释放所有未释放的锁完成");
    }
}
