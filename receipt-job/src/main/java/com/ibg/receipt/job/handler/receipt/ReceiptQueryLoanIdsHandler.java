package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.job.vo.job.ReceiptJobVo;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.ReceiptOrderLoan;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptOrderLoanService;
import com.ibg.receipt.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * D+1拉取本地待开票状态主单数据，查询对应的loanId
 */
@Component
@Slf4j
public class ReceiptQueryLoanIdsHandler extends BaseHandler {

    @Autowired
    private ReceiptOrderLoanService receiptOrderLoanService;

    @Override
    public void handler() throws Exception {
        try {
            Job job = this.getJob().get();
            ReceiptJobVo vo = JSONObject.parseObject(job.getJobParam(), ReceiptJobVo.class);
            Date yesterdayDate = vo.getJobDate();
            Date beginDate = DateUtils.setDateBegin(yesterdayDate);
            Date endDate = DateUtils.setDateEnd(yesterdayDate);
            List<ReceiptOrderLoan> receiptOrderLoanList = receiptOrderLoanService.findByRequestTimeAndStatus(ReceiptStatus.INIT.getStatus(), beginDate, endDate);
            log.info("发票系统拉取本地{}待开票数据为{}条!", DateUtils.format(yesterdayDate, DateUtils.DATE_FORMAT_PATTERN), receiptOrderLoanList.size());
            if (receiptOrderLoanList.size() > 0) {
                List<String> loanIds = receiptOrderLoanList.parallelStream().map(ReceiptOrderLoan::getLoanId).collect(Collectors.toList());
                vo.setLoanIdList(loanIds);
                jobService.generateJob(JobMachineStatus.PULL_DATA_WAREHOUSE, vo.getBatchNo(), JSONObject.parseObject(JSONObject.toJSONString(vo)), new Date(), job.getId());
            }
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        } catch (Exception e) {
            log.error("发票系统拉取本地开票数据异常!",e);
            saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "发票系统拉取本地开票数据异常!");
        }
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }
}
