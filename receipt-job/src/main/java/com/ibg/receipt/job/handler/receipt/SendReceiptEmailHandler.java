package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.exception.ExceptionUtils;
import com.ibg.receipt.config.fileSystem.FileSystemConfig;
import com.ibg.receipt.enums.business.ReceiptChannel;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.service.common.MailSender;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptOrderService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.util.EncryptUtil;
import com.ibg.receipt.util.MetaFsUtil;
import com.ibg.receipt.vo.mail.AttachmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 */
@Component
@Slf4j
    public class SendReceiptEmailHandler extends BaseHandler implements CommandLineRunner {

    @Autowired
    private MailSender mailSender;

    @Autowired
    private FileSystemConfig fileSystemConfig;
    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;
    @Autowired
    private ReceiptOrderService receiptOrderService;

    @Override
    public void handler() throws Exception {
//        {"uid":"e154f9f8448c3b4e57a0022675f5f4c0","receiptChildOrderKeys":"[\"RECEIPT_CHILD_AMOUNTxEM55VNFRVV5\"]"}
        Job job = this.getJob().get();
//        Job job = jobService.get(60l);
        String sendBatchNo = job.getBusinessKey();
        JSONObject jsonObject = JSONObject.parseObject(job.getJobParam());
        List<String> repayChildOrderKeys = jsonObject.getJSONArray("receiptChildOrderKeys").toJavaList(String.class);
        if(repayChildOrderKeys.size() == 0){
            throw  new Exception("子单数据为空，无法导出邮件！");
        }
        try {
            List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByReceiptChildOrderKeyIn(repayChildOrderKeys);
            Map<String,List<ReceiptChildOrderAmount>> map = list.stream().filter(receiptChildOrderAmount -> ReceiptChannel.MANUAL.equals(receiptChildOrderAmount.getReceiptChannel()) && receiptChildOrderAmount.getReceiptFileId() != null).collect(Collectors.groupingBy(ReceiptChildOrderAmount::getReceiptOrderKey));
            for (String key:map.keySet()){
                List<ReceiptChildOrderAmount> receiptOrderGroupList = map.get(key);
                ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(key);
                //TODO 文件判重
                List<String> fileIds = receiptOrderGroupList.stream().map(ReceiptChildOrderAmount::getReceiptFileId).distinct().collect(Collectors.toList());
                List<AttachmentVo> attachmentVos = new ArrayList<>();
                for(String fsFileId:fileIds){
                    Map fileMap;
                    try {
                        log.info("partnerId:{};邮箱:{}当前上传meta文件id为:{}",receiptOrder.getUid(), EncryptUtil.getDecoded(receiptOrder.getEmail()),fsFileId);
                        fileMap = MetaFsUtil.downloadFromMetaFs(fileSystemConfig.getMetaFileDownloadSystemUrl(),fileSystemConfig.getSecret(),fsFileId);
                        String fileName = (String) fileMap.get("fileName");
                        byte[] fileByte = (byte[]) fileMap.get("fileBytes");
                        attachmentVos.add(generateAttachment(fileByte,fileName));
                    } catch (Exception e) {
                        log.error("metaFs文件下载失败！fsId:{}.",fsFileId,e);
                        throw new Exception("发票文件下载失败，发送邮件异常！batchNo="+sendBatchNo);
                    }
                }
                log.info("邮件收到发票的partnerId:{}",receiptOrder.getUid());
                sendMail(new String[]{EncryptUtil.getDecoded(receiptOrder.getEmail().trim())},attachmentVos);
                receiptOrderGroupList.forEach(x -> {
                    x.setSendStatus((byte)2);
                    x.setSendTime(new Date());
                    receiptChildOrderAmountService.update(x);
                });
            }

            //更新任务状态
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        }catch (Exception e){
            log.error("发票发送邮件异常！",e);
            //更新任务状态
            saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "发票发送邮件异常:" + e.getMessage());
            throw e;
        }
    }


    private void sendMail(String[] toMail,  List<AttachmentVo> attachmentVo) {
        try {
            AttachmentVo[]  attachmentVos = attachmentVo.toArray(new AttachmentVo[0]);
            String reportContent = "您好！<br/>发票已开具完成，见附件．</br>谢谢！";
            ProcessStatus sendStatus = mailSender.send(toMail, null, null, "好分期发票开具完成提醒", reportContent, attachmentVos);
            if (sendStatus == ProcessStatus.SUCCESS) {
                log.info("发票邮件发送成功");
            } else {
                log.warn("发票邮件发送失败sendStatus:{}", sendStatus);
                String subject = "发票发送失败!";
                String content = String.format("您好！<br/>发票名单发送失败！</br>发送状态:%s",
                        sendStatus);
                //NoticeUtils.businessError("\n" + subject + "\ncontent:" + content);
                mailSender.send(toMail, null, null, subject, content);
                throw ExceptionUtils.commonError(content);
            }
        } catch (Exception e) {
            String message = String.format("邮件发送异常:%s", e.getMessage());
            log.error(message, e);
            throw ExceptionUtils.commonError(message);
        }
    }


    private AttachmentVo generateAttachment(byte[] arrays, String fileName) {
        AttachmentVo attachmentVo = new AttachmentVo();
        attachmentVo.setFileName(fileName);
        attachmentVo.setFileBytes(arrays);
        return attachmentVo;
    }


    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }

    @Override
    public void run(String... strings) throws Exception {
//        handler();
    }
}
