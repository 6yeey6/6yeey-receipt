package com.ibg.receipt.service.receipt.impl;

import cn.hutool.core.util.ObjectUtil;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.enums.business.ReceiptChannel;
import com.ibg.receipt.enums.business.ReceiptChildOrderAmountStatus;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptBaseInfoService;
import com.ibg.receipt.service.receipt.ReceiptOrderService;
import com.ibg.receipt.service.receipt.ReceiptService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderBaseService;
import com.ibg.receipt.vo.api.receiptChild.ChildOrderSuccessVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;
    @Autowired
    private ReceiptBaseInfoService receiptBaseInfoService;
    @Autowired
    private ReceiptOrderService receiptOrderService;
    @Autowired
    private ReceiptChildOrderBaseService receiptChildOrderBaseService;
    @Autowired
    private JobService jobService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initReceiptChildOrderSuccess(List<ReceiptChildOrderAmount> amountList, List<ReceiptChildOrderBase> baseList, List<ReceiptBaseInfo> list, String receiptOrderKey) {
        try {
            //子单 资金项维度
            for (ReceiptChildOrderAmount amount : amountList) {
                receiptChildOrderAmountService.save(amount);
            }
            //base主体维度
            for (ReceiptChildOrderBase base : baseList) {
                receiptChildOrderBaseService.save(base);
            }
            //更新数仓导入表状态
            for (ReceiptBaseInfo info : list) {
                //数仓 处理完成
                info.setStatus(ReceiptStatus.DEALING.getStatus());
                receiptBaseInfoService.update(info);
            }
            //主单开票中
            ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(receiptOrderKey);
            if (ReceiptStatus.DEALING.getStatus() != receiptOrder.getStatus()) {
                receiptOrder.setStatus(ReceiptStatus.DEALING.getStatus());
                receiptOrderService.update(receiptOrder);
            }
        } catch (Exception e) {
            log.error("主单receiptOrderKey:{}子单生成失败!",receiptOrderKey, e);
            //e.printStackTrace();
            throw new ServiceException("主单receiptOrderKey:"+receiptOrderKey+"子单生成失败!",e);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void childOrderSuccess(String receiptChildOrderKey, ChildOrderSuccessVo vo) {
        try {
            //更新子单
            ReceiptChildOrderAmount receiptChildOrderAmount = receiptChildOrderAmountService.findByReceiptChildOrderKey(receiptChildOrderKey);
            receiptChildOrderAmount.setReceiptUrl(vo.getReceiptUrl());
            receiptChildOrderAmount.setReceiptFileId(ObjectUtil.isNull(vo.getReceiptFileId()) ? null : vo.getReceiptFileId());
            receiptChildOrderAmount.setStatus(ReceiptChildOrderAmountStatus.FINISH.getStatus());
            receiptChildOrderAmount.setFinishTime(new Date());
            if(ReceiptChannel.NUONUO.equals(receiptChildOrderAmount.getReceiptChannel())){
                receiptChildOrderAmount.setSendStatus(ReceiptStatus.SUCCESS.getStatus());
                receiptChildOrderAmount.setSendTime(new Date());
            }
            receiptChildOrderAmountService.update(receiptChildOrderAmount);
            jobService.generateJob(JobMachineStatus.UPDATE_RECEIPT_ORDER, receiptChildOrderAmount.getReceiptOrderKey(), "",new Date(),null);

        } catch (Exception e) {
            log.error("子单更新异常！", e);
            throw e;
        }
    }

    @Override
    public void updateReceiptChildOrderAmountList(List<ReceiptChildOrderAmount> list){
       try {
             for (ReceiptChildOrderAmount amount : list){
                 amount.setSendStatus(ReceiptStatus.SUCCESS.getStatus());
                 amount.setSendTime(new Date());
                 receiptChildOrderAmountService.update(amount);
             }
       }catch (Exception e){
           log.error("子单更新列表发送状态异常！", e);
           throw e;
       }
    }
}
