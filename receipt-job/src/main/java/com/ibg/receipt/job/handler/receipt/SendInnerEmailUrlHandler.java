package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptUploadInfoStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.model.receiptUploadInfo.ReceiptUploadInfo;
import com.ibg.receipt.service.common.MailSender;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptUserService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.service.receiptUploadInfo.ReceiptUploadInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Component
@Slf4j
public class SendInnerEmailUrlHandler extends BaseHandler {

    @Autowired
    private MailSender mailSender;
    @Autowired
    private ReceiptUploadInfoService receiptUploadInfoService;
    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;

    @Override
    public void handler() throws Exception {
        String text = "借据号:";
        Job job = this.getJob().get();
        Set<String> creditorNames = new HashSet<>();
//        Job job = this.jobService.get(99l);
        String uploadBatchNo = job.getBusinessKey();
        JSONObject jsonObject = JSONObject.parseObject(job.getJobParam());
        String receiver = jsonObject.getString("receiver");
        String fileUrl = jsonObject.getString("fileUrl");
        List<ReceiptUploadInfo> list = receiptUploadInfoService.findByUploadBatchNo(uploadBatchNo)
                .stream().filter(x-> ReceiptUploadInfoStatus.SUCC.getStatus() == x.getStatus().byteValue()).collect(Collectors.toList());
        for (ReceiptUploadInfo info : list) {
            ReceiptChildOrderAmount amount = receiptChildOrderAmountService.findByReceiptChildOrderKey(info.getReceiptChildOrderKey());
            creditorNames.add(amount.getCreditor().getDesc());
        }
        text += new HashSet<>(list.stream().map(x -> x.getLoanId()).collect(Collectors.toList())) + "\n";
        text += "上传姓名:" + list.get(0).getReceiptUserId() + "\n";
        text += "开票主体:" + creditorNames + "\n";
        text += fileUrl;
        mailSender.send(receiver.split(","), uploadBatchNo + "发票上传结果", text);
        //更新任务状态
        saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
    }


    /**
     * 根据属性名获取属性值
     *
     * @param fieldName 属性名
     * @param object    类
     */
    private String getFieldValueByFieldName(String fieldName, Object object) {
        Object val = null;
        try {
            if (object instanceof Map) {
                val = ((Map) object).get(fieldName);
            } else {
                Field field = object.getClass().getDeclaredField(fieldName);
                //设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                val = field.get(object);
            }

        } catch (Exception e) {
            return null;
        }
        if (val instanceof Enum) {
            val = ((Enum) val).name();
        } else {
            val = String.valueOf(val);
        }
        return (String) val;
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }

}
