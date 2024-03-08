package com.ibg.receipt.job.base;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;

import com.google.common.base.Stopwatch;
import com.ibg.receipt.context.ContextContainer;
import com.ibg.receipt.job.util.JobUtils;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.util.NoticeUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.util.ShardingUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yushijun
 * @date 2020/6/4
 * @description
 */
@Slf4j
public abstract class BaseXxlJob extends IJobHandler {

    @Autowired
    private Tracer tracer;


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        Stopwatch started = Stopwatch.createStarted();
        String classSimpleName = JobUtils.getClassSimpleName(getClass());
        String lock = JobUtils.getJobRunningKey(classSimpleName) + ShardingUtil.getShardingVo().getIndex();
        tracer.createSpan(classSimpleName);
        log.info("开始执行任务：{},lockKey:{}", classSimpleName, lock);
        // 获取锁
        RedisService redisService = ContextContainer.getAc().getBean(RedisService.class);

        try {
            // get lock
            if (!redisService.acquireLockWithTTL(lock, JobUtils.JOB_RUNNING_KEY_EXPIRED_INT)) {
                log.info("Job：{}，上次运行未结束，此次退出,redis:{}", classSimpleName,lock);
                ReturnT<String> fail = ReturnT.FAIL;
                fail.setMsg("上次运行未结束，此次退出Job：" + classSimpleName);
//                redisService.releaseLockWithTTL(lock);
                return fail;
            }
            run(param);
            log.info("任务执行结束：{}", classSimpleName);
        } catch (Exception e) {
            log.error("任务执行异常：", e);
            NoticeUtils.businessError("任务执行异常");
            ReturnT<String> fail = ReturnT.FAIL;
            fail.setMsg("上次运行未结束，此次退出Job：" + classSimpleName + ":" + e.getMessage());
            return fail;
        } finally {
            log.info("调度任务释放锁开始:{}", classSimpleName);
            Long del = redisService.del(lock);
            long cost = started.stop().elapsed(TimeUnit.MILLISECONDS);
            log.info("调度任务释放锁结束:{},{},任务执行耗时:{}ms", classSimpleName, del, cost);
            tracer.close(tracer.getCurrentSpan());
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 子类实现
     */
    public abstract void run(String param);

}
