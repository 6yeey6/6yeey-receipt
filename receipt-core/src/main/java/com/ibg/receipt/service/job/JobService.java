package com.ibg.receipt.service.job;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.vo.api.manage.JobQueryVO;
import com.ibg.receipt.vo.api.manage.JobUpdateVO;
import com.ibg.receipt.vo.api.manage.JobVO;
import com.ibg.receipt.vo.job.base.JobParamVo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface JobService extends BaseService<Job> {

    Page<Job> getToDealJobList(int pageNum, int pageSize, Long lastId);

    List<Job> getToDealJobList(int pageSize, Long lastId, int mod, int result);

    List<Job> findJobsByBusinessKey(String businessKey);

    void generateJob(JobMachineStatus jobMachineStatus, String businessKey, String jsonJobParam);

    void saveOrUpdate(List<Job> job);

    void saveOrUpdate(Job job);

    void saveOrUpdateWithJobStartTime(Job job);

    void saveNotUpdateExecuteTimes(Job job);

    void updateJobState(String ids, JobStatus jobStatus, String lastError) throws Exception;

    void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JSONObject jsonParam, Date jobStartTime,
        Long jobId);

    void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JSONObject jsonParam);

    void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JobParamVo jobParamVo);

    Job findOneByExample(Job cond);

    List<Job> findByExample(Job job);

    void saveJob(JobStatus status, ProcessStatus businessStatus, String lastError, Job job);

    void saveNextJob(JobMachineStatus nextStatus, Job job);

    void saveJob(JobStatus status, ProcessStatus businessStatus, int delaySeconds, String lastError, Job job);

    void saveJob(JobStatus noticeManual, String message, Job job);

    void saveNextJob(List<JobMachineStatus> nextStatusList, String businessKey, JSONObject jsonParam);

    void generateJob(JobMachineStatus jobMachineStatus, String businessKey, String jsonParam, Long jobId);

    void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JSONObject jsonParam, Long jobId);

    void generateJob(JobMachineStatus jobMachineStatus, String businessKey, String jsonParam, Date jobStartTime,
        Long jobId);

    PageVo<JobVO> findJobs(JobQueryVO jobQueryVO);

    Job updateJobStatus(JobUpdateVO jobUpdateVO);

    List<Job> findJobByJobMachineStatusAndCreateTimeBetween(String machineStatus, Date startTime, Date endTime);

    List<Job> findJobsByMachineStatusAndLastJobIdAndCreateTimeBetween(String machineStatus,Long id, Date startTime, Date endTime);
}
