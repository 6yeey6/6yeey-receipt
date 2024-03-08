package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptService;
import com.ibg.receipt.util.JsonUtils;
import com.ibg.receipt.util.NoticeUtils;
import com.ibg.receipt.vo.api.receiptChild.ChildOrderSuccessVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 子单完成
 */
@Component
@Slf4j
public class ReceiptChildOrderSuccessHandler extends BaseHandler implements CommandLineRunner {

    @Autowired
    private ReceiptService receiptService;

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        ChildOrderSuccessVo vo = JSONObject.parseObject(job.getJobParam(), ChildOrderSuccessVo.class);
        String receiptChildOrderKey = job.getBusinessKey();
        log.info("发票系统子单获取发票成功！receiptChildOrderKey:{}", receiptChildOrderKey);
        //更新子单
        try {
            receiptService.childOrderSuccess(receiptChildOrderKey,vo);
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        } catch (Exception e) {
            log.error("发票系统子单更新失败！receiptChildOrderKey:{}", receiptChildOrderKey, e);
            NoticeUtils.businessError("发票系统子单更新失败！receiptChildOrderKey:" + receiptChildOrderKey);
            saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "发票系统子单失败receiptChildOrderKey:" + receiptChildOrderKey);
        }

    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }

    @Override
    public void run(String... strings) throws Exception {

    }
}
