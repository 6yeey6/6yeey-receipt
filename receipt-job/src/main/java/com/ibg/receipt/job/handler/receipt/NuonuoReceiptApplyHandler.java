package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.constant.ConfigConstants;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.config.refresh.ConfigStaticService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.CreditorInfo;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.nuonuo.NuoNuoService;
import com.ibg.receipt.service.receipt.CreditorInfoService;
import com.ibg.receipt.service.receipt.ReceiptBaseInfoService;
import com.ibg.receipt.service.receipt.ReceiptOrderService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.util.*;
import com.ibg.receipt.vo.api.haohuan.base.NuoNuoApplyRespVo;
import com.ibg.receipt.vo.api.nuonuo.constants.NuoNuoConstants;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptApplyReqVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptApplyRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 诺诺开票申请
 * @author liuye07
 */
@Component
@Slf4j
public class NuonuoReceiptApplyHandler extends BaseHandler {

    @Autowired
    private NuoNuoService nuoNuoService;
    @Autowired
    private ReceiptOrderService receiptOrderService;
    @Autowired
    private ReceiptBaseInfoService receiptBaseInfoService;
    @Autowired
    private CreditorInfoService creditorInfoService;
    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;
    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private RedisService redisService;
    /**
     * 税率
     */
    private static final BigDecimal TAX_RATE = new BigDecimal("0.06");

