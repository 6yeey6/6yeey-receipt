package com.ibg.receipt.job.quartz.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.job.base.BaseXxlJob;
import com.ibg.receipt.job.vo.job.ReceiptJobVo;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.util.SerialNoGenerator;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Date;

/**
 * @author liuye07
 */
@Slf4j
@Component
@JobHandler("nuonuoQueryRetryJob")
public class NuonuoQueryRetryJob extends BaseXxlJob {

    @Autowired
    private JobService jobService;

    @Override
    public void run(String param) {
        String batchNo = SerialNoGenerator.generateSerialNo("NUO_ERROR_QUERY", 32);
        ReceiptJobVo jobVo = ReceiptJobVo.builder()
                .batchNo(batchNo)
                .build();
        jobService.generateJob(JobMachineStatus.NUONUO_QUERY_RETRY, batchNo, JSONObject.parseObject(JSONObject.toJSONString(jobVo)));
        log.info("诺诺查询节点为开飘完成数据统计!任务时间:{}", new Date());
    }
}
