package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.job.vo.job.ReceiptJobVo;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptBaseInfoService;
import com.ibg.receipt.util.DateUtils;
import com.ibg.receipt.util.NoticeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * D+1拉取数仓同步数据
 */
@Component
@Slf4j
public class ReceiptPullDataWareHouseHandler extends BaseHandler {

    @Autowired
    private ReceiptBaseInfoService receiptBaseInfoService;

    @Override
    public void handler() throws Exception {

        Job job = this.getJob().get();
        try {
            ReceiptJobVo vo = JSONObject.parseObject(job.getJobParam(), ReceiptJobVo.class);
            //本地待开票数据
            Date date = DateUtils.getNextDayDate(vo.getJobDate(), 1);
            Date beginDate = DateUtils.setDateBegin(date);
            Date endDate = DateUtils.setDateEnd(date);
            Set<String> localLoanIdSet = new HashSet<>(vo.getLoanIdList());
            //查询数仓数据基本信息表-未处理
            List<String> list = receiptBaseInfoService.findByCreateTimeBetweenAndStatus(beginDate, endDate, ReceiptStatus.INIT.getStatus())
                    .stream().map(receiptBaseInfo -> receiptBaseInfo.getLoanId()).collect(Collectors.toList());
            log.info("查询数仓未处理条数{}",list.size());
            if (list.size() > 0) {
                Set<String> loanIdsSet = new HashSet<>(list);
                localLoanIdSet.removeAll(loanIdsSet);
                if (localLoanIdSet.size() > 0) {
                    log.error("发票系统数仓同步数据缺少!日期:{};loanIds:{}", DateUtils.format(date, DateUtils.DATE_FORMAT_PATTERN), localLoanIdSet);
                    saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "发票系统数仓同步数据缺少!");
                }
                //按照订单纬度拆分生成子单
                jobService.generateJob(JobMachineStatus.SPILT_DATA_WAREHOUSE, vo.getBatchNo(), JSONObject.parseObject(JSONObject.toJSONString(vo)), new Date(), job.getId());
                log.info("发票系统查询数仓基础表查询需开票数据为:{}条", list.size());
            }
            log.info("发票系统-查询数仓数据基本信息表-未处理数据为空!date:{}",DateUtils.format(vo.getJobDate(),DateUtils.DATE_FORMAT_PATTERN));
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        } catch (Exception e) {
            NoticeUtils.businessError("发票系统拉取数仓同步数据job任务异常!jobId:" + job.getId());
            log.error("发票系统拉取数仓同步数据异常!batchNo:{}", job.getBusinessKey(),e);
            //log.info("发票系统拉取数仓同步数据异常!{}",e.getMessage());
            saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "发票系统拉取数仓同步数据异常!batchNo:" + job.getBusinessKey());
            throw new Exception(e);
        }
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }
}
