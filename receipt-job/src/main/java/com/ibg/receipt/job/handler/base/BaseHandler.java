package com.ibg.receipt.job.handler.base;

import com.google.common.base.Stopwatch;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.enums.job.JobType;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.DateUtils;
import com.ibg.receipt.util.NoticeUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public abstract class BaseHandler implements Runnable {

    protected final static Logger logger = LogManager.getLogger(BaseHandler.class);

    protected JobService jobService;

    private ThreadLocal<Job> job = new ThreadLocal<>();

    @Autowired
    private Tracer tracer;


    @Override
    public void run() {
        Stopwatch watch = Stopwatch.createStarted();
        try {
            startTracing();
            handler();
        } catch (Exception e) {
            log.error("job:{}，执行异常：{}", job.get().getId(), e.getMessage(), e);
            saveJob(JobStatus.NOTICE_MANUAL,
                    StringUtils.left(StringUtils.defaultIfBlank(e.getMessage(), e.toString()), 2000));
            NoticeUtils.businessError(
                    String.format("Job[%s, id = %s]需要人工处理:\n %s\nthread:\"trace.%s\"", job.get().getMachineStatus(),
                            job.get().getId(),
                            StringUtils.left(StringUtils.defaultIfBlank(e.getMessage(), e.toString()), 500),
                            tracer.getCurrentSpan().traceIdString()));
        } finally {
            endTracing(watch);
            job.remove();
        }
    }

    private void endTracing(Stopwatch watch) {
        Span span = tracer.getCurrentSpan();
        long cost = watch.elapsed(TimeUnit.MILLISECONDS);
        log.info("thread:\"trace.{}\"，jobId:{}, machineStatus:{}, bizKey:{} end, costTime:{}ms",
            tracer.getCurrentSpan().traceIdString(), job.get().getId(),
            job.get().getMachineStatus(), job.get().getBusinessKey(), cost);
        tracer.close(span);
    }

    private void startTracing() {
        tracer.createSpan(getClass().getSimpleName(), tracer.getCurrentSpan());
        log.info("thread:\"trace.{}\"，jobId:{} start", tracer.getCurrentSpan().traceIdString(), job.get().getId());
    }

    public abstract void handler() throws Exception;

    public abstract void setJobService(JobService jobService) throws Exception;

    public void saveJob(JobStatus status, String lastError) {
        job.get().setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.get().setLastError(lastError);
        getJobService().saveOrUpdate(job.get());
    }

    public void saveJob(JobStatus status, ProcessStatus businessStatus, String lastError) {
        job.get().setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.get().setBusinessStatus(businessStatus.getStatus());
        job.get().setLastError(lastError);
        getJobService().saveOrUpdate(job.get());
    }

    public void saveJob(JobStatus status, ProcessStatus businessStatus, String lastError, String jobParam) {
        job.get().setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.get().setJobParam(jobParam);
        job.get().setBusinessStatus(businessStatus.getStatus());
        job.get().setLastError(lastError);
        getJobService().saveOrUpdate(job.get());
    }

    public void saveJob(JobStatus status, ProcessStatus businessStatus, String lastError, String jobParam, Date nextRunTime) {
        job.get().setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        if(jobParam != null){
            job.get().setJobParam(jobParam);
        }
        job.get().setBusinessStatus(businessStatus.getStatus());
        job.get().setLastError(lastError);
        if (nextRunTime != null) {
            job.get().setJobStartTime(nextRunTime);
            getJobService().saveOrUpdateWithJobStartTime(job.get());
        } else {
            getJobService().saveOrUpdate(job.get());
        }
    }

    public void saveJob(JobStatus status, ProcessStatus businessStatus, String lastError, Date nextRunTime) {
        job.get().setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.get().setBusinessStatus(businessStatus.getStatus());
        job.get().setLastError(lastError);
        if(nextRunTime != null){
            job.get().setJobStartTime(nextRunTime);
            getJobService().saveOrUpdateWithJobStartTime(job.get());
        }else {
            getJobService().saveOrUpdate(job.get());
        }
    }


    /**
     * 保存job，下次开始设置
     *
     * @param status
     * @param delaySeconds
     *            延迟秒数
     * @param lastError
     */
    public void saveJob(JobStatus status, int delaySeconds, String lastError) {
        this.saveJob(status, ProcessStatus.getEnum(job.get().getBusinessStatus()), delaySeconds, lastError);
    }

    /**
     * 保存job，下次开始设置
     *
     * @param status
     * @param businessStatus
     * @param delaySeconds
     *            延迟秒数
     * @param lastError
     */
    public void saveJob(JobStatus status, ProcessStatus businessStatus, int delaySeconds, String lastError) {
        job.get().setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.get().setBusinessStatus(businessStatus.getStatus());
        job.get().setLastError(lastError);
        job.get().setJobStartTime(DateUtils.addSeconds(new Date(), delaySeconds));
        getJobService().saveOrUpdateWithJobStartTime(job.get());
    }

    public void saveWithJobStartTime(JobStatus status, String lastError) {
        job.get().setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.get().setLastError(lastError);
        getJobService().saveOrUpdateWithJobStartTime(job.get());
    }

    public void saveNotUpdateExecuteTimes(JobStatus status, String lastError) {
        job.get().setJobStatus(status.getStatus());
        job.get().setLastError(lastError);
        job.get().setJobStartTime(DateUtils.getDelayDate(15));
        getJobService().saveNotUpdateExecuteTimes(job.get());
    }

    public void saveNextJob(JobMachineStatus nextStatus, String jobParam) {
        Job nextJob = new Job();
        nextJob.setMachineStatus(nextStatus.getStatus());
        nextJob.setBusinessKey(job.get().getBusinessKey());
        nextJob.setJobType(JobType.COMMON_JOB.getType());
        nextJob.setJobStatus(JobStatus.INIT.getStatus());
        nextJob.setBusinessStatus(ProcessStatus.INIT.getStatus());
        nextJob.setLastJobId(job.get().getId());
        nextJob.setJobParam(jobParam);
        getJobService().saveOrUpdate(nextJob);
    }

    public void saveNextJob(JobMachineStatus nextStatus) {
        saveNextJob(nextStatus, loadJob().getJobParam());
    }

    public void saveNextJob(List<JobMachineStatus> nextStatusList) {
        this.saveNextJob(nextStatusList, true);
    }

    /**
     * @param nextStatusList
     * @param isOpenNextJob
     *            是否开启nextJob
     */
    public void saveNextJob(List<JobMachineStatus> nextStatusList, boolean isOpenNextJob) {

        // 如果未开启nextJob的控制，则结束
        if (!isOpenNextJob) {
            log.info("job:{}，未开启nextJob控制", job.get().getId());
            return;
        }

        if (CollectionUtils.isEmpty(nextStatusList)) {
            return;
        }
        List<Job> jobList = new ArrayList<>();
        for (JobMachineStatus jobMachineStatus : nextStatusList) {
            Job nextJob = new Job();
            nextJob.setMachineStatus(jobMachineStatus.getStatus());
            nextJob.setBusinessKey(job.get().getBusinessKey());
            nextJob.setJobType(JobType.COMMON_JOB.getType());
            nextJob.setJobStatus(JobStatus.INIT.getStatus());
            nextJob.setBusinessStatus(ProcessStatus.INIT.getStatus());
            nextJob.setLastJobId(loadJob().getId());
            nextJob.setJobParam(loadJob().getJobParam());
            jobList.add(nextJob);
        }
        getJobService().saveOrUpdate(jobList);
    }

    public void saveNextJobWithStartTime(JobMachineStatus nextStatus, String jobParam, Date startTime) {
        Job nextJob = new Job();
        nextJob.setMachineStatus(nextStatus.getStatus());
        nextJob.setBusinessKey(job.get().getBusinessKey());
        nextJob.setBusinessStatus(ProcessStatus.INIT.getStatus());
        nextJob.setJobType(JobType.COMMON_JOB.getType());
        nextJob.setJobStatus(JobStatus.INIT.getStatus());
        nextJob.setLastJobId(job.get().getId());
        nextJob.setJobParam(jobParam);
        nextJob.setJobStartTime(startTime);
        getJobService().saveOrUpdate(nextJob);
    }

    public Job loadJob() {
        return getJob().get();
    }

    public JobMachineStatus loadMachineStatus() {
        return JobMachineStatus.getEnum(getJob().get().getMachineStatus());
    }

    // 批量保存下个节点
    public void saveNextJobs(List<JobMachineStatus> jobMachineStatuses, Date nextRunTime) {
        if (CollectionUtils.isNotEmpty(jobMachineStatuses)) {
            for (JobMachineStatus machineStatus : jobMachineStatuses) {
                if(machineStatus != null){
                    saveNextJobWithStartTime(machineStatus, this.getJob().get().getJobParam(), nextRunTime);
                }
            }
        }
    }
}
