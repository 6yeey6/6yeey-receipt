package com.ibg.receipt.job.quartz.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.job.base.BaseXxlJob;
import com.ibg.receipt.job.vo.job.ReceiptJobVo;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.util.DateUtils;
import com.ibg.receipt.util.SerialNoGenerator;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 开票定时
 */
@Slf4j
@Component
@JobHandler("waitReceiptQueryJob")
public class WaitReceiptQueryJob extends BaseXxlJob {

    @Autowired
    private JobService jobService;

    @Override
    public void run(String param) {
        String batchNo = SerialNoGenerator.generateSerialNo("WAIT_RECEIPT_QUERY", 32);
        Date date = new Date();
        ReceiptJobVo jobVo = ReceiptJobVo.builder()
                .batchNo(batchNo)
                .jobDate(date)
                .build();
        log.info("发票系统定时开票JOB！batchNo:{};date:{}",batchNo, DateUtils.format(date,DateUtils.DATE_FORMAT_PATTERN));
        jobService.generateJob(JobMachineStatus.WAIT_RECEIPT_QUERY, batchNo, JSONObject.parseObject(JSONObject.toJSONString(jobVo)));
    }
}
