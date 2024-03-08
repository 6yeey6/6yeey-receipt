package com.ibg.receipt.service.receipt.complex;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ibg.receipt.base.enums.FunderChannelCode;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.base.exception.ExceptionUtils;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.enums.business.*;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receipt.ReceiptOrderLoan;
import com.ibg.receipt.model.receipt.ReceiptRepayDetail;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;
import com.ibg.receipt.server.ProxyServer;
import com.ibg.receipt.service.receipt.ReceiptBaseInfoService;
import com.ibg.receipt.service.receipt.ReceiptOrderLoanService;
import com.ibg.receipt.service.receipt.ReceiptOrderService;
import com.ibg.receipt.service.receipt.ReceiptRepayDetailService;
import com.ibg.receipt.service.receipt.excel.ReceiptOrderImportExcel;
import com.ibg.receipt.service.receipt.excel.ReceiptOrderImportVo;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderBaseService;
import com.ibg.receipt.util.*;
import com.ibg.receipt.utils.ExcelXlsxReaderWithDefaultHandler;
import com.ibg.receipt.utils.FourFactorUtils;
import com.ibg.receipt.vo.api.fee.ExtFeeVo;
import com.ibg.receipt.vo.api.fee.FeeVo;
import com.ibg.receipt.vo.api.order.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 贷款工单
 * 
 * @author zhangjilong
 */
@Slf4j
@Service
public class LoanOrderService {

    @Autowired
    private ReceiptOrderLoanService receiptOrderLoanService;
    @Autowired
    private ReceiptOrderService receiptOrderService;
    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;
    @Autowired
    private ReceiptChildOrderBaseService receiptChildOrderBaseService;
    @Autowired
    private ReceiptRepayDetailService repayDetailService;
    @Autowired
    private ReceiptBaseInfoService receiptBaseInfoService;
    @Autowired
    private ProxyServer proxyServer;

