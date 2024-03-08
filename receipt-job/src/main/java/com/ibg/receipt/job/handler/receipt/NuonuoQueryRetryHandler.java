package com.ibg.receipt.job.handler.receipt;

import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.vo.api.manage.JobUpdateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liuye07
 */
@Component
@Slf4j
public class NuonuoQueryRetryHandler extends BaseHandler {

    @Autowired
    private JobService jobService;

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        try {
            //查询待重推的子单
            List<Job> list = jobService.findByExample(Job.builder().jobStatus((byte) 3).businessStatus((byte) -1).machineStatus(JobMachineStatus.NUONUO_RECEIPT_QUERY.getStatus()).build());
            //存在未处理的数仓数据
            if (list.size() > 0) {
                log.info("当前待重推诺诺查询子单数：{}", list.size());
                for (Job jobBus : list) {
                    jobService.updateJobStatus(JobUpdateVO.builder().jobId(jobBus.getId()).oldJobStatus(3).oldBusinessStatus(-1)
                            .jobStatus(0).businessStatus(0).build());
                }
            }
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        } catch (Exception e) {
            log.error("发票系统生成子单任务异常!", e);
            saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "发票系统生成子单任务异常!");
        }
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }

}
