package com.ibg.receipt.job.handler.receipt;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.enums.business.*;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.*;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;
import com.ibg.receipt.service.haohuan.HaoHuanService;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.*;
import com.ibg.receipt.util.*;
import com.ibg.receipt.vo.api.haohuan.HaoHuanReqVo;
import com.ibg.receipt.vo.api.haohuan.HaoHuanRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生成子单任务
 */
@Component
@Slf4j
public class ReceiptInitChildOrderHandler extends BaseHandler {

    @Autowired
    private ReceiptBaseInfoService receiptBaseInfoService;
    @Autowired
    private CreditorAmountConfigService creditorAmountConfigService;
    @Autowired
    private CreditorService creditorService;
    @Autowired
    private ReceiptOrderLoanService receiptOrderLoanService;
    @Autowired
    private CreditorBaseConfigService creditorBaseConfigService;
    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private HaoHuanService haoHuanService;
    @Autowired
    private ReceiptOrderService receiptOrderService;

    private final static List<String> itemExtList = Arrays.asList("INTEREST_FEE", "TOTAL_SERVICE_FEE", "TOTAL_GUARANTOR_FEE");

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        String loanId = job.getBusinessKey();
        log.info("数仓数据订单纬度JOB生成！loanId:{}", loanId);
        try {
            //待开票子单数量
            List<ReceiptChildOrderAmount> amountList = new ArrayList<>();
            List<ReceiptChildOrderBase> baseList = new ArrayList<>();
            //需开票订单
            //数仓同步数据且未处理数据生成子单，根据配置项属性生成
            List<ReceiptBaseInfo> list = receiptBaseInfoService.findByLoanIdAndStatus(loanId, ReceiptStatus.INIT.getStatus());
            if (list.size() > 0) {
                ReceiptOrderLoan receiptOrderLoan = receiptOrderLoanService.findByLoanId(list.get(0).getLoanId());
                //查询此订单所有主体，通过主体的配置查询到需要生成子单的数据
                String receiptOrderKey = receiptOrderLoan.getReceiptOrderKey();
                //数仓订单纬度的数据
                List<CreditorEnum> creditorList = list.stream().map(ReceiptBaseInfo -> ReceiptBaseInfo.getCreditor()).collect(Collectors.toList());
                HashSet<CreditorEnum> creditorSet = new HashSet<>(creditorList);
                //查询该主体的所有配置项
                for (CreditorEnum creditorEnum : creditorSet) {
                    ReceiptBaseInfo receiptBaseInfoNew = ReceiptBaseInfo.builder().build();
                    List<ReceiptBaseInfo> baseInfos = receiptBaseInfoService.findByLoanIdAndCreditor(loanId, creditorEnum);
                    BeanUtils.copyProperties(receiptBaseInfoNew, baseInfos.get(0));
                    BigDecimal repayInterest = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayInterest())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayMgmtFee = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayMgmtFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayOverdueInterest = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayOverdueInterest())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayOverdueMgmtFee = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayOverdueMgmtFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayFunderOverdueInterest = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayFunderOverdueInterest())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayGuaranteeDeposit = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayGuaranteeDeposit())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayGuaranteeFee = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayGuaranteeFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayOverdueGuaranteeFee = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayOverdueGuaranteeFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayCommutation = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayCommutation())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal repayInRepayFee = baseInfos.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayInRepayFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    receiptBaseInfoNew = ReceiptBaseInfo.builder().repayInterest(repayInterest).repayMgmtFee(repayMgmtFee).repayOverdueInterest(repayOverdueInterest)
                            .repayOverdueMgmtFee(repayOverdueMgmtFee).repayFunderOverdueInterest(repayFunderOverdueInterest).repayGuaranteeDeposit(repayGuaranteeDeposit)
                            .repayGuaranteeFee(repayGuaranteeFee).repayOverdueGuaranteeFee(repayOverdueGuaranteeFee).repayCommutation(repayCommutation).repayInRepayFee(repayInRepayFee).build();
                    //该订单的所有主体项
                    List<CreditorAmountConfig> creditorAmountConfigList = creditorAmountConfigService.findByCreditorAndDeleted(creditorEnum, false);
                    if (creditorAmountConfigList.size() == 0) {
                        //取该主体方对应的运营人员
                        List<Creditor> creditors = creditorService.findByCreditorAndDeleted(creditorEnum, false);
                        List<String> userName;
                        if (creditors.size() > 0) {
                            userName = creditors.parallelStream().map(Creditor::getUserName).collect(Collectors.toList());
                        } else {
                            userName = Lists.newArrayList("zengyuyan");
                        }
                        log.error("未查询到主体配置列表!jobId:{};loanId:{};主体:{}", job.getId(), loanId, creditorEnum);
                        NoticeUtils.businessError("未查询到主体配置列表!请配置主体主体:" + creditorEnum.getDesc(), userName, Lists.newArrayList());
                        throw new ServiceException("未查询到主体配置列表!jobId:" + job.getId() + ";loanId:" + loanId + ";主体:" + creditorEnum);
                        //continue;
                    }
                    String extItem = splitItem(creditorAmountConfigList, receiptBaseInfoNew);
                    for (CreditorAmountConfig config : creditorAmountConfigList) {
                        //查询此资金项的值
                        BigDecimal receiptAmount = getReceiptAmount(config, receiptBaseInfoNew);
                        //资金项拆分
                        Creditor creditor1 = creditorService.findByCreditorAndDeleted(config.getCreditor(), false).get(0);
                        ReceiptChildOrderAmount amount = this.genReceiptChildOrderAmount(creditor1, baseInfos.get(0), config, receiptAmount, receiptOrderKey, extItem);
                        //如果配置项有，但数仓为INTEREST_FEE0，不生成子单
                        if (BigDecimal.ZERO.compareTo(amount.getReceiptAmount()) != 0) {
                            //查询该主体如果是WE的,利息和保障金不生成子单
                            ReceiptBaseInfo info = list.stream().filter(x -> creditorEnum.equals(x.getCreditor())).findFirst().get();
                            if ("WE".equals(info.getFundCode())) {
                                if (ReceiptItemCodeAmount.INTEREST.equals(amount.getReceiptItemCode()) ||
                                        ReceiptItemCodeAmount.GUARANTEE_DEPOSIT.equals(amount.getReceiptItemCode())) {
                                    continue;
                                }
                            }
                            amountList.add(amount);
                        }
                    }
                    //大秦主体配置服务费、逾期管理费、逾期罚息为0时 不生成子单
                    if (CreditorEnum.YIRONG.equals(creditorEnum)){
                        List<ReceiptChildOrderAmount> amountListTemp =  amountList.stream().filter(amount -> ReceiptItemCodeAmount.MGMT_FEE.equals(amount.getReceiptItemCode())
                                || ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.equals(amount.getReceiptItemCode())
                                || ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.equals(amount.getReceiptItemCode())).collect(Collectors.toList());
                        //数据集合为空，即为三个资金项都没有，remove掉提前还款违约金
                        if (CollectionUtils.isEmpty(amountListTemp)){
                            amountList = amountList.stream().filter(amount -> !ReceiptItemCodeAmount.IN_REPAY_FEE.equals(amount.getReceiptItemCode())).collect(Collectors.toList());
                        }
                    }
                    if (amountList.size() > 0){
                    //TODO 主体维度生成baseInfo
                    List<CreditorBaseConfig> baseConfigList = creditorBaseConfigService.findByCreditorAndDeleted(creditorEnum, false);
                    //数仓有值，配置无值
                    if (baseConfigList.size() == 0) {
                        log.error("未查询到主体配置列表!jobId:{};loanId:{};主体名称:{}", job.getId(), loanId, creditorEnum.getDesc());
                        NoticeUtils.businessError("未查询到主体配置列表!jobId:" + job.getId() + ";loanId:" + loanId + ";主体名称为:" + creditorEnum.getDesc() + "\n辛苦配置该主体配置!", Lists.newArrayList("zengyuyan"), Lists.newArrayList());
                        throw new ServiceException("未查询到主体配置列表!jobId:" + job.getId() + ";loanId:" + loanId + ";主体:" + creditorEnum);
                    }
                    //几个主体，生成几个base
                    ReceiptChildOrderBase base = this.genReceiptChildOrderBase(creditorEnum, baseInfos.get(0), receiptOrderKey, baseConfigList);
                    baseList.add(base);
                    }
                }
                //成功生成子单，并处理状态
                receiptService.initReceiptChildOrderSuccess(amountList, baseList, list, receiptOrderKey);
            }
            saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
        } catch (ServiceException ex) {
            log.error("{}发票系统生成子单错误!重试", loanId, ex);
            saveJob(JobStatus.NOTICE_MANUAL, ProcessStatus.FAIL, "票系统生成子单错误!重试");
        } catch (Exception e) {
            log.warn("{}发票系统生成子单异常!重试", loanId, e);
            saveJob(JobStatus.INIT, ProcessStatus.INIT, "发票系统生成子单异常!重试");
        }
    }

    /**
     * 查询资金项金额
     *
     * @param config
     * @param receiptBaseInfoNew
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private BigDecimal getReceiptAmount(CreditorAmountConfig config, ReceiptBaseInfo receiptBaseInfoNew) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        BigDecimal receiptAmount;
        if (ReceiptItemCodeAmount.INTEREST_FEE.equals(config.getReceiptItemCode())) {
            //求和
            BigDecimal interest = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.INTEREST.getCode()), ReceiptItemCodeAmount.INTEREST.getDesc());
            BigDecimal funderOverDueInterest = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getCode()), ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getDesc());
            receiptAmount = "WE".equals(receiptBaseInfoNew.getFundCode()) ? funderOverDueInterest : BigDecimalUtils.add(interest, funderOverDueInterest);
        } else if (ReceiptItemCodeAmount.TOTAL_SERVICE_FEE.equals(config.getReceiptItemCode())) {
            BigDecimal mgmtFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.MGMT_FEE.getCode()), ReceiptItemCodeAmount.MGMT_FEE.getDesc());
            BigDecimal overdueInterest = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.OVERDUE_INTEREST.getCode()), ReceiptItemCodeAmount.OVERDUE_INTEREST.getDesc());
            BigDecimal overdueMgmtFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getCode()), ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getDesc());
            BigDecimal inRepayFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.IN_REPAY_FEE.getCode()), ReceiptItemCodeAmount.IN_REPAY_FEE.getDesc());
            receiptAmount = BigDecimalUtils.add(mgmtFee, overdueInterest, overdueMgmtFee, inRepayFee);
        } else if (ReceiptItemCodeAmount.TOTAL_GUARANTOR_FEE.equals(config.getReceiptItemCode())) {
            BigDecimal guaranteeFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.GUARANTEE_FEE.getCode()), ReceiptItemCodeAmount.GUARANTEE_FEE.getDesc());
            BigDecimal commutation = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.COMMUTATION.getCode()), ReceiptItemCodeAmount.COMMUTATION.getDesc());
            receiptAmount = BigDecimalUtils.add(guaranteeFee, commutation);
        } else {
            receiptAmount = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, config.getReceiptItemCode().getCode()), "item");
        }
        return receiptAmount;
    }

    /**
     * 拆分资金项
     *
     * @param creditorAmountConfigList
     * @param receiptBaseInfoNew
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private String splitItem(List<CreditorAmountConfig> creditorAmountConfigList, ReceiptBaseInfo receiptBaseInfoNew) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        JSONObject jsonObject = new JSONObject();
        for (CreditorAmountConfig config : creditorAmountConfigList) {
            if (ReceiptItemCodeAmount.INTEREST_FEE.equals(config.getReceiptItemCode())) {
                //求和
                BigDecimal interest = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.INTEREST.getCode()), ReceiptItemCodeAmount.INTEREST.getDesc());
                BigDecimal funderOverDueInterest = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getCode()), ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getDesc());
                jsonObject.put(ReceiptItemCodeAmount.INTEREST.getCode(), interest.toString());
                if (!"WE".equals(receiptBaseInfoNew.getFundCode())) {
                    jsonObject.put(ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getCode(), funderOverDueInterest.toString());
                }

            } else if (ReceiptItemCodeAmount.TOTAL_SERVICE_FEE.equals(config.getReceiptItemCode())) {
                BigDecimal mgmtFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.MGMT_FEE.getCode()), ReceiptItemCodeAmount.MGMT_FEE.getDesc());
                BigDecimal overdueInterest = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.OVERDUE_INTEREST.getCode()), ReceiptItemCodeAmount.OVERDUE_INTEREST.getDesc());
                BigDecimal overdueMgmtFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getCode()), ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getDesc());
                BigDecimal inRepayFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.IN_REPAY_FEE.getCode()), ReceiptItemCodeAmount.IN_REPAY_FEE.getDesc());
                jsonObject.put(ReceiptItemCodeAmount.MGMT_FEE.getCode(), mgmtFee.toString());
                jsonObject.put(ReceiptItemCodeAmount.OVERDUE_INTEREST.getCode(), overdueInterest.toString());
                jsonObject.put(ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getCode(), overdueMgmtFee.toString());
                jsonObject.put(ReceiptItemCodeAmount.IN_REPAY_FEE.getCode(), inRepayFee.toString());
            } else if (ReceiptItemCodeAmount.TOTAL_GUARANTOR_FEE.equals(config.getReceiptItemCode())) {
                BigDecimal guaranteeFee = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.GUARANTEE_FEE.getCode()), ReceiptItemCodeAmount.GUARANTEE_FEE.getDesc());
                BigDecimal commutation = BigDecimalUtils.parseBigDecimalWithBlankZero(BeanUtils.getProperty(receiptBaseInfoNew, ReceiptItemCodeAmount.COMMUTATION.getCode()), ReceiptItemCodeAmount.COMMUTATION.getDesc());
                jsonObject.put(ReceiptItemCodeAmount.GUARANTEE_FEE.getCode(), guaranteeFee.toString());
                jsonObject.put(ReceiptItemCodeAmount.COMMUTATION.getCode(), commutation.toString());
            }
        }
        return jsonObject.toString();
    }

    /**
     * 构造子单数据
     * 前台不需要配置的，子单为不许处理状态
     *
     * @return
     */
    private ReceiptChildOrderAmount genReceiptChildOrderAmount(Creditor creditor, ReceiptBaseInfo info, CreditorAmountConfig creditorAmountConfig, BigDecimal receiptAmount, String receiptOrderKey, String extItem) {

        ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(receiptOrderKey);
        //需拆分紫金项
        if (itemExtList.contains(creditorAmountConfig.getReceiptItemCode().name())) {

        }
        return ReceiptChildOrderAmount
                .builder()
                .receiptOrderKey(receiptOrderKey)
                .receiptChildOrderKey(SerialNoGenerator.generateSerialNo("CHILD", 12))
                .creditor(creditor.getCreditor())
                .receiptItemCode(creditorAmountConfig.getReceiptItemCode())
                .loanId(info.getLoanId())
                .needAudit(ObjectUtil.isNull(creditor.getNeedAudit()) ? false : true)
                .partnerUserId(info.getPartnerUserId())
                .invoiceKey(info.getInvoiceKey())
                .receiptChannel(creditor.getReceiptChannel())
                .receiptAmount(receiptAmount)
                .uid(info.getUid())
                .priorityLevel(receiptOrder.getPriorityLevel())
                //不需要处理的子单直接置为2
                .status(ReceiptChannel.NOSYSTEM.equals(creditor.getReceiptChannel()) ? ReceiptChildOrderAmountStatus.NODEAL.getStatus() : ReceiptChildOrderAmountStatus.INIT.getStatus())
                .finishTime(ReceiptChannel.NOSYSTEM.equals(creditor.getReceiptChannel()) ? new Date() : null)
                .sendTime(ReceiptChannel.NOSYSTEM.equals(creditor.getReceiptChannel()) ? new Date() : null)
                .sendStatus(ReceiptChannel.NOSYSTEM.equals(creditor.getReceiptChannel()) ? ReceiptStatus.NODEAL.getStatus()
                        : ReceiptStatus.INIT.getStatus())
                .requestTime(new Date())
                .operatorName(creditor.getOperatorName())
                .creditorConfigVersion(creditor.getCreditorConfigVersion())
                .itemExtInfo(extItem)
                .build();
    }

    /**
     * 构造基本信息字段
     *
     * @return
     */
    private ReceiptChildOrderBase genReceiptChildOrderBase(CreditorEnum creditorEnum, ReceiptBaseInfo info, String receiptOrderKey, List<CreditorBaseConfig> baseConfigList) throws Exception {

        ReceiptChildOrderBase base = ReceiptChildOrderBase.builder().creditor(creditorEnum).build();
        List<CreditorBaseConfig> strList = baseConfigList.parallelStream().filter(e -> ItemType.STRING.equals(e.getItemType())).collect(Collectors.toList());
        List<ReceiptBaseInfo> infoList = receiptBaseInfoService.findByLoanIdAndCreditor(info.getLoanId(), creditorEnum);
        //存在配置列表
        if (strList.size() > 0) {
            //查询该主体需要的字段+合同
            for (CreditorBaseConfig creditorBaseConfig : strList) {
                //还款明细若配置，则打标
                if (ReceiptItemCodeBase.REPAY_DETAIL.equals(creditorBaseConfig.getReceiptItemCode())) {
                    base.setNeedRepayDetail("1");
                }
                //若配置开票金额，查询此订单此实体总金额
                else if (ReceiptItemCodeBase.RECEIPT_AMOUNT.equals(creditorBaseConfig.getReceiptItemCode())) {
                    //资金项金额保存
                    BigDecimal repayAmount = infoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    base.setReceiptAmount(repayAmount);
                } else if (ReceiptItemCodeBase.LOAN_DATE.equals(creditorBaseConfig.getReceiptItemCode())) {
                    base.setLoanTime(infoList.get(0).getLoanTime());

                } else if (ReceiptItemCodeBase.PAY_OFF_TIME.equals(creditorBaseConfig.getReceiptItemCode())) {
                    base.setPayoffTime(infoList.get(0).getPayoffTime());
                } else {
                    //获取baseInfo的值，保存到orderBase
                    if (!ReceiptItemCodeBase.RECEIPT_SIDE.equals(creditorBaseConfig.getReceiptItemCode())) {
                        String baseInfo = BeanUtils.getProperty(info, creditorBaseConfig.getReceiptItemCode().getCode());
                        BeanUtils.setProperty(base, creditorBaseConfig.getReceiptItemCode().getCode(), baseInfo);
                    }
                }
            }
        }
        //前台合同配置列表
        List<CreditorBaseConfig> fileList = baseConfigList.parallelStream().filter(e -> ItemType.FILE.equals(e.getItemType())).collect(Collectors.toList());
        if (fileList.size() > 0) {
            //调用业务接口查询列表
            List<HaoHuanRespVo.ResultData.ContractInfo.ContractAddress> addressesList = haoHuanService.getTotalByUniqId(HaoHuanReqVo.builder().loanIds(info.getLoanId()).build());
            for (CreditorBaseConfig config : fileList) {
                //按中文名称文件列表分组insure_letter_path
                Map<String, List<HaoHuanRespVo.ResultData.ContractInfo.ContractAddress>> mapExtInfo = addressesList.stream().filter(x -> StringUtils.isNotBlank(x.getTitle()))
                        .collect(Collectors.groupingBy(e -> e.getTitle()));
                for (String title : mapExtInfo.keySet()) {
                    //特殊资方处理
                    if (CreditorEnum.JX_FG.equals(config.getCreditor())) {
                        if (title.contains("委托担保合同")) {
                            base.setInsureLetterPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                        if (title.contains("个人消费性贷款电子协议")) {
                            base.setLoanContractPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                    } else if (CreditorEnum.YIRONG.equals(config.getCreditor())) {
                        if (title.contains("担保服务合同")) {
                            base.setInsureLetterPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                        if (title.contains("担保咨询及管理服务合同")) {
                            base.setGuaranteeServiceContractPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                    } else if (CreditorEnum.TB_FG.equals(config.getCreditor())) {
                        if (title.contains("担保服务合同")) {
                            base.setInsureLetterPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                        if (title.contains("担保咨询服务合同")) {
                            base.setGuaranteeServiceContractPath(mapExtInfo.get(title).get(0).getUrl());
                        }

                    } else if(CreditorEnum.YNGM_FG.equals(config.getCreditor())){
                        if (title.contains("担保服务合同")) {
                            base.setInsureLetterPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                        if (title.contains("担保咨询及管理服务合同") || title.contains("担保咨询服务合同")) {
                            base.setGuaranteeServiceContractPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                    } else if(CreditorEnum.CNIF.equals(config.getCreditor())){
                        if (title.contains("委托担保合同") || title.contains("担保服务合同")) {
                            base.setInsureLetterPath(mapExtInfo.get(title).get(0).getUrl());
                        }
                    }else {
                        if (title.contains(config.getReceiptItemCode().getDesc())) {
                            BeanUtils.setProperty(base, config.getReceiptItemCode().getCode(), mapExtInfo.get(title).get(0).getUrl());
                        }
                    }
                }
            }
        }
        base.setReceiptOrderKey(receiptOrderKey);
        base.setCreditor(creditorEnum);
        base.setLoanId(info.getLoanId());
        base.setUid(info.getUid());
        base.setInvoiceKey(info.getInvoiceKey());
        base.setPartnerUserId(info.getPartnerUserId());
        base.setCreditorConfigVersion(baseConfigList.get(0).getCreditorConfigVersion());
        return base;
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }

}