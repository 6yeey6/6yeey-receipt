package com.ibg.receipt.api.controller;

import com.alibaba.fastjson.JSON;
import com.ibg.commons.log.LogInfo;
import com.ibg.commons.log.LogInfos;
import com.ibg.commons.log.LogKey;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.manage.JobAddVO;
import com.ibg.receipt.vo.api.manage.JobQueryVO;
import com.ibg.receipt.vo.api.manage.JobUpdateVO;
import com.ibg.receipt.vo.api.manage.JobVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 *
 * @author liuye07
 */
@Slf4j
@RestController
@RequestMapping("/manage/job")
public class JobController {
    @Autowired
    private JobService jobService;


    @PostMapping(value = "/queryJob")
    public JsonResultVo<?> jobList(@Valid @RequestBody JobQueryVO jobQueryVO) {
        try {
            jobQueryVO.checkParam();
            PageVo<JobVO> jobs = jobService.findJobs(jobQueryVO);
            return JsonResultVo.success().setData(jobs);
        } catch (ServiceException e) {
            log.warn("查询job异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "查询失败:" + e.getMessage());
        } catch (Exception e) {
            log.warn("查询job异常", e);
            return JsonResultVo.error();
        }
    }

    @LogInfos({@LogInfo(key = LogKey.TRACE_KEY, value = "#jobUpdateVO.jobId"),
            @LogInfo(key = LogKey.MODULE, value = "更新job状态"),
            @LogInfo(key = "output", value = "#output", inReturn = true)})
    @PostMapping(value = "/changeJobStatus")
    public JsonResultVo<?> changeJobStatus(@Valid @RequestBody JobUpdateVO jobUpdateVO) {
        try {
            log.info("更新job状态,请求参数：{}", JSON.toJSONString(jobUpdateVO));
            jobService.updateJobStatus(jobUpdateVO);
            return JsonResultVo.success();
        } catch (ServiceException e) {
            log.warn("更新job异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "更新失败:" + e.getMessage());
        } catch (Exception e) {
            log.warn("更新job异常", e);
            return JsonResultVo.error();
        }
    }

    @PostMapping(value = "/addJob")
    public JsonResultVo<?> addJob(@Valid @RequestBody JobAddVO jobAddVO) {
        try {
            Job job = Job.builder()
                    .businessKey(jobAddVO.getBusinessKey()).machineStatus(jobAddVO.getJobMachineStatus().getStatus())
                    .businessStatus((byte) 0).executeTimes(0).jobParam(jobAddVO.getJobParam())
                    .jobStartTime(jobAddVO.getJobStartTime()).jobStatus((byte) 0).jobType("COMMON").lastError(StringUtils.EMPTY)
                    .lastJobId(StringUtils.isBlank(jobAddVO.getLastJobId()) ? null : Long.parseLong(jobAddVO.getLastJobId()))
                    .maxExecuteTimes(null).build();
            jobService.saveOrUpdate(job);
            return JsonResultVo.success();
        } catch (Exception e) {
            return JsonResultVo.error(CodeConstants.C_10101002, "新增失败:" + e.getMessage());
        }
    }
}