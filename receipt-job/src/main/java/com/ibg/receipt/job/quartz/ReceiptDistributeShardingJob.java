package com.ibg.receipt.job.quartz;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ibg.receipt.context.ContextContainer;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.base.BaseXxlJob;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.util.CollectionUtils;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yushijun
 * @de
 */
@Component
@Slf4j
@JobHandler("distributeShardingJob")
public class ReceiptDistributeShardingJob extends BaseXxlJob {
    @Autowired
    private JobService jobService;
    public static ThreadPoolExecutor poolExecutor;
    public static ThreadPoolExecutor auditPoolExecutor;
    public static ThreadPoolExecutor slowPoolExecutor;

    @PostConstruct
    public void initConstrusct() {
        poolExecutor = new ThreadPoolExecutor(20, 40, 600, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(10000, true),
            new ThreadFactoryBuilder().setNameFormat("pool-job-" + System.currentTimeMillis() + "-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());
        auditPoolExecutor = new ThreadPoolExecutor(5, 5, 600, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5000, true), new ThreadFactoryBuilder()
            .setNameFormat("audit-pool-job-" + System.currentTimeMillis() + "-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
        slowPoolExecutor = new ThreadPoolExecutor(20, 20, 600, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(10000, true), new ThreadFactoryBuilder()
            .setNameFormat("slow-pool-job-" + System.currentTimeMillis() + "-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void run(String param) {
        log.info("sharding info:{}", JSON.toJSONString(ShardingUtil.getShardingVo()));

        try {
            log.info("开始分配任务.");
            distributeJob();
            log.info("分配任务结束.");
        } catch (Exception e) {
            log.error("分配任务出错.", e);
        }

    }

    private Job dealingJob(Job job) {
        job.setJobStatus(JobStatus.DEALING.getStatus());
        jobService.saveNotUpdateExecuteTimes(job);
        return jobService.load(job.getId());
    }

    public void distributeJob() {
        if (poolExecutor.isShutdown()) {
            return;
        }
        int pageSize = 50;
        Long lastId = 0L;
        while (true) {
            try {
                List<Job> pageInfo = jobService.getToDealJobList(pageSize, lastId,
                    ShardingUtil.getShardingVo().getTotal(), ShardingUtil.getShardingVo().getIndex());
                if (CollectionUtils.isNotEmpty(pageInfo)) {
                    for (Job job : pageInfo) {
                        lastId = job.getId();
                        Date now = new Date();
                        Date startDate = job.getJobStartTime();
                        JobMachineStatus jobMachineStatus = JobMachineStatus.getEnum(job.getMachineStatus());
                        if (startDate != null && now.before(startDate)
                            && JobMachineStatus.APPL_AUDIT != jobMachineStatus) {
                            continue;
                        }
                        distributeJobToDeal(job);
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                log.error("获取任务列表失败.", e);
                break;
            }
        }
    }

    private void distributeJobToDeal(Job job) {

        if (poolExecutor.isShutdown()) {
            return;
        }

        try {
            JobMachineStatus jobMachineStatus = JobMachineStatus.getEnum(job.getMachineStatus());
            if (jobMachineStatus == null) {
                log.error("job枚举没找到, 跳过执行. jobId:{}", job.getId());
                job.setJobStatus(JobStatus.NOTICE_MANUAL.getStatus());
                job.setLastError("job枚举没找到, 跳过执行.");
                jobService.saveNotUpdateExecuteTimes(job);
                return;
            }
            Job dealingJob = dealingJob(job);
            BaseHandler handler = (BaseHandler) ContextContainer.getAc().getBean(jobMachineStatus.getHandler());
            Runnable task = () -> {
                handler.getJob().set(dealingJob);
                handler.run();
            };
            if (JobMachineStatus.APPL_AUDIT == jobMachineStatus) {
                auditPoolExecutor.execute(task);
            } else if (Objects.nonNull(dealingJob.getExecuteTimes()) && dealingJob.getExecuteTimes() > 100) {
                slowPoolExecutor.execute(task);
            } else {
                poolExecutor.execute(task);
            }

        } catch (Exception e) {
            log.error("分发job失败. jobId:{}", job.getId(), e);
        }
    }
}