    public OrderLoanResponseVo queryLoanOrdes(OrderLoanRequestVo requestVo) {

        OrderLoanResponseVo res = new OrderLoanResponseVo();
        Map<String, String> request = Maps.newHashMap();

        // TODO 暂时只有好还业务线
        request.put("partner", "HAOHUAN");
        //H5端增加
        request.put("idCard ", requestVo.getIdCard());
        request.put("partnerUserId", requestVo.getPartnerUserId());

        HttpUtil http = new HttpUtil();
        String url = proxyServer.getUserLoanOrders();
        String result = http.post(url, JsonUtils.toJson(request));
        log.info("调用代理获取贷款工单, url:{}, req:{}, res{}", url, JsonUtils.toJson(request), result);
        JSONObject jsonObject = proxyServer.checkProxyResult(result);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<OrderLoanVo> loanVos = Lists.newArrayList();
        res.setOrderLoanList(loanVos);
        if (!jsonArray.isEmpty()) {
            List<LoanPayOffVo> loanPayOffVos = jsonArray.toJavaList(LoanPayOffVo.class);
            // 是否已经存在
            if (CollectionUtils.isNotEmpty(loanPayOffVos)) {
                loanVos = Lists.newArrayList();

                if (requestVo.getStartDate() != null && requestVo.getEndDate() != null) {
                    loanPayOffVos = loanPayOffVos.stream().filter(o -> o.getPayOffDate() != null && DateUtils.isEffectiveDate(o.getPayOffDate(),
                            new Date(requestVo.getStartDate()), new Date(requestVo.getEndDate()))).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(loanPayOffVos)) {
                        return res;
                    }
                }
                List<OrderLoanVo> finalLoanVos = loanVos;
                loanPayOffVos.forEach(l -> {
                            ReceiptOrderLoan receiptOrderLoan = receiptOrderLoanService
                                    .findByLoanId(l.getPartnerLoanNo());
                            boolean isLocal = receiptOrderLoan != null;
                            OrderLoanVo orderLoanVo = OrderLoanVo.builder()
                                    .customerName(FourFactorUtils.decode(l.getUserName())).funderName(l.getFunderName())
                                    .loanDate(l.getStartDate().getTime()).isLocal(isLocal)
                                    .payoffDate(l.getPayOffDate().getTime())
                                    .loanAmount(l.getLoanAmount().toString()).loanNo(l.getPartnerLoanNo())
                                    .loanStatus(l.getLoanStatus()).partneruserId(l.getPartnerUserId())
                                    .periods(ObjectUtil.isNull(l.getPeriods()) ? 0 : l.getPeriods())
                                    .build();

                            if (isLocal) {
                                orderLoanVo.setCreteDate(receiptOrderLoan.getCreateTime().getTime());
                                // 添加子单开票状态
                                List<ReceiptChildOrderAmount> childOrderAmountList = receiptChildOrderAmountService
                                        .findByLoanId(l.getPartnerLoanNo());
                                if (CollectionUtils.isNotEmpty(childOrderAmountList)) {
                                    setFinishCount(null, orderLoanVo, childOrderAmountList);
                                    childOrderAmountList.stream()
                                            .filter(c -> c.getStatus() == ReceiptChildOrderAmountStatus.FINISH
                                                    .getStatus()).sorted(Comparator
                                            .comparing(ReceiptChildOrderAmount::getFinishTime,
                                                    Comparator.nullsLast(Comparator.reverseOrder())))
                                            .map(ReceiptChildOrderAmount::getFinishTime).findFirst().ifPresent(
                                            finishedDate -> orderLoanVo.setFinishedDate(finishedDate.getTime()));
                                }
                            }
                            finalLoanVos.add(orderLoanVo);
                        });
                res.setOrderLoanList(loanVos);
            }
        }
        return res;
    }

    private void setFinishCount(OrderReceiptQueryResponseVo orderReceiptQueryResponseVo, OrderLoanVo orderLoanVo,
            List<ReceiptChildOrderAmount> childOrderAmountList) {
        if (CollectionUtils.isEmpty(childOrderAmountList)) {
            return;
        }

        int unfinished = 0;
        int finished = 0;
        for (ReceiptChildOrderAmount receiptChildOrder : childOrderAmountList) {
            if (ReceiptChildOrderAmountStatus.FINISHED_STATUS_SET
                    .contains(ReceiptChildOrderAmountStatus.getEnum(receiptChildOrder.getStatus()))) {
                finished++;
            } else {
                unfinished++;
            }
        }
        if (orderReceiptQueryResponseVo != null) {
            orderReceiptQueryResponseVo.setUnfinishedCount(unfinished);
            orderReceiptQueryResponseVo.setFinishedCount(finished);
            orderReceiptQueryResponseVo.setTotalCount(childOrderAmountList.size());
        }
        if (orderLoanVo != null) {
            orderLoanVo.setUnfinishedCount(unfinished);
            orderLoanVo.setFinishedCount(finished);
            orderLoanVo.setTotalCount(childOrderAmountList.size());
        }
    }

    public OrderLoanDetailResponseVo queryLoanOrdeDetails(OrderLoanDetailRequestVo requestVo) {
        OrderLoanDetailResponseVo res = new OrderLoanDetailResponseVo();
        Map<String, Object> request = Maps.newHashMap();

        // TODO 暂时只有好还业务线
        request.put("partner", "HAOHUAN");
        request.put("partnerUserId", requestVo.getPartnerUserId());
        request.put("partnerLoanNos", requestVo.getLoanNos());
        HttpUtil http = new HttpUtil();
        String url = proxyServer.getUserLoanOrderDetails();
        String result = http.post(url, JsonUtils.toJson(request));
        log.info("调用代理获取还款明细，result:{}", JsonUtils.toJson(result));
        JSONObject jsonObject = proxyServer.checkProxyResult(result);
        JSONArray data = jsonObject.getJSONArray("data");
        if (!data.isEmpty()) {
            List<LoanPayOffRepaidDetailVo> loanPayOffRepaidDetailVos = JSONArray.parseArray(data.toJSONString())
                    .toJavaList(LoanPayOffRepaidDetailVo.class);
            // 是否已经存在
            if (CollectionUtils.isNotEmpty(loanPayOffRepaidDetailVos)) {
                List<OrderLoanDetailVo> list = Lists.newArrayList();
                BigDecimal amount = BigDecimal.ZERO;
                for (LoanPayOffRepaidDetailVo detail : loanPayOffRepaidDetailVos) {
                    FeeVo repaidFee = detail.getRepaidFee();
                    OrderLoanDetailVo detailVo = OrderLoanDetailVo.builder().fundName(detail.getFunderName())
                            .fundCode(detail.getFunderCode()).inRepayFee(repaidFee.getInRepayFee())
                            .interest(repaidFee.getInterest()).loanAmount(detail.getLoanAmount())
                            .loanId(detail.getPartnerLoanNo()).loanStatus(detail.getLoanStatus())
                            .loanTime(detail.getStartDate().getTime()).mgmt(repaidFee.getServiceFee())
                            .overInterest(repaidFee.getOverdueInterest()).payOffTime(detail.getPayOffDate().getTime())
                            .build();

                    List<ExtFeeVo> extFeeVos = repaidFee.getExtFeeVos();
                    setExtFee(extFeeVos, detailVo);
                    list.add(detailVo);
                    // 扣除本金
                    amount = BigDecimalUtils.add(repaidFee.getAmount(), amount).subtract(repaidFee.getPrincipal());
                }
                res.setList(list);
                res.setSumLoanAmount(amount.toString());
                res.setUserName(FourFactorUtils.decode(loanPayOffRepaidDetailVos.get(0).getUserName()));
            }
        }
        return res;
    }

    private void setExtFee(List<ExtFeeVo> extFeeVos, OrderLoanDetailVo detailVo) {
        Map<String, List<ExtFeeVo>> listFeeMap = null;
        if (CollectionUtils.isNotEmpty(extFeeVos)) {
            listFeeMap = extFeeVos.stream().collect(Collectors.groupingBy(ExtFeeVo::getFeeExtType));
        }
        Set<String> extFeeSet = Sets.newHashSet("FUNDER_OVERDUE_INTEREST", "GUARANTEE_FEE", "COMMUTATION",
                "OVERDUE_GUARANTEE_FEE");
        for (String extFee : extFeeSet) {
            BigDecimal feeAmount = BigDecimal.ZERO;
            if (MapUtils.isNotEmpty(listFeeMap) && listFeeMap.containsKey(extFee)
                    && CollectionUtils.isNotEmpty(listFeeMap.get(extFee))) {
                feeAmount = listFeeMap.get(extFee).stream().map(ExtFeeVo::getAmount).reduce(BigDecimal.ZERO,
                        BigDecimal::add);
            }
            switch (extFee) {
                case "GUARANTEE_DEPOSIT":
                    break;
                case "GUARANTEE_DEPOSIT_SERVICE_FEE":
                    break;
                case "FUNDER_OVERDUE_INTEREST":
                    detailVo.setFundOverdueInterest(feeAmount);
                    break;
                case "GUARANTEE_FEE":
                    detailVo.setGuarantee(feeAmount);
                    break;
                case "COMMUTATION":
                    detailVo.setCommutation(feeAmount);
                    break;
                case "GRACE_PERIOD_INTEREST":
                    break;
                case "OVERDUE_GUARANTEE_FEE":
                    detailVo.setOverdueGuaranteeFee(feeAmount);
                    break;
                default:
                    break;
            }
        }
    }

    public void saveOrder(OrderLoanCreateRequestVo vo) {

        //if (CollectionUtils.isEmpty(vo.getLoanNos())) {
        //    return;
        //}
        //// 是否已经存在
        //List<ReceiptOrderLoan> receiptOrderLoans = receiptOrderLoanService.findByLoanIds(vo.getLoanNos());
        //if (CollectionUtils.isNotEmpty(receiptOrderLoans)) {
        //    throw new ServiceException(CodeConstants.C_10101002.getCode(), "存在已经创建的工单");
        //}
        //Assert.notBlank(vo.getCreateUser(), "创建人");
        //String receiptOrderKey = UniqueKeyUtils.uniqueKey();
        //ReceiptOrder receiptOrder = new ReceiptOrder();
        //receiptOrder.setReceiptOrderKey(receiptOrderKey);
        //receiptOrder.setCreatorName(vo.getCreateUser());
        //receiptOrder.setStatus(ReceiptStatus.INIT.getStatus());
        //receiptOrder.setRequestTime(new Date());
        //receiptOrder.setUid(vo.getPartnerUserId());
        //receiptOrder.setUserName(FourFactorUtils.encode(vo.getUserName()));
        //receiptOrder.setEmail(vo.getEmail());
        //receiptOrder.setPriorityLevel(vo.getPriorityLevel());
        //receiptOrderService.save(receiptOrder);
        //
        //vo.getLoanNos().forEach(loanNo -> {
        //    ReceiptOrderLoan orderLoan = new ReceiptOrderLoan();
        //    orderLoan.setReceiptOrderKey(receiptOrderKey);
        //    orderLoan.setLoanId(loanNo);
        //    receiptOrderLoanService.save(orderLoan);
        //});
    }

    /**
     * 新版保存兼容H5开票
     * @param vo
     */
    @Transactional
    public void saveOrderList(OrderLoanCreateRequestVo vo,UserSource userSource){
        if (CollectionUtils.isEmpty(vo.getLoanNos())) {
            return;
        }
        //校验订单是否存在
        List<String> loans = vo.getLoanNos().stream().map(a -> a.getLoanNo()).collect(Collectors.toList());
        List<ReceiptOrderLoan> receiptOrderLoans = receiptOrderLoanService.findByLoanIds(loans);
        if (CollectionUtils.isNotEmpty(receiptOrderLoans)) {
            throw new ServiceException(CodeConstants.C_10101008.getCode(), "存在已经创建的工单");
        }
        //用户-放款列表
        List<OrderLoanCreateRequestVo.LoanList> loanNos = vo.getLoanNos();
        //用户纬度分组
        Map<String, List<OrderLoanCreateRequestVo.LoanList>> loanByUserIdMap =
                loanNos.stream()
                        .collect(Collectors.groupingBy(loanList -> loanList.getPartnerUserId()));
        for (String partnerUserId : loanByUserIdMap.keySet()){
            ReceiptOrder receiptOrder = new ReceiptOrder();
            String receiptOrderKey = UniqueKeyUtils.uniqueKey();
            receiptOrder.setReceiptOrderKey(receiptOrderKey);
            receiptOrder.setCreatorName(vo.getCreateUser());
            receiptOrder.setStatus(ReceiptStatus.INIT.getStatus());
            receiptOrder.setRequestTime(new Date());
            receiptOrder.setUid(partnerUserId);
            receiptOrder.setUserName(FourFactorUtils.encode(vo.getUserName()));
            receiptOrder.setEmail(vo.getEmail());
            receiptOrder.setSource(userSource);
            receiptOrder.setPriorityLevel(vo.getPriorityLevel());
            receiptOrderService.save(receiptOrder);
            loanByUserIdMap.get(partnerUserId).forEach(loanNo -> {
                ReceiptOrderLoan orderLoan = new ReceiptOrderLoan();
                orderLoan.setReceiptOrderKey(receiptOrderKey);
                orderLoan.setLoanId(loanNo.getLoanNo());
                receiptOrderLoanService.save(orderLoan);
            });
        }


    }

    public PageVo<OrderReceiptQueryResponseVo> queryReceiptOrders(OrderReceiptQueryRequestVo vo) {
        PageVo<OrderReceiptQueryResponseVo> responseVo = new PageVo<>();
        Page<ReceiptOrder> receiptOrderPage = receiptOrderService.findAllqueryPaged(vo);
        List<OrderReceiptQueryResponseVo> orderReceipts = Lists.newArrayList();
        long total = 0;
        if (receiptOrderPage != null && CollectionUtils.isNotEmpty(receiptOrderPage.getContent())) {
            List<String> receiptOrderKeys = receiptOrderPage.getContent().stream().map(ReceiptOrder::getReceiptOrderKey)
                    .distinct().collect(Collectors.toList());

            // 获取开票总额
            List<ReceiptChildOrderAmount> receiptChildOrders = receiptChildOrderAmountService
                    .findByReceiptOrerKeys(receiptOrderKeys);

            for (ReceiptOrder o : receiptOrderPage.getContent()) {

                List<ReceiptChildOrderAmount> receiptChildOrderAmounts = receiptChildOrders.stream()
                        .filter(c -> o.getReceiptOrderKey().equals(c.getReceiptOrderKey()))
                        .collect(Collectors.toList());

                BigDecimal receipiAmount = receiptChildOrderAmounts.stream()
                        .map(ReceiptChildOrderAmount::getReceiptAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                ReceiptChildOrderAmountStatus receiptStatus = ReceiptChildOrderAmountStatus.getEnum(o.getStatus());
                Date date;
                if (receiptStatus == ReceiptChildOrderAmountStatus.FINISH) {
                    date = o.getFinishTime();
                } else if (receiptStatus == ReceiptChildOrderAmountStatus.NODEAL) {
                    date = o.getCreateTime();
                } else {
                    date = new Date();
                }

                Integer handleDays = DateUtils.differenceDays(date, o.getCreateTime());
                List<ReceiptChildOrderAmount> temp = receiptChildOrderAmounts.stream()
                        .filter(e -> !Objects.isNull(e.getFinishTime())).collect(Collectors.toList());
                Date finishedTime = temp.size() > 0
                        ? temp.stream().max(Comparator.comparing(ReceiptChildOrderAmount::getFinishTime)).get().getFinishTime()
                        : null;
                OrderReceiptQueryResponseVo orderReceiptQueryResponseVo = OrderReceiptQueryResponseVo.builder()
                        .customerName(this.desensitizedName(FourFactorUtils.decode(o.getUserName()))).handleDays(handleDays)
                        .partnerUserId(o.getUid()).receiptOrderKey(o.getReceiptOrderKey())
                        .receiptOrderStatus(receiptStatus.getDesc()).receiptTotalAmount(receipiAmount)
                        .email(Desensitization.emailDesensitization(FourFactorUtils.decode(o.getEmail()))).createUser(FourFactorUtils.decode(o.getCreatorName())).createTime(o.getCreateTime())
                        .finishedTime(finishedTime).priorityLevel(o.getPriorityLevel()).build();

                // 添加子单完成数量
                setFinishCount(orderReceiptQueryResponseVo, null, receiptChildOrderAmounts);
                orderReceipts.add(orderReceiptQueryResponseVo);

            }
            total = receiptOrderPage.getTotalElements();
            // 排序
            orderReceipts = orderReceipts.stream()
                    .sorted(Comparator.comparing(OrderReceiptQueryResponseVo::getCreateTime).reversed()
                            .thenComparing(OrderReceiptQueryResponseVo::getPriorityLevel))
                    .collect(Collectors.toList());
        }

        responseVo.setPageNum(vo.getPageNum());
        responseVo.setPageSize(vo.getPageSize());
        responseVo.setList(orderReceipts);
        responseVo.setTotal(total);
        return responseVo;
    }

    public String desensitizedName(String userName){
        return userName.replace(StringUtils.left(userName,1),"*");
    }

    public List<ReceiptOrderImportVo> importReceiptOrder(MultipartFile file) {

        List<ReceiptOrderImportVo> receiptOrderVos = Lists.newArrayList();
        // 解析
        ExcelXlsxReaderWithDefaultHandler excelAbstract = new ExcelXlsxReaderWithDefaultHandler();
        try (InputStream inputStream = file.getInputStream()) {
            List<List<String>> dataList = excelAbstract.process(inputStream);
            for (int i = 0; i < dataList.size(); i++) {
                ReceiptOrderImportVo receiptOrderImportVo = convertReceiptOrderImportVo(dataList.get(i), i + 1);
                if (receiptOrderImportVo == null) {
                    continue;
                }
                receiptOrderVos.add(receiptOrderImportVo);
            }
        } catch (Exception e) {
            log.error("发票历史数据导入异常", e);
            throw ExceptionUtils.commonError("发票历史数据导入异常: " + e.getMessage());
        }

        return receiptOrderVos;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveImportData(List<ReceiptOrderImportVo> receiptOrderVos) {

        if (CollectionUtils.isEmpty(receiptOrderVos)) {
            log.info("要导入的数据为空");
            return;
        }

        // 保存
        Map<String, List<ReceiptOrderImportVo>> operatorUserMap = receiptOrderVos.stream()
                .collect(Collectors.groupingBy(r -> r.getUid() + "#" + r.getOperator()));

        for (Map.Entry<String, List<ReceiptOrderImportVo>> entry : operatorUserMap.entrySet()) {

            String operator = StringUtils.split(entry.getKey(), "#")[1];
            String partnerUserId = StringUtils.split(entry.getKey(), "#")[0];
            List<ReceiptOrderImportVo> receiptOrderImportVos = entry.getValue();
            List<String> partnerLoanNos = receiptOrderImportVos.stream().map(ReceiptOrderImportVo::getPartnerLoanNo)
                    .distinct().collect(Collectors.toList());

            // 判断是否存在
            List<ReceiptOrderLoan> receiptOrderLoans = receiptOrderLoanService.findByLoanIds(partnerLoanNos);
            List<String> existsReceipt = null;
            if (CollectionUtils.isNotEmpty(receiptOrderLoans)) {
                existsReceipt = receiptOrderLoans.stream().map(ReceiptOrderLoan::getLoanId)
                        .collect(Collectors.toList());
            }

            if (CollectionUtils.isNotEmpty(existsReceipt) && existsReceipt.size() == partnerLoanNos.size()) {
                log.error("借款列表: {}, 开票信息已经存在.", JsonUtils.toJson(existsReceipt));
                continue;
            }

            Map<String, List<ReceiptOrderImportVo>> partnerLoanNoMap = receiptOrderImportVos.stream()
                    .collect(Collectors.groupingBy(ReceiptOrderImportVo::getPartnerLoanNo));

            ReceiptOrderImportVo receipt = receiptOrderImportVos.get(0);
            boolean isDealing = receiptOrderImportVos.stream().map(ReceiptOrderImportVo::getReceiptStatus)
                    .anyMatch(r -> ReceiptStatus.DEALING.getDesc().equals(r));

            // 主单
            String receiptOrderKey = "IMPORT" + UniqueKeyUtils.uniqueKey();
            ReceiptOrder receiptOrder = new ReceiptOrder();
            receiptOrder.setReceiptOrderKey(receiptOrderKey);
            receiptOrder.setMobile(FourFactorUtils.encode(receipt.getMobile()));
            receiptOrder.setCreatorName(operator);
            receiptOrder.setOperatorName(operator);
            receiptOrder.setStatus(isDealing ? ReceiptStatus.DEALING.getStatus() : ReceiptStatus.SUCCESS.getStatus());
            receiptOrder.setRequestTime(receipt.getReceiptDate());
            receiptOrder.setFinishTime(isDealing ? null : receipt.getFinishedDate());
            receiptOrder.setUid(partnerUserId);
            receiptOrder.setUserName(FourFactorUtils.encode(receipt.getUserName()));
            receiptOrder.setEmail(EncryptUtil.getDecoded(receipt.getEmail()));
            receiptOrder.setPriorityLevel(PriorityLevelEnum.NORMAL.getLevel());
            receiptOrder.setCreateTime(receipt.getReceiptDate());
            receiptOrder.setUpdateTime(receipt.getReceiptDate());
            receiptOrderService.saveReceiptOrder(receiptOrder);

            // 保存借款
            for (Map.Entry<String, List<ReceiptOrderImportVo>> loanEntry : partnerLoanNoMap.entrySet()) {
                ReceiptOrderLoan receiptOrderLoan = receiptOrderLoanService.findByLoanId(loanEntry.getKey());
                if (receiptOrderLoan != null) {
                    log.error("开票信息已经存在, loanId:{}", loanEntry.getKey());
                    continue;
                }
                ReceiptOrderLoan orderLoan = new ReceiptOrderLoan();
                orderLoan.setReceiptOrderKey(receiptOrderKey);
                orderLoan.setLoanId(loanEntry.getKey());
                orderLoan.setCreateTime(receipt.getReceiptDate());
                orderLoan.setUpdateTime(receipt.getReceiptDate());
                receiptOrderLoanService.saveReceiptOrderLoan(orderLoan);
                try {
                    // 保存子单
                    ReceiptRepayDetail repayDetail = ReceiptRepayDetail.builder().loanId(loanEntry.getKey())
                            .amount(BigDecimal.ZERO)
                            .build();
                    ReceiptBaseInfo receiptBase = new ReceiptBaseInfo();
                    boolean flag = true;
                    for (ReceiptOrderImportVo loan : loanEntry.getValue()) {

                        ReceiptStatus receiptStatus = ReceiptStatus.getEnumByDesc(loan.getReceiptStatus());

                        for (Map.Entry<String, BigDecimal> receiptItemEntry : loan.getFeeDetail().entrySet()) {
                            if (BigDecimalUtils.isEquals(receiptItemEntry.getValue(), BigDecimal.ZERO)) {
                                continue;
                            }
                            // receipt_child_order_amount (一个资金项一条记录)
                            ReceiptChildOrderAmount childOrderAmount = ReceiptChildOrderAmount.builder()
                                    .receiptOrderKey(receiptOrderKey)
                                    .receiptChildOrderKey(SerialNoGenerator.generateSerialNo("CHILD", 12))
                                    .creditor(CreditorEnum.valueOf(loan.getPaySubject()))
                                    .receiptItemCode(ReceiptItemCodeAmount
                                            .getReceiptItemCodeAmountByCode(receiptItemEntry.getKey()))
                                    .loanId(loan.getPartnerLoanNo()).needAudit(true)
                                    .receiptChannel(ReceiptChannel.valueOf(loan.getReceiptChannel()))
                                    .receiptAmount(receiptItemEntry.getValue()).uid(partnerUserId)
                                    .status(receiptStatus.getStatus())
                                    .sendStatus(
                                            receiptStatus == ReceiptStatus.SUCCESS ? ReceiptStatus.SUCCESS.getStatus()
                                                    : ReceiptStatus.INIT.getStatus())
                                    .requestTime(loan.getReceiptDate()).finishTime(loan.getFinishedDate())
                                    .sendTime(receiptStatus == ReceiptStatus.SUCCESS ? loan.getFinishedDate() : null)
                                    .operatorName(operator).creditorConfigVersion("20221015000000")
                                    .partnerUserId(partnerUserId).build();
                            receiptChildOrderAmountService.save(childOrderAmount);
                            buildReceiptRepayDetail(repayDetail, receiptBase, receiptItemEntry);
                        }

                        // receipt_child_order_base
                        ReceiptChildOrderBase childOrderBase = ReceiptChildOrderBase.builder()
                                .account(FourFactorUtils.encode(loan.getMobile())).address(loan.getUserAddress())
                                .creditor(CreditorEnum.valueOf(loan.getPaySubject()))
                                .dt(DateUtils.format(loan.getReceiptDate(), DateUtils.DATE_FORMAT_PATTERN))
                                .fundCode(loan.getFunderCode())
                                .fundName(FunderChannelCode.valueOf(loan.getFunderCode()).toString())
                                .inRepayPeriod(StringUtils.isNotBlank(loan.getInRepayStartPeriod())
                                        ? Integer.parseInt(loan.getInRepayStartPeriod())
                                        : null)
                                .loanAmount(loan.getLoanAmount()).loanId(loan.getPartnerLoanNo())
                                .loanTime(loan.getStartDate()).payoffTime(loan.getPayOffDate())
                                .period(Integer.parseInt(loan.getLoanPeriod())).receiptAmount(loan.getReceiptAmount())
                                .repayAmount(loan.getReceiptAmount()).repayStatus("已结清")
                                .receiptOrderKey(receiptOrderKey).uid(loan.getUid()).userName(FourFactorUtils.encode(loan.getUserName()))
                                .creditorConfigVersion("20221015000000").partnerUserId(partnerUserId).uid(partnerUserId)
                                .build();
                        receiptChildOrderBaseService.save(childOrderBase);

                        if (flag) {
                            repayDetail.setPayoffTime(loan.getPayOffDate());
                            repayDetail.setPeriod(Integer.parseInt(loan.getLoanPeriod()));
                            org.springframework.beans.BeanUtils.copyProperties(childOrderBase, receiptBase);
                            flag = false;
                        }
                    }
                    // receipt_repay_detail
                    repayDetailService.save(repayDetail);
                    // receipt_base_info
                    receiptBaseInfoService.save(receiptBase);
                } catch (Exception e) {
                    log.error("借款:{}, 保存子单异常", loanEntry.getKey(), e);
                    throw ServiceException.exception(CodeConstants.C_10101002, e.getMessage());
                }
            }
        }
    }

    private void buildReceiptRepayDetail(ReceiptRepayDetail repayDetail, ReceiptBaseInfo receiptBase, Map.Entry<String, BigDecimal> receiptItemEntry) {

        switch (receiptItemEntry.getKey()) {
            case "repayInterest":
                repayDetail.setInterest(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getInterest()));
                receiptBase.setRepayInterest(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayInterest()));
                break;
            case "repayMgmtFee":
                repayDetail.setMgmtFee(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getMgmtFee()));
                receiptBase.setRepayMgmtFee(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayMgmtFee()));
                break;
            case "repayOverdueInterest":
                repayDetail.setOverdueInterest(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getOverdueInterest()));
                receiptBase.setRepayOverdueInterest(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayOverdueInterest()));
                break;
            case "repayOverdueMgmtFee":
                repayDetail.setOverdueMgmtFee(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getOverdueMgmtFee()));
                receiptBase.setRepayOverdueMgmtFee(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayOverdueMgmtFee()));
                break;
            case "repayInRepayFee":
                repayDetail.setInRepayFee(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getInRepayFee()));
                receiptBase.setRepayInRepayFee(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayInRepayFee()));
                break;
            case "repayFunderOverdueInterest":
                repayDetail.setFundOverdueInterest(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getFundOverdueInterest()));
                receiptBase.setRepayFunderOverdueInterest(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayFunderOverdueInterest()));
                break;
            case "repayGuaranteeFee":
                repayDetail.setGuaranteeFee(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getGuaranteeFee()));
                receiptBase.setRepayGuaranteeFee(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayGuaranteeFee()));
                break;
            case "repayGuaranteeDeposit":
                log.error("无保障金字段, loan:{}", repayDetail.getLoanId());
                break;
            case "repayOverdueGuaranteeFee":
                repayDetail.setRepayOverdueGuaranteeFee(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getRepayOverdueGuaranteeFee()));
                receiptBase.setRepayOverdueGuaranteeFee(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayOverdueGuaranteeFee()));
                break;
            case "repayCommutation":
                receiptBase.setRepayCommutation(BigDecimalUtils.add(receiptItemEntry.getValue(), receiptBase.getRepayCommutation()));
                repayDetail.setCommutation(BigDecimalUtils.add(receiptItemEntry.getValue(), repayDetail.getCommutation()));
                break;
            default:
                log.error("填充借款:{}还款明细, 不支持的资金项:{}", repayDetail.getLoanId(), receiptItemEntry.getKey());
                throw new ServiceException(CodeConstants.C_10101002.getCode(), "不支持的资金项！");
        }
        repayDetail.setAmount(BigDecimalUtils.add(repayDetail.getAmount(), receiptItemEntry.getValue()));
    }

    private ReceiptOrderImportVo convertReceiptOrderImportVo(List<String> cellValues, int rowNum) {
        ReceiptOrderImportVo vo = new ReceiptOrderImportVo();
        Map<String, BigDecimal> feeDetail = Maps.newHashMap();
        BigDecimal amount = BigDecimal.ZERO;
        for (int i = 0; i < ReceiptOrderImportExcel.EXCEL_CONFIG.length; i++) {
            try {
                String name = ReceiptOrderImportExcel.EXCEL_CONFIG[i];
                String value = cellValues.get(i);
                if (!ReceiptOrderImportExcel.ALLOW_NULL_LIST.contains(name) && StringUtils.isBlank(value)) {
                    throw ExceptionUtils.commonError("该单元格不能为空");
                }
                if (ReceiptOrderImportExcel.DATE_FORMAT_LIST.contains(name)) {
                    if (ReceiptOrderImportExcel.ALLOW_NULL_DATE_LIST.contains(name) && StringUtils.isBlank(value)) {
                        continue;
                    }
                    Date date;
                    if (ReceiptOrderImportExcel.DATE_FORMAT_LIST_NO_MUNITE.contains(name)) {
                        date = DateUtils.formatToDate(value, DateUtils.DATE_FORMAT_PATTERN);
                    } else {
                        date = DateUtils.formatToDate(value, DateUtils.DATE_TIME_FORMAT_PATTERN);
                    }
                    BeanUtils.setProperty(vo, name, date);
                } else if (ReceiptOrderImportExcel.BIGDECIMAL_FORMAT_LIST.contains(name)) {
                    BigDecimal bigDecimal;
                    try {
                        bigDecimal = new BigDecimal(value);
                    } catch (Exception e) {
                        throw ExceptionUtils.commonError("金额类型不正确");
                    }
                    if (ReceiptOrderImportExcel.BIGDECIMAL_FORMAT_EXCLUDE_CAPITAL_LIST.contains(name)) {
                        BeanUtils.setProperty(vo, name, bigDecimal);
                    } else {
                        if (BigDecimalUtils.isEquals(bigDecimal, BigDecimal.ZERO)) {
                            continue;
                        }
                        feeDetail.put(name, bigDecimal);
                        amount = BigDecimalUtils.add(amount, bigDecimal);
                    }
                } else {
                    BeanUtils.setProperty(vo, name, value);
                }
            } catch (Exception e) {
                log.warn("读取Cell[{}行{}列]出错", rowNum, i + 1, e);
                throw ExceptionUtils.commonError(String.format("读取[%s]行[%s]列出错：%s", rowNum, i + 1, e.getMessage()));
            }
        }
        if (BigDecimalUtils.isEquals(amount, BigDecimal.ZERO)) {
            log.warn("读取Cell[{}行], 开票资金项金额为0, 不进行保存", rowNum);
            return null;
        }
        vo.setFeeDetail(feeDetail);
        return vo;
    }


    public static <k, v> List<HashMap<k, v>> mapChunk(HashMap<k, v> chunkMap, int chunkNum) {
        if (chunkMap == null || chunkNum <= 0) {
            return null;
        }
        Set<k> keySet = chunkMap.keySet();
        Iterator<k> iterator = keySet.iterator();
        int i = 1;
        List<HashMap<k, v>> total = new ArrayList<>();
        HashMap<k, v> tem = new HashMap<>();
        while (iterator.hasNext()) {
            k next = iterator.next();
            tem.put(next, chunkMap.get(next));
            if (i == chunkNum) {
                total.add(tem);
                tem = new HashMap<>();
                i = 0;
            }
            i++;
        }
        if (MapUtils.isNotEmpty(tem)) {
            total.add(tem);
        }
        return total;
    }

}
