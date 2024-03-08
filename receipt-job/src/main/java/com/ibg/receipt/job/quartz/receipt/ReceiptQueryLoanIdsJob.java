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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 */
@Slf4j
@Component
@JobHandler("receiptQueryLoanIdsJob")
public class ReceiptQueryLoanIdsJob extends BaseXxlJob {

    @Autowired
    private JobService jobService;

    @Override
    public void run(String param) {
        String batchNo = SerialNoGenerator.generateSerialNo("QUERY_LOAN_IDS", 32);
        Date date = Date.from(LocalDate.now().plusDays(-1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        ReceiptJobVo jobVo = ReceiptJobVo.builder()
                .batchNo(batchNo)
                .jobDate(date)
                .build();
        jobService.generateJob(JobMachineStatus.QUERY_LOAN_IDS, batchNo, JSONObject.parseObject(JSONObject.toJSONString(jobVo)));
        log.info("生成发票系统每日拉取D-1待开票数据!任务时间:{},待开票日期:{}", new Date(), date);
    }
}
