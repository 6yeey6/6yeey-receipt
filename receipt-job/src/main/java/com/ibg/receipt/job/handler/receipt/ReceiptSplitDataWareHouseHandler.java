package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.job.vo.job.ReceiptJobVo;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptBaseInfoService;
import com.ibg.receipt.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 按照订单纬度生成对应资金项的任务
 */
@Component
@Slf4j
public class ReceiptSplitDataWareHouseHandler extends BaseHandler {

    @Autowired
    private ReceiptBaseInfoService receiptBaseInfoService;

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        try {
            ReceiptJobVo vo = JSONObject.parseObject(job.getJobParam(), ReceiptJobVo.class);
            //需开票订单
            Date date = DateUtils.getNextDayDate(vo.getJobDate(), 1);
            Date beginDate = DateUtils.setDateBegin(date);
            Date endDate = DateUtils.setDateEnd(date);
            //数仓同步数据且未处理数据生成子单，根据配置项属性生成
            //利用主体查询该主体对应的资金项,遍历该主体配置的资金项目列表，查看数据该资金项是否为空，为空提示异常！
            List<ReceiptBaseInfo> list = receiptBaseInfoService.findByCreateTimeBetweenAndStatus(beginDate, endDate, ReceiptStatus.INIT.getStatus());
            //存在未处理的数仓数据
            if (list.size() > 0) {
                //按订单纬度生成JOB，并根据JOB生成子单
                Map<String, List<ReceiptBaseInfo>> receiptBaseInfoMap = list.stream()
                        .collect(Collectors.groupingBy(receiptBaseInfo -> receiptBaseInfo.getLoanId()));
                for (String loanId : receiptBaseInfoMap.keySet()) {
                    jobService.generateJob(JobMachineStatus.INIT_CHILD_ORDER, loanId, JSONObject.parseObject(JSONObject.toJSONString(vo)), new Date(), job.getId());
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
