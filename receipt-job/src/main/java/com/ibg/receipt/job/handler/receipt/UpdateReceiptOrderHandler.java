package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.enums.business.ReceiptChildOrderAmountStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptOrderService;
import com.ibg.receipt.service.receipt.ReceiptService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.vo.api.receiptChild.ChildOrderSuccessVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主单状态进度更新
 */
@Component
@Slf4j
public class UpdateReceiptOrderHandler extends BaseHandler implements CommandLineRunner {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;
    @Autowired
    private ReceiptOrderService receiptOrderService;
    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        String receiptOrderKey = job.getBusinessKey();
        String key = "UPDATERECEIPTORDER_"+receiptOrderKey;
        if(redisService.acquireLockWithTTL(key,300)){
            log.info("开始更新主单状态:{}",receiptOrderKey);
            //更新主单
            List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByReceiptOrderKey(receiptOrderKey);
            int finishedList = list.parallelStream().filter(e -> ReceiptChildOrderAmountStatus.FINISH.getStatus() == e.getStatus()
                    || ReceiptChildOrderAmountStatus.NODEAL.getStatus() == e.getStatus()).collect(Collectors.toList()).size();
            ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(receiptOrderKey);
            //全部完成
            if (finishedList == list.size()) {
                receiptOrder.setStatus(ReceiptStatus.SUCCESS.getStatus());
                receiptOrder.setFinishTime(new Date());
                receiptOrder.setScale(finishedList + "/" + list.size());
            } else {
                receiptOrder.setScale(finishedList + "/" + list.size());
            }
            receiptOrderService.update(receiptOrder);
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS,null);
            redisService.releaseLock(key);
            log.info("更新完毕主单状态:{}",receiptOrderKey);

        }else{
            log.info("更新主单{}获取锁失败！",receiptOrderKey);
            saveJob(JobStatus.INIT, ProcessStatus.INIT,"获取更新主单锁失败");
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
