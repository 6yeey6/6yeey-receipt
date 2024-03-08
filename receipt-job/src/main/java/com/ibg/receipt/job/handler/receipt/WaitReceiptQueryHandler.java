package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.config.nuonuo.NuoNuoConfig;
import com.ibg.receipt.enums.business.ReceiptChannel;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.job.vo.job.ReceiptJobVo;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.NoticeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数仓在T+1日回传数据后，根据开票主体生成对应的子单.生成子单后
 * ，对于人工审核的，资金运营进行审核后导出开票信息发送至相应的开票主体，对于自动开票的直接调用诺诺平台开票接口
 */
@Component
@Slf4j
public class WaitReceiptQueryHandler extends BaseHandler {

    @Autowired
    private NuoNuoConfig nuoNuoConfig;
    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        try {
            ReceiptJobVo vo = JSONObject.parseObject(job.getJobParam(), ReceiptJobVo.class);

            // 待开票子单数据
            List<ReceiptChildOrderAmount> orderAmounts = receiptChildOrderAmountService.findByStatus(ReceiptStatus.INIT.getStatus());

            // 无待开票子单
            if (CollectionUtils.isEmpty(orderAmounts)) {
                saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
                return;
            }

            // 人工干预
            List<ReceiptChildOrderAmount> manualOrders = orderAmounts.stream().filter(order -> ReceiptChannel.MANUAL.equals(order.getReceiptChannel())).collect(Collectors.toList());
            //if (orderAmounts.size()>0) {
            //    // 运营群提醒
            //    String msg = "【发票工单处理】Hi 同学，您当前有"+orderAmounts.size()+"个待处理的开票工单，请及时登录发票平台处理！\n发票平台:" + nuoNuoConfig.getManualLink();
            //    NoticeUtils.wxNoticeSendMessage(nuoNuoConfig.getManualDboUrl(), msg, Lists.newArrayList());
            //}

            // 诺诺开票
            List<ReceiptChildOrderAmount> nuonuoOrders = orderAmounts.stream().filter(order -> ReceiptChannel.NUONUO.equals(order.getReceiptChannel())).collect(Collectors.toList());
            nuonuoOrders.forEach(order -> jobService.generateJob(JobMachineStatus.NUONUO_RECEIPT_APPLY, order.getReceiptChildOrderKey(), new JSONObject(), new Date(), job.getId()));

            log.info("发票系统-待开票数据处理完成:共{}条,人工干预{}条，诺诺开票申请{}条", orderAmounts.size(), manualOrders.size(), nuonuoOrders.size());
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        } catch (Exception e) {
            String msg = "发票系统-待开票数据处理异常!batchNo:" + job.getBusinessKey();
            log.error(msg, e);
            NoticeUtils.businessError(msg);
            saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, msg);
        }
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }
}
