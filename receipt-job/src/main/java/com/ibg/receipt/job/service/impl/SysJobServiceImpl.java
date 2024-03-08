package com.ibg.receipt.job.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;

import org.apache.commons.collections.MapUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import com.google.common.collect.Sets;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ExceptionUtils;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.job.base.BaseJob;
import com.ibg.receipt.job.constants.ParamType;
import com.ibg.receipt.job.constants.TaskStatus;
import com.ibg.receipt.job.constants.TaskType;
import com.ibg.receipt.job.model.TaskTimer;
import com.ibg.receipt.job.service.SysJobService;
import com.ibg.receipt.job.service.TaskTimerParamParser;
import com.ibg.receipt.job.service.TaskTimerParamService;
import com.ibg.receipt.job.service.TaskTimerService;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.NoticeUtils;

import lombok.extern.slf4j.Slf4j;

@Lazy
@Slf4j
@Service
@Transactional
//本地调试吧下面的注解去掉
@ConditionalOnExpression("!'${os.name}'.startsWith('W') && !'${os.name}'.startsWith('w') && !'${os.name}'.startsWith('m') && !'${os.name}'.startsWith('M')")
public class SysJobServiceImpl implements SysJobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskTimerService taskTimerService;
    @Autowired
    private TaskTimerParamService taskTimerParamService;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init(){
        log.info("JobService initing...");
        // 启动所有Job
        try {
            runAllJob();
        } catch (Exception e) {
            NoticeUtils.businessError("【严重】\n 资金平台job启动失败：\n" + e);
            throw new RuntimeException(e);
        }
        log.info("JobService inited.");
    }

    @Override
    public void runJobById(Long id) throws Exception {
        runJob(taskTimerService.load(id));
    }

    @Override
    public void runJobByTaskClass(String taskClass) throws Exception {
        runJob(taskTimerService.loadByTaskClass(taskClass));
    }

    @Override
    public void runAllJob() {
        List<TaskTimer> taskTimers = taskTimerService.findAll();
        if (CollectionUtils.isEmpty(taskTimers)) {
            return;
        }

        // 删除不再使用的job（数据库中记录直接删除）
        deleteUnusingJobs(taskTimers);

        for (TaskTimer taskTimer : taskTimers) {
            if (taskTimer.getTaskStatus() == TaskStatus.RUNNING) {
                try {
                    runJob(taskTimer);
                } catch (Exception e) {
                    log.error("启动任务{}出错", taskTimer.getTaskClass(), e);
                    throw ExceptionUtils.commonError("启动任务报错:" + taskTimer.getTaskClass());
                }
            } else {
                // 如果非运行的则删除
                JobKey jobKey = JobKey.jobKey(taskTimer.getTaskClass(), Scheduler.DEFAULT_GROUP);
                deleteJob(jobKey);
            }
        }
    }

    /**
     * 找出调度器跟数据库中的job差集删除
     *
     * @param taskTimers
     */
    private void deleteUnusingJobs(List<TaskTimer> taskTimers) {
        try {
            Set<JobKey> exsitJobs = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP));
            Set<JobKey> processingJobs = taskTimers.stream()
                    .map(t -> JobKey.jobKey(t.getTaskClass(), Scheduler.DEFAULT_GROUP)).collect(Collectors.toSet());
            Sets.difference(exsitJobs, processingJobs).forEach(job -> {
                deleteJob(job);
            });
        } catch (SchedulerException e) {
            log.error("error when deleting the unusing jobs", e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * 从scheduler中删除job
     *
     * @param jobKey
     */
    private void deleteJob(JobKey jobKey) {
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.info("deleted job: {}", jobKey);
            }
        } catch (SchedulerException e) {
            log.error("error when delete job ：{}", jobKey.getName(), e);
            throw new RuntimeException(e);
        }
    }

    private void runJob(TaskTimer taskTimer) throws Exception {
        Assert.notNull(taskTimer, "任务定时器");

        log.info("启动任务：{}", taskTimer.getTaskName());

        Map<ParamType, String> paramMapping = taskTimerParamService.findParamMapping(taskTimer.getId());
        if (MapUtils.isEmpty(paramMapping)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "任务定时器参数");
        }

        log.info("任务参数：{}", paramMapping);

        if (!applicationContext.containsBean(taskTimer.getTaskClass())) {
            log.error("任务【{}】对应的处理类【{}】不存在", taskTimer.getTaskName(), taskTimer.getTaskClass());
            throw ExceptionUtils.commonError("未找到对应的任务实例:" + taskTimer.getTaskClass());
        }

        JobDetail jobDetail = buildJobDetail(taskTimer);

        List<Trigger> triggers = getParser(taskTimer.getTaskType()).parse(taskTimer.getTaskClass(), paramMapping);
        if (CollectionUtils.isEmpty(triggers)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "任务定时器触发器");
        }

        for (Trigger trigger : triggers) {
            createOrUpdateJob(jobDetail, trigger);
        }
    }

    private JobDetail buildJobDetail(TaskTimer taskTimer) {
        BaseJob job = (BaseJob) applicationContext.getBean(taskTimer.getTaskClass());
        // 由于quartz内部会检查job类是否存在，对于被cglib代理的类每次启动时都会变化，需要提取其原始类
        Class<? extends Job> jobClass = (Class<? extends Job>) ClassUtils.getUserClass(job);
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(taskTimer.getTaskClass(), Scheduler.DEFAULT_GROUP).build();
        return jobDetail;
    }


    private void createOrUpdateJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        JobKey jobKey = jobDetail.getKey();
        if (!scheduler.checkExists(jobKey)) {
            // if the job doesn't already exist, we can create it, along with its trigger.
            // this prevents us
            // from creating multiple instances of the same job when running in a clustered
            // environment
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("SCHEDULED JOB WITH KEY " + jobKey.toString());
        } else {
            // if the job has exactly one trigger, we can just reschedule it, which allows
            // us to update the schedule for
            // that trigger.
            try {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                if (triggers.size() == 1) {
                    scheduler.rescheduleJob(triggers.get(0).getKey(), trigger);
                    return;
                }
            } catch (Exception e) {
                // if get exception, just delete and recreate
                log.warn("{} reschedule error, will delete and recreate.", jobKey.getName(), e);
            }

            // if for some reason the job has multiple triggers, it's easiest to just delete
            // and re-create the job,
            // since we want to enforce a one-to-one relationship between jobs and triggers
            scheduler.deleteJob(jobKey);
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }


    private TaskTimerParamParser getParser(TaskType taskType) {
        TaskTimerParamParser parser = null;
        switch (taskType) {
            case TIMING_TASK:
                parser = new com.ibg.receipt.job.service.impl.TimingTaskTimerParamParser();
                break;
            case LOOP_TASK:
                parser = new com.ibg.receipt.job.service.impl.LoopTaskTimerParamParser();
                break;
            case CRON_TASK:
                parser = new CronTaskTimerParamParser();
                break;
            default:
                throw new RuntimeException("不可到达的分支");
        }
        return parser;
    }

    @Override
    public void stopJob(Long id) throws Exception {
        stopJob(taskTimerService.load(id));
    }

    @Override
    public void stopJob(String taskClass) throws Exception {
        stopJob(taskTimerService.loadByTaskClass(taskClass));
    }

    @Override
    public void stopAllJob() {
        List<TaskTimer> taskTimers = taskTimerService.findAll();
        if (CollectionUtils.isEmpty(taskTimers)) {
            return;
        }
        for (TaskTimer taskTimer : taskTimers) {
            try {
                stopJob(taskTimer);
            } catch (Exception e) {
                log.error("关闭任务{}出错", taskTimer.getTaskClass(), e);
            }
        }
    }

    private void stopJob(TaskTimer taskTimer) throws Exception {
        Assert.notNull(taskTimer, "任务定时器");

        JobKey jobKey = new JobKey(taskTimer.getTaskClass(), Scheduler.DEFAULT_GROUP);

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        if (CollectionUtils.isNotEmpty(triggers)) {
            for (Trigger trigger : triggers) {
                scheduler.unscheduleJob(trigger.getKey());
            }
        }
        scheduler.deleteJob(jobKey);
    }

    /**
     * 释放所有未释放的锁
     */
    @PreDestroy
    public void releaseAllLock() {
        log.info("releasing all lock...");
        BaseJob.releaseAllLock();
        log.info("released all lock...");
    }
}
