package com.ibg.receipt.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.constant.ConfigConstants;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.base.vo.CustomPageRequest;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.config.refresh.ConfigStaticService;
import com.ibg.receipt.dao.job.JobRepository;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.enums.job.JobType;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.DateUtils;
import com.ibg.receipt.util.RandomUtils;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.manage.JobQueryVO;
import com.ibg.receipt.vo.api.manage.JobUpdateVO;
import com.ibg.receipt.vo.api.manage.JobVO;
import com.ibg.receipt.vo.job.base.JobParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class JobServiceImpl extends BaseServiceImpl<Job, JobRepository> implements JobService {

    @Autowired
    @Override
    protected void setRepository(JobRepository repository) {
        super.repository = repository;
    }

    public static final String JOB_PARAM_NEXT_START_TIME = "jobNextStartTime";

    @Override
    public Page<Job> getToDealJobList(int pageNum, int pageSize, Long lastId) {
        CustomPageRequest pageable = new CustomPageRequest(pageNum, pageSize, Sort.Direction.ASC, "id");
        return this.repository.findByJobStatusAndIdGreaterThan(JobStatus.INIT.getStatus(), lastId, pageable);

    }

    @Override
    public List<Job> getToDealJobList(int pageSize, Long lastId, int mod, int result) {

        return this.repository.findJobBySharding(JobStatus.INIT.getStatus(), lastId, mod, result, pageSize);

    }

    @Override
    public List<Job> findJobsByBusinessKey(String businessKey) {
        return repository.findAllByBusinessKeyEqualsOrderByUpdateTimeDesc(businessKey);
    }

    @Override
    public void saveOrUpdate(Job job) {
        trimLastError(job);
        if (job.getId() == null) {
            super.save(job);
        } else {
            if (job.getExecuteTimes() == null) {
                job.setExecuteTimes(1);
            } else {
                job.setExecuteTimes(job.getExecuteTimes() + 1);
            }
            if (JobStatus.INIT == JobStatus.getEnum(job.getJobStatus())) {
                if (JobType.TIME_JOB != JobType.getEnum(job.getJobType())) {
                    Date delayDate = getNextRunDate(job.getExecuteTimes());
                    job.setJobStartTime(delayDate);
                } else {
                    String jobParam = job.getJobParam();
                    if (StringUtils.isBlank(jobParam)) {
                        jobParam = new JSONObject().toJSONString();
                    }
                    JSONObject jobParamJson = JSONObject.parseObject(jobParam);
                    jobParamJson.put(JOB_PARAM_NEXT_START_TIME,
                        DateUtils.getDelayDate(15).getTime());
                    job.setJobParam(jobParamJson.toJSONString());
                }
            }
            super.update(job);
        }
    }

    private Date getNextRunDate(Integer executeTimes) {
        // 阈值（默认50次）以内 推迟15s执行，如果大于阈值，15s 执行一次，可能拿到结果的概率比较小，减少资源消耗
        if (executeTimes == null
            || executeTimes < ConfigStaticService.getConfigAsInteger(ConfigConstants.JOB_NEXT_RUN_THRESHOLD, 50)) {
            return DateUtils.getDelayDate(15);
        } else {
            return DateUtils.getDelayDate(5 * executeTimes);
        }
    }

    private void trimLastError(Job job) {
        if (StringUtils.isNotBlank(job.getLastError())) {
            if (job.getLastError().length() > 255) {
                job.setLastError(job.getLastError().substring(0, 255));
            }
        }
    }

    public boolean isJobDone(Job job) {
        JobStatus jobStatus = JobStatus.getEnum(job.getJobStatus());
        return (JobStatus.FAIL == jobStatus)
            || (JobStatus.SUCCESS == jobStatus)
            || (JobStatus.NOTICE_MANUAL == jobStatus);
    }

    /**
     * job指定了下次开始时间
     *
     * @param job
     */
    @Override
    public void saveOrUpdateWithJobStartTime(Job job) {
        trimLastError(job);
        if (job.getId() == null) {
            save(job);
        } else {
            if (job.getExecuteTimes() == null) {
                job.setExecuteTimes(1);
            } else {
                job.setExecuteTimes(job.getExecuteTimes() + 1);
            }
            update(job);
        }
    }

    @Override
    public void saveNotUpdateExecuteTimes(Job job) {
        trimLastError(job);
        update(job);
    }

    @Override
    public void updateJobState(String ids, JobStatus jobStatus, String lastError) throws Exception {
        // 校验参数
        Assert.notBlank(ids, "job表主键");
        List<String> idList = StringUtils.splitToList(ids, ",");
        if (!CollectionUtils.isEmpty(idList)) {
            for (String id : idList) {
                Job job = load(Long.parseLong(id));
                if (job == null) {
                    throw new Exception("Job的id为" + id + "的Job不存在");
                }
                job.setJobStatus(jobStatus.getStatus());
                job.setLastError(lastError);
                update(job);
            }
        }
    }

    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JSONObject jsonParam,
        Date jobStartTime, Long jobId) {
        Job job = new Job();
        job.setMachineStatus(jobMachineStatus.getStatus());
        job.setBusinessKey(businessKey);
        job.setJobType(JobType.COMMON_JOB.getType());
        job.setJobStatus(JobStatus.INIT.getStatus());
        job.setBusinessStatus(ProcessStatus.INIT.getStatus());
        job.setJobStartTime(jobStartTime);
        job.setJobParam(jsonParam.toString());
        if (jobId != null) {
            job.setLastJobId(jobId);
        }
        this.saveOrUpdate(job);
    }

    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JSONObject jsonParam) {
        Job job = new Job();
        job.setMachineStatus(jobMachineStatus.getStatus());
        job.setBusinessKey(businessKey);
        job.setJobType(JobType.COMMON_JOB.getType());
        job.setJobStatus(JobStatus.INIT.getStatus());
        job.setBusinessStatus(ProcessStatus.INIT.getStatus());
        job.setJobParam(jsonParam != null ? jsonParam.toString() : null);
        this.saveOrUpdate(job);
    }

    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JobParamVo jobParamVo) {
        generateJob(jobMachineStatus, businessKey, JSON.parseObject(JSON.toJSONString(jobParamVo)));
    }

    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, String businessKey, String jsonJobParam) {
        generateJob(jobMachineStatus, businessKey, JSON.parseObject(jsonJobParam));
    }

    @Override
    public void saveOrUpdate(List<Job> jobList) {
        if (CollectionUtils.isNotEmpty(jobList)) {
            for (Job job : jobList) {
                saveOrUpdate(job);
            }
        }
    }

    @Override
    public Job findOneByExample(Job cond) {
        return this.repository.findOne(Example.of(cond));
    }

    @Override
    public List<Job> findByExample(Job cond) {
        if (cond == null) {
            // model 为空，不查询所有数据
            return Lists.newArrayList();
        }
        return this.repository.findAll(Example.of(cond));
    }

    @Override
    public void saveJob(JobStatus status, ProcessStatus businessStatus, String lastError, Job job) {
        job.setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.setBusinessStatus(businessStatus.getStatus());
        job.setLastError(lastError);
        this.saveOrUpdate(job);
    }

    @Override
    public void saveNextJob(JobMachineStatus nextStatus, Job job) {
        Job nextJob = new Job();
        nextJob.setMachineStatus(nextStatus.getStatus());
        nextJob.setBusinessKey(job.getBusinessKey());
        nextJob.setJobType(JobType.COMMON_JOB.getType());
        nextJob.setJobStatus(JobStatus.INIT.getStatus());
        nextJob.setBusinessStatus(ProcessStatus.INIT.getStatus());
        nextJob.setLastJobId(job.getId());
        nextJob.setJobParam(job.getJobParam());
        this.saveOrUpdate(nextJob);
    }

    /**
     * 保存job，下次开始设置
     *
     * @param status
     * @param businessStatus
     * @param delaySeconds   延迟秒数
     * @param lastError
     */
    @Override
    public void saveJob(JobStatus status, ProcessStatus businessStatus, int delaySeconds, String lastError, Job job) {
        job.setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.setBusinessStatus(businessStatus.getStatus());
        job.setLastError(lastError);
        job.setJobStartTime(DateUtils.addSeconds(new Date(), delaySeconds));
        this.saveOrUpdateWithJobStartTime(job);
    }

    @Override
    public void saveJob(JobStatus status, String lastError, Job job) {
        job.setJobStatus(status.getStatus());
        if (lastError == null) {
            lastError = "";
        }
        job.setLastError(lastError);
        this.saveOrUpdate(job);
    }

    @Override
    public void saveNextJob(List<JobMachineStatus> nextStatusList, String businessKey, JSONObject jsonParam) {

        if (CollectionUtils.isEmpty(nextStatusList)) {
            return;
        }
        List<Job> jobList = new ArrayList<>();
        for (JobMachineStatus jobMachineStatus : nextStatusList) {
            Job nextJob = new Job();
            nextJob.setMachineStatus(jobMachineStatus.getStatus());
            nextJob.setBusinessKey(businessKey);
            nextJob.setJobType(JobType.COMMON_JOB.getType());
            nextJob.setJobStatus(JobStatus.INIT.getStatus());
            nextJob.setBusinessStatus(ProcessStatus.INIT.getStatus());
            nextJob.setJobParam(jsonParam != null ? jsonParam.toString() : null);
            nextJob.setJobStartTime(Date.from(LocalDateTime.now().plusSeconds(RandomUtils.nextInt(5, 10))
                .atZone(ZoneId.systemDefault()).toInstant()));
            jobList.add(nextJob);
        }
        this.saveOrUpdate(jobList);
    }

    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, String businessKey, String jsonParam, Long jobId) {
        Job job = new Job();
        job.setMachineStatus(jobMachineStatus.getStatus());
        job.setBusinessKey(businessKey);
        job.setJobType(JobType.COMMON_JOB.getType());
        job.setJobStatus(JobStatus.INIT.getStatus());
        job.setBusinessStatus(ProcessStatus.INIT.getStatus());
        job.setJobParam(jsonParam);
        if (jobId != null) {
            job.setLastJobId(jobId);
        }
        this.saveOrUpdate(job);
    }

    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, String businessKey, JSONObject jsonParam, Long jobId) {
        Job job = new Job();
        job.setMachineStatus(jobMachineStatus.getStatus());
        job.setBusinessKey(businessKey);
        job.setJobType(JobType.COMMON_JOB.getType());
        job.setJobStatus(JobStatus.INIT.getStatus());
        job.setBusinessStatus(ProcessStatus.INIT.getStatus());
        job.setJobParam(jsonParam != null ? jsonParam.toString() : null);
        if (jobId != null) {
            job.setLastJobId(jobId);
        }
        this.saveOrUpdate(job);
    }

    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, String businessKey, String jsonParam, Date jobStartTime,
        Long jobId) {
        Job job = new Job();
        job.setMachineStatus(jobMachineStatus.getStatus());
        job.setBusinessKey(businessKey);
        job.setJobType(JobType.COMMON_JOB.getType());
        job.setJobStatus(JobStatus.INIT.getStatus());
        job.setBusinessStatus(ProcessStatus.INIT.getStatus());
        job.setJobStartTime(jobStartTime);
        job.setJobParam(jsonParam);
        if (jobId != null) {
            job.setLastJobId(jobId);
        }
        this.saveOrUpdate(job);
    }

    @Override
    public PageVo<JobVO> findJobs(JobQueryVO jobQueryVO) {
        CustomPageRequest pageable = new CustomPageRequest(jobQueryVO.getPageNum(), jobQueryVO.getPageSize());
        Specification<Job> querySpecification = this.listWithDynamicQueryPaged(jobQueryVO);
        return new PageVo<>(repository.findAll(querySpecification, pageable), JobVO.class, jobQueryVO.getPageNum(),
                jobQueryVO.getPageSize());
    }

    private Specification<Job> listWithDynamicQueryPaged(JobQueryVO jobQueryVO) {
        Specification<Job> querySpecification = (Root<Job> root, CriteriaQuery<?> criteriaQuery,
                CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != jobQueryVO.getJobId()) {
                predicates.add(cb.equal(root.get("id"), jobQueryVO.getJobId()));
            }
            if (StringUtils.isNotBlank(jobQueryVO.getJobMachineStatus())) {
                predicates.add(cb.equal(root.get("machineStatus"), jobQueryVO.getJobMachineStatus()));
            }
            if (null != jobQueryVO.getJobStatus() && null != JobStatus.getEnum(jobQueryVO.getJobStatus())) {
                predicates
                        .add(cb.equal(root.get("jobStatus"), JobStatus.getEnum(jobQueryVO.getJobStatus()).getStatus()));
            }
            if (null != jobQueryVO.getBusinessStatus()
                    && null != ProcessStatus.getEnum(jobQueryVO.getBusinessStatus())) {
                predicates.add(cb.equal(root.get("businessStatus"),
                        ProcessStatus.getEnum(jobQueryVO.getBusinessStatus()).getStatus()));
            }
            if (StringUtils.isNotBlank(jobQueryVO.getBusinessKey())) {
                predicates.add(cb.equal(root.get("businessKey"), jobQueryVO.getBusinessKey()));
            }

            if (StringUtils.isNotBlank(jobQueryVO.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"),
                        DateUtils.parse(jobQueryVO.getBeginTime(), DateUtils.DATE_TIME_FORMAT_PATTERN)));
            }
            if (StringUtils.isNotBlank(jobQueryVO.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"),
                        DateUtils.parse(jobQueryVO.getEndTime(), DateUtils.DATE_TIME_FORMAT_PATTERN)));
            }
            return cb.and(predicates.toArray(new Predicate[] {}));
        };
        return querySpecification;
    }

    @Override
    public Job updateJobStatus(JobUpdateVO jobUpdateVO) {
        Job job = Job.builder().build();
        if (jobUpdateVO.getOldJobStatus() != null && JobStatus.getEnum(jobUpdateVO.getOldJobStatus()) != null) {
            job.setJobStatus(JobStatus.getEnum(jobUpdateVO.getOldJobStatus()).getStatus());
        }
        if (jobUpdateVO.getOldBusinessStatus() != null && ProcessStatus.getEnum(jobUpdateVO.getOldBusinessStatus()) != null) {
            job.setBusinessStatus(ProcessStatus.getEnum(jobUpdateVO.getOldBusinessStatus()).getStatus());
        }
        job.setId(jobUpdateVO.getJobId());
        job = this.findOneByExample(job);
        if (job != null) {
            if (StringUtils.isNotBlank(jobUpdateVO.getJobParam())) {
                job.setJobParam(jobUpdateVO.getJobParam());
            }
            if (jobUpdateVO.getJobStartTime() != null) {
                job.setJobStartTime(jobUpdateVO.getJobStartTime());
            }
            if (jobUpdateVO.getJobStatus() != null && JobStatus.getEnum(jobUpdateVO.getJobStatus()) != null) {
                job.setJobStatus(JobStatus.getEnum(jobUpdateVO.getJobStatus()).getStatus());
            }
            if (jobUpdateVO.getBusinessStatus() != null && ProcessStatus.getEnum(jobUpdateVO.getBusinessStatus()) != null) {
                job.setBusinessStatus(ProcessStatus.getEnum(jobUpdateVO.getBusinessStatus()).getStatus());
            }
        } else {
            throw new ServiceException("job不存在或者job已经更新");
        }

        return this.update(job);
    }

    @Override
    public List<Job> findJobByJobMachineStatusAndCreateTimeBetween(String machineStatus, Date startTime, Date endTime) {
        return repository.findJobsByMachineStatusAndCreateTimeBetween(machineStatus, startTime, endTime);
    }

    @Override
    public List<Job> findJobsByMachineStatusAndLastJobIdAndCreateTimeBetween(String machineStatus, Long id, Date startTime, Date endTime) {
        return repository.findJobsByMachineStatusAndLastJobIdAndCreateTimeBetween(machineStatus, id, startTime, endTime);
    }
}
