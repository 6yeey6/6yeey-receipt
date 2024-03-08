package com.ibg.receipt.api.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ibg.commons.log.LogInfo;
import com.ibg.commons.log.LogInfos;
import com.ibg.commons.log.LogKey;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.UserSource;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.service.receipt.complex.LoanOrderService;
import com.ibg.receipt.service.receipt.complex.ShiroUserComplexService;
import com.ibg.receipt.service.receipt.excel.ReceiptOrderImportVo;
import com.ibg.receipt.service.receipt.remote.CustomerRemoteService;
import com.ibg.receipt.shiro.ReceiptSecurityUtils;
import com.ibg.receipt.shiro.RedisSessionDAO;
import com.ibg.receipt.shiro.ShiroConstant;
import com.ibg.receipt.shiro.ShiroUser;
import com.ibg.receipt.util.JsonUtils;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.log.SecOperationLogVO;
import com.ibg.receipt.vo.api.order.OrderLoanCreateRequestVo;
import com.ibg.receipt.vo.api.order.OrderLoanDetailRequestVo;
import com.ibg.receipt.vo.api.order.OrderLoanDetailResponseVo;
import com.ibg.receipt.vo.api.order.OrderLoanRequestVo;
import com.ibg.receipt.vo.api.order.OrderLoanResponseVo;
import com.ibg.receipt.vo.api.order.OrderReceiptQueryRequestVo;
import com.ibg.receipt.vo.api.order.OrderReceiptQueryResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 贷款工单
 * 
 * @author zhangjilong
 */

@Slf4j
@RestController
@RequestMapping("{source}/order")
public class ReceiptOrderController {

    @Autowired
    private LoanOrderService loanOrderService;

    @Autowired
    private ShiroUserComplexService shiroUserComplexService;

    Logger secLog = LoggerFactory.getLogger("SecOperationLogger");

    @LogInfos({ @LogInfo(key = LogKey.MODULE, value = "用户贷款单查询") })
    @PostMapping("/loan/query")
    public JsonResultVo<?> queryUserReceipt(HttpServletRequest request, @PathVariable("source") String source,
            @RequestBody OrderLoanRequestVo vo) {
        try {
            vo.checkParams();
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUserComplexService.getShiroUser(request);
            }
            OrderLoanResponseVo response = loanOrderService.queryLoanOrdes(vo);

            // 安全日志打印vo
            SecOperationLogVO logVO = new SecOperationLogVO(
                    response.getOrderLoanList().stream().map(order -> order.getPartneruserId()).collect(Collectors.toList()), null
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.PLAINTEXT.getValue()
                    , "发票管理-创建发票工单"
            );
            secLog.info(JSON.toJSONString(logVO));
            return JsonResultVo.success(response);
        } catch (ServiceException e) {
            log.warn("用户贷款单查询, code：{}，message：{}", e.getCode(), e.getMessage());
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户贷款单查询异常！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }

    @LogInfos({ @LogInfo(key = LogKey.MODULE, value = "用户贷款单明细查询") })
    @PostMapping("/loan/queryDetail")
    public JsonResultVo<?> queryLoanDetail(HttpServletRequest request, @PathVariable("source") String source,
            @RequestBody OrderLoanDetailRequestVo vo) {
        try {
            vo.checkParams();
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUserComplexService.getShiroUser(request);
            }
            OrderLoanDetailResponseVo response = loanOrderService.queryLoanOrdeDetails(vo);
            return JsonResultVo.success(response);
        } catch (ServiceException e) {
            log.warn("用户贷款单明细查询, code：{}，message：{}", e.getCode(), e.getMessage());
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户贷款单明细查询！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }

    @LogInfos({ @LogInfo(key = LogKey.MODULE, value = "用户贷款单创建") })
    @PostMapping("/loan/create")
    public JsonResultVo<?> createReceipt(HttpServletRequest request, @PathVariable("source") String source,
            @RequestBody OrderLoanCreateRequestVo vo) {
        try {
            log.info("用户贷款单创建:{}", JsonUtils.toJson(vo) );
            vo.checkParams(source);
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            ShiroUser shiroUser;
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUser = shiroUserComplexService.getShiroUser(request);
                vo.setCreateUser(shiroUser.getUserName());
            } else if(userSource == UserSource.MANAGEMENT_PLATFORM){
                shiroUser = ReceiptSecurityUtils.getShiroUser();
            }else{
                log.info("发票系统-业务系统开票:{}",vo.getPartnerUserId());
            }
            log.info("用户贷款单创建，准备保存贷款单:{}", JsonUtils.toJson(vo) );
            loanOrderService.saveOrderList(vo,userSource);
            return JsonResultVo.success();
        } catch (ServiceException e) {
            log.warn("用户贷款单创建, code：{}，message：{}", e.getCode(), e.getMessage());
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户贷款单创建！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }

    @LogInfos({ @LogInfo(key = LogKey.MODULE, value = "工单查询") })
    @PostMapping("/receipt/query")
    public JsonResultVo<?> queryReceipt(HttpServletRequest request, @PathVariable("source") String source,
        @RequestBody OrderReceiptQueryRequestVo vo) {
        try {
            log.info("请求来源:{}，业务参数:{}", source, JsonUtils.toJson(vo));
            vo.checkParams();
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUserComplexService.getShiroUser(request);
            }
            PageVo<OrderReceiptQueryResponseVo> responseVo = loanOrderService.queryReceiptOrders(vo);
            // 安全日志打印vo
            SecOperationLogVO logVO = new SecOperationLogVO(
                    responseVo.getList().stream().map(order -> order.getPartnerUserId()).collect(Collectors.toList()), null
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.PLAINTEXT.getValue()
                    , "发票管理-开票信息管理-开票信息管理"
            );
            secLog.info(JSON.toJSONString(logVO));
            return JsonResultVo.success(responseVo);
        } catch (ServiceException e) {
            log.warn("工单查询, code：{}，message：{}", e.getCode(), e.getMessage());
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("工单查询！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }


    @LogInfos({ @LogInfo(key = LogKey.MODULE, value = "开票数据导入") })
    @PostMapping("/receipt/import")
    public JsonResultVo<?> receiptImport(HttpServletRequest request, @PathVariable("source") String source,
            @RequestParam(required = true, value = "file") MultipartFile file) {
        try {
            Assert.notNull(file, "文件");
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUserComplexService.getShiroUser(request);
            }

            // 检查文件类型
            String excelSuffix = StringUtils.parseMaterialType(file.getOriginalFilename());
            if (!"xlsx".equalsIgnoreCase(excelSuffix)) {
                return JsonResultVo.error(CodeConstants.C_10101002.getCode(), "不支持的文件类型");
            }
            List<ReceiptOrderImportVo> receiptOrderImportVos = loanOrderService.importReceiptOrder(file);
            loanOrderService.saveImportData(receiptOrderImportVos);
            return JsonResultVo.success();
        } catch (ServiceException e) {
            log.warn("开票数据导入, code：{}，message：{}", e.getCode(), e.getMessage());
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("开票数据导入！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }
}
