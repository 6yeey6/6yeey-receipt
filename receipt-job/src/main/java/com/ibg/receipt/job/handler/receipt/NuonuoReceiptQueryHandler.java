package com.ibg.receipt.job.handler.receipt;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.constant.ConfigConstants;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.config.fileSystem.FileSystemConfig;
import com.ibg.receipt.config.refresh.ConfigStaticService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.CreditorInfo;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.service.common.MetaFileService;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.nuonuo.NuoNuoService;
import com.ibg.receipt.service.receipt.CreditorInfoService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.util.*;
import com.ibg.receipt.vo.api.haohuan.base.NuoNuoQueryRespVo;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptApplyReqVo;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptQueryReqVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptQueryRespVo;
import com.ibg.receipt.vo.api.receiptChild.ChildOrderSuccessVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 诺诺开票结果查询
 */
@Component
@Slf4j
public class NuonuoReceiptQueryHandler extends BaseHandler {

    @Autowired
    private NuoNuoService nuoNuoService;
    @Autowired
    private CreditorInfoService creditorInfoService;
    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;
    @Autowired
    private MetaFileService metaFileService;
    @Autowired
    private FileSystemConfig fileSystemConfig;

    /**
     * 开票完成状态
     */
    private static final List<String> CODE_SUCCESS = Lists.newArrayList("2");
    /**
     * 开票中
     */
    private static final List<String> CODE_PROCESS = Lists.newArrayList("20", "21", "3", "31");
    /**
     * 开票失败
     */
    private static final List<String> CODE_FAIL = Lists.newArrayList("22", "24");

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        String receiptChildOrderKey = job.getBusinessKey();
        try {
            JSONObject vo = JSONObject.parseObject(job.getJobParam());
            String invoiceSerialNum = vo.getString("invoiceSerialNum");

            ReceiptChildOrderAmount receiptChildOrderAmount = receiptChildOrderAmountService.findByReceiptChildOrderKey(receiptChildOrderKey);

            List<String> creditorHeika = ConfigStaticService.getConfigAsList(ConfigConstants.CREDITOR_HEIKA,
                    String.class, Arrays.asList(CreditorEnum.HKXD.name(), CreditorEnum.HKXDV2.name(), CreditorEnum.HKXDV3.name(),
                            CreditorEnum.HKXDV4.name(), CreditorEnum.HKXD_YNGM.name(), CreditorEnum.HEIKA.name(), CreditorEnum.XMRD.name()));
            ReceiptQueryReqVo reqVo;
            JsonResultVo<List<ReceiptQueryRespVo>> resultVo;
            if (creditorHeika.contains(receiptChildOrderAmount.getCreditor().name())) {
                reqVo = this.buildReceiptQueryReqVo(receiptChildOrderAmount, invoiceSerialNum);
                log.info("{}发票系统-新版诺诺开票查询请求报文:{}", receiptChildOrderKey, JsonUtils.toJson(reqVo));
                resultVo = nuoNuoService.receiptNewQuery(reqVo);
                log.info("{}发票系统-新版诺诺开票查询响应报文:{}", receiptChildOrderKey, JsonUtils.toJson(resultVo));
            } else {
                reqVo = this.buildReceiptQueryReqVo(receiptChildOrderAmount, invoiceSerialNum);
                log.info("{}发票系统-诺诺开票查询请求报文:{}", receiptChildOrderKey, JsonUtils.toJson(reqVo));
                resultVo = nuoNuoService.receiptQuery(reqVo);
                log.info("{}发票系统-诺诺开票查询响应报文:{}", receiptChildOrderKey, JsonUtils.toJson(resultVo));
            }

            // 响应异常，重试
            if (!resultVo.isSuccess() || resultVo.getData() == null || CollectionUtils.isEmpty(resultVo.getData())) {
                saveJob(JobStatus.INIT, ProcessStatus.FAIL, "请求诺诺开票申请响应异常，待重试!receiptChildOrderKey:" + receiptChildOrderKey);
                return;
            }
            NuoNuoQueryRespVo respVo = JSON.parseObject(JsonUtils.toJson(resultVo), NuoNuoQueryRespVo.class);
            NuoNuoQueryRespVo.ResultData resultData = respVo.getData().get(0);
            // 开票状态
            String status = resultData.getStatus();
            if (ConfigStaticService.getConfigAsList("nuonuoReceiptSuccessCode", String.class, CODE_SUCCESS).contains(status)) {
                // 开票成功
                this.receiptSuccess(resultData, receiptChildOrderKey);
                saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
                return;
            } else if (ConfigStaticService.getConfigAsList("nuonuoReceiptProcessCode", String.class, CODE_PROCESS).contains(status)) {
                // 开票处理中
                saveJob(JobStatus.INIT, ProcessStatus.FAIL, "请求诺诺开票处理中，待重试，receiptChildOrderKey:" + receiptChildOrderKey + ";status:" + status);
                return;
            } else if (ConfigStaticService.getConfigAsList("nuonuoReceiptFailCode", String.class, CODE_FAIL).contains(status)) {

                List<String> authMsg = ConfigStaticService.getConfigAsList(ConfigConstants.AUTH_MSG,
                        String.class, Arrays.asList("[9999]本次操作需登录电子发票服务平台，请先使用短信验证码登录", "[9999]您的发票开具触发了风险监控预警或实人身份认证时间已过期，需要重新扫码进行实人身份认证"));
                List<String> retryMsg = ConfigStaticService.getConfigAsList(ConfigConstants.RETRY_MSG,
                        String.class, Arrays.asList("请稍后进行手工重推"));

                String errMsg = String.format("诺诺开票申请失败！需人工干预！诺诺申请执行jobId:%s,我方子单号:%s\n主体信息:%s\n请求诺诺订单号:[%s];\n失败原因:[%s]", ObjectUtil.isNotNull(job.getLastJobId()) ? job.getLastJobId() : job.getId(), job.getBusinessKey(), resultData.getSaleName(), resultData.getOrderNo(), resultData.getFailCause());
                if (authMsg.contains(resultData.getFailCause())) {
                    log.error(errMsg);
                    NoticeUtils.businessError(errMsg, Lists.newArrayList("wangtao30", "wangbaozhu"), Lists.newArrayList());
                    //认证后，5个小时候重试申请
                    saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, errMsg,job.getJobParam(),DateUtils.setHours(new Date(), 5));
                    return;
                }else if (retryMsg.contains(resultData.getFailCause())) {
                    //5个小时后重试
                    log.error(errMsg+"\n5小时候重试!");
                    Date startTime = DateUtils.setHours(new Date(), 5);
                    jobService.generateJob(JobMachineStatus.NUONUO_RECEIPT_APPLY, receiptChildOrderKey, new JSONObject(), startTime, job.getId());
                    saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
                    return;
                }else{
                    log.error(errMsg);
                    NoticeUtils.businessError(errMsg);
                    saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, errMsg);
                    return;
                }

            } else {
                saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "请求诺诺开票未知响应,人工干预!receiptChildOrderKey" + receiptChildOrderKey + ";status:" + status);
                return;
            }
        } catch (Exception e) {
            String msg = "发票系统-开票结果查询异常!receiptChildOrderKey:" + receiptChildOrderKey;
            log.error(msg, e);
            JobStatus jobStatus = JobStatus.INIT;
            if (job.getExecuteTimes() != null && job.getExecuteTimes() > 10) {
                jobStatus = JobStatus.NOTICE_MANUAL;
            }
            super.saveJob(jobStatus, ProcessStatus.FAIL, e.getMessage());
        }
    }

    /**
     * 开票成功，上传文件到fs
     *
     * @param
     * @param receiptChildOrderKey
     */
    private void receiptSuccess(NuoNuoQueryRespVo.ResultData resultData, String receiptChildOrderKey) throws Exception {
        String pdfUrl = resultData.getPdfUrl();
        String ofdUrl = resultData.getOfdUrl();
        // 发票pdf地址（若同时返回了ofdUrl与pdfUrl，则pdf文件不能做为原始凭证，请用ofd文件做为原始凭证）
        String url = StringUtils.isNotBlank(pdfUrl) && StringUtils.isNotBlank(ofdUrl) ? ofdUrl : pdfUrl;
//        String fileName = url.substring(url.lastIndexOf('/')+1);
        String fileName = DateUtils.format(new Date(), DateUtils.DATE_TIME_NO_BLANK_FORMAT) + url.substring(url.lastIndexOf('.'));
        log.info("诺诺发票下载成功！url:{};receiptChildOrderKey:{}:上传fs系统文件名为:{}", url, receiptChildOrderKey, fileName);
        byte[] bos = this.getByteArrayContent(url);
        String metaFileId = metaFileService.uploadToMetaFs(new ByteArrayInputStream(bos), fileName,
                null, null, "NUONUO_RECEIPT");
        //String filePath = this.fsClientService.getFileDownloadUrl(metaFileId);
        String filePath = fileSystemConfig.getReceiptFileDownloadUrl() + metaFileId;

        ChildOrderSuccessVo param = ChildOrderSuccessVo.builder().receiptFileId(metaFileId).receiptUrl(filePath).build();
        // 保存子单开票状态job
        jobService.generateJob(JobMachineStatus.CHILD_ORDER_SUCCESS, receiptChildOrderKey, JSONObject.toJSONString(param), new Date(), null);
    }

    private ReceiptQueryReqVo buildReceiptQueryReqVo(ReceiptChildOrderAmount receiptChildOrderAmount, String invoiceSerialNum) {
        CreditorInfo creditorInfo = creditorInfoService.getByCreditor(receiptChildOrderAmount.getCreditor());
        ReceiptQueryReqVo reqVo = ReceiptQueryReqVo.builder().serialNos(Lists.newArrayList(invoiceSerialNum)).build();
        reqVo.setAppKey(creditorInfo.getAppKey());
        reqVo.setAppSecret(creditorInfo.getAppSecret());
        return reqVo;
    }

    private byte[] getByteArrayContent(String filePath) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(filePath);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(10 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();

            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            return bos.toByteArray();
        } catch (Exception e) {
            log.error("从url:{}获取文件流失败", filePath, e);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                log.error("通过url下载文件close异常！", e);
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                log.error("通过url下载文件close异常！", e);
            }
        }
        return null;
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }
}
