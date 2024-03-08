package com.ibg.receipt.job.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ibg.receipt.context.ContextContainer;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.base.BaseJob;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@DisallowConcurrentExecution
@Data
@Slf4j
public class DistributeJobController extends BaseJob {
    @Autowired
    private JobService jobService;
    public static ThreadPoolExecutor poolExecutor;
    public static ThreadPoolExecutor auditPoolExecutor;

    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(20, 40, 600, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10000, true),
                new ThreadFactoryBuilder().setNameFormat("pool-job-" + System.currentTimeMillis() + "-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        auditPoolExecutor = new ThreadPoolExecutor(10, 10, 600, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(5000, true), new ThreadFactoryBuilder()
                        .setNameFormat("audit-pool-job-" + System.currentTimeMillis() + "-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void run() {
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
        int pageNo = 1;
        int pageSize = 50;
        Long lastId = 0L;
        while (true) {
            try {
                Page<Job> pageInfo = jobService.getToDealJobList(pageNo, pageSize, lastId);
                List<Job> jobList = pageInfo.getContent();
                if (pageInfo.getTotalElements() > 0L && !CollectionUtils.isEmpty(jobList)) {
                    for (Job job : jobList) {
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
            } else {
                poolExecutor.execute(task); 
            }
            
        } catch (Exception e) {
            log.error("分发job失败. jobId:{}", job.getId(), e);
        }
    }
}