    private static final String REPEAT_CODE = "E9106";

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        String receiptChildOrderKey = job.getBusinessKey();
        try {
            ReceiptChildOrderAmount receiptChildOrderAmount = receiptChildOrderAmountService.findByReceiptChildOrderKey(receiptChildOrderKey);
            //黑卡走新接口
            List<String> creditorHeika = ConfigStaticService.getConfigAsList(ConfigConstants.CREDITOR_HEIKA,
                    String.class, Arrays.asList(CreditorEnum.HKXD.name(), CreditorEnum.HKXDV2.name(), CreditorEnum.HKXDV3.name(),
                            CreditorEnum.HKXDV4.name(), CreditorEnum.HKXD_YNGM.name(), CreditorEnum.HEIKA.name(), CreditorEnum.XMRD.name()));
            ReceiptApplyReqVo reqVo;
            JsonResultVo<ReceiptApplyRespVo> resultVo;
            //命中
            if (creditorHeika.contains(receiptChildOrderAmount.getCreditor().name())){
                 reqVo = this.buildReceiptApplyReqVo(receiptChildOrderAmount,true);
                log.info("{}发票系统-新版诺诺开票申请请求报文:{}", receiptChildOrderKey, JsonUtils.toJson(reqVo));
                resultVo = nuoNuoService.receiptNewApply(reqVo);
                log.info("{}发票系统-新版诺诺开票申请返回报文:{}", receiptChildOrderKey, JsonUtils.toJson(resultVo));
            }else {
                 reqVo = this.buildReceiptApplyReqVo(receiptChildOrderAmount,false);
                log.info("{}发票系统-诺诺开票申请请求报文:{}", receiptChildOrderKey, JsonUtils.toJson(reqVo));
                resultVo = nuoNuoService.receiptApply(reqVo);
                log.info("{}发票系统-诺诺开票申请返回报文:{}", receiptChildOrderKey, JsonUtils.toJson(resultVo));
            }

            // 响应异常，重试
            if (!resultVo.isSuccess() || resultVo.getData() == null) {
                //token不一致，删除key，重新查询
                if("070301".equals(resultVo.getStatus()) && "accessToken不匹配/或appKey不匹配".equals(resultVo.getMessage())){
                    String key = NuoNuoConstants.FUND_NUONUO_ACCESSTOKEN +  reqVo.getAppKey();
                    redisService.del(key);
                }
                log.error("{}发票系统-申请诺诺响应异常,待重试！", receiptChildOrderKey);
                saveJob(JobStatus.INIT, ProcessStatus.FAIL, "请求诺诺开票申请响应异常，待重试，响应:" + JSON.toJSONString(resultVo));
                return;
            }
            // 发票流水号
            NuoNuoApplyRespVo respVo = JSON.parseObject(JsonUtils.toJson(resultVo), NuoNuoApplyRespVo.class);
            String invoiceSerialNum = respVo.getData().getInvoiceSerialNum();
            // 保存开票流水号
            JSONObject extInfoJson = StringUtils.isNotBlank(receiptChildOrderAmount.getExtInfo()) ? JSONObject.parseObject(receiptChildOrderAmount.getExtInfo()) : new JSONObject();
            extInfoJson.put("invoiceSerialNum", invoiceSerialNum);
            receiptChildOrderAmount.setStatus(ReceiptStatus.DEALING.getStatus());
            receiptChildOrderAmount.setExtInfo(extInfoJson.toJSONString());
            receiptChildOrderAmountService.update(receiptChildOrderAmount);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("invoiceSerialNum", invoiceSerialNum);
            // 开票结果查询job
            jobService.generateJob(JobMachineStatus.NUONUO_RECEIPT_QUERY, receiptChildOrderKey, jsonObject, new Date(), job.getId());
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        } catch (Exception e) {
            String msg = "发票系统-开票申请异常!receiptChildOrderKey:" + receiptChildOrderKey;
            log.error(msg, e);
            //JobStatus jobStatus = JobStatus.INIT;
            //if (job.getExecuteTimes() != null && job.getExecuteTimes() > 10) {
            //    NoticeUtils.businessError(msg);
            //    jobStatus = JobStatus.NOTICE_MANUAL;
            //}
            super.saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, e.getMessage());
        }
    }

    /**
     * 根据isNeW判断是不是新接口
     * @param receiptChildOrderAmount
     * @param
     * @return
     */
    private ReceiptApplyReqVo buildReceiptApplyReqVo(ReceiptChildOrderAmount receiptChildOrderAmount,boolean isNew) {
        Date currentDate = new Date();
        String receiptOrderKey = receiptChildOrderAmount.getReceiptOrderKey();
        String loanId = receiptChildOrderAmount.getLoanId();
        CreditorEnum creditor = receiptChildOrderAmount.getCreditor();
        List<ReceiptBaseInfo> receiptBaseInfo = receiptBaseInfoService.findByLoanIdAndCreditor(loanId, creditor);
        ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(receiptOrderKey);
        CreditorInfo creditorInfo = creditorInfoService.getByCreditor(creditor);

        // 开票金额
        BigDecimal receiptAmount = receiptChildOrderAmount.getReceiptAmount();
        // 不含税金额=含税金额/1.06
        BigDecimal taxExcludedAmount = receiptAmount.divide(BigDecimalUtils.add(TAX_RATE, new BigDecimal(1)), 2, BigDecimal.ROUND_HALF_UP);

        // 发票明细
        ReceiptApplyReqVo.OrderBean.InvoiceDetailBean invoiceDetail = ReceiptApplyReqVo.OrderBean.InvoiceDetailBean.builder()
                // 商品名称
                .goodsName(EncryptUtil.getDecoded(creditorInfo.getGoodsName()))
                // 单价含税标志：0:不含税,1:含税
                .withTaxFlag("1")
                // 税率，注：1、纸票清单红票存在为null的情况；2、二手车发票税率为null或者0
                .taxRate(TAX_RATE)
                // 含税金额
                .taxIncludedAmount(receiptAmount)
                // 不含税金额
                .taxExcludedAmount(taxExcludedAmount)
                // 税额
                .tax(BigDecimalUtils.subtract(receiptAmount, taxExcludedAmount))
                .build();

        ReceiptApplyReqVo.OrderBean order = ReceiptApplyReqVo.OrderBean.builder()
                // 企业名称/个人
                .buyerName(EncryptUtil.getDecoded(receiptBaseInfo.get(0).getUserName()))
                // 销方税号（使用沙箱环境请求时消息体参数salerTaxNum和消息头参数userTax填写339902999999789113）
                .salerTaxNum("test".equals(active) ? "339902999999789113" : creditorInfo.getTaxNo())
                // 销方电话 我司
                .salerTel(EncryptUtil.getDecoded(creditorInfo.getMobile()))
                // 销方地址 我司
                .salerAddress(EncryptUtil.getDecoded(creditorInfo.getAddress()))
                // 订单号（每个企业唯一）
                .orderNo(SerialNoGenerator.generateSerialNo(receiptChildOrderAmount.getReceiptChildOrderKey(), 32))
                // 订单时间 2022-01-13 12:30:00
                .invoiceDate(DateUtils.format(currentDate, DateUtils.DATE_TIME_FORMAT_PATTERN))
                // 开票员
                .clerk(EncryptUtil.getDecoded(creditorInfo.getClerk()))
                // 推送方式：-1,不推送;0,邮箱;1,手机（默认）;2,邮箱、手机
                .pushMode("2")
                // 购方手机（pushMode为1或2时，此项为必填，同时受企业资质是否必填控制）
                .buyerPhone(EncryptUtil.getDecoded(receiptBaseInfo.get(0).getAccount()))
                // 用户邮件
                .email(EncryptUtil.getDecoded(receiptOrder.getEmail()))
                // 开票类型：1:蓝票;2:红票 （全电发票暂不支持红票）
                .invoiceType("1")
                // 发票明细
                .invoiceDetail(Lists.newArrayList(invoiceDetail))
                .build();
        //全电开票方式时
        if (isNew){
            order.setInvoiceLine("pc");
            //order.setExtensionNumber("923");
        }else{
            //非全电,且中盈主体走老方法也需要传
            List<String> creditorZy = ConfigStaticService.getConfigAsList(ConfigConstants.CREDITOR_ZY,
                    String.class, Arrays.asList(CreditorEnum.HTXD.name(), CreditorEnum.LSXD.name(), CreditorEnum.TRXD.name(),
                            CreditorEnum.HTXD_WC.name(), CreditorEnum.TRXD2.name(), CreditorEnum.LSXD_YX.name()));
            if (creditorZy.contains(receiptChildOrderAmount.getCreditor().name())){
                order.setInvoiceLine("pc");
            }
        }
        ReceiptApplyReqVo reqVo = ReceiptApplyReqVo.builder().order(order).build();
        reqVo.setAppKey(creditorInfo.getAppKey());
        reqVo.setAppSecret(creditorInfo.getAppSecret());
        return reqVo;
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }
}
