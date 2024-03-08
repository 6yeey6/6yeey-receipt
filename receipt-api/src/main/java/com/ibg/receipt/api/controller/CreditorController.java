package com.ibg.receipt.api.controller;

import com.alibaba.fastjson.JSON;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.ReceiptItemCodeAmount;
import com.ibg.receipt.model.receipt.Creditor;
import com.ibg.receipt.service.receipt.CreditorService;
import com.ibg.receipt.service.receipt.complex.CreditorComplexService;
import com.ibg.receipt.shiro.ReceiptSecurityUtils;
import com.ibg.receipt.shiro.ShiroUser;
import com.ibg.receipt.vo.api.creditor.*;
import com.ibg.receipt.vo.api.user.OrganizationListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/24 13:59
 */
@Slf4j
@RestController
@RequestMapping("/creditor/config")
public class CreditorController {

    @Autowired
    private CreditorComplexService creditorComplexService;
    @Autowired
    private CreditorService creditorService;

    @PostMapping("/add")
    public JsonResultVo<?> add(@RequestBody CreditorConfigAddRequestVo vo) {
        log.info("新增开票信息:{}", JSON.toJSONString(vo));
        try {
            vo.check();
            ShiroUser shiroUser = ReceiptSecurityUtils.getShiroUser();
            return this.checkAmounts(vo.getAmounts(),shiroUser,vo);
        } catch (ServiceException e) {
            log.warn("新增开票信息异常", e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e){
            log.error("新增开票信息异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "新增客户开票信息异常");
        }
    }

    @PostMapping("/delete")
    public JsonResultVo<?> delete(@RequestBody CreditorConfigDeleteRequestVo vo) {
        log.info("删除开票信息:{}", JSON.toJSONString(vo));
        try {
            vo.check();
            ShiroUser shiroUser = ReceiptSecurityUtils.getShiroUser();
            creditorComplexService.deleteCreditorConfig(shiroUser, vo);
            return JsonResultVo.success();
        } catch (ServiceException e) {
            log.warn("删除开票信息异常", e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e){
            log.error("删除开票信息异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "删除客户开票信息异常");
        }
    }

    @PostMapping("/query")
    public JsonResultVo<?> query(@RequestBody CreditorConfigQueryRequestVo vo) {
        log.info("查询开票信息:{}", JSON.toJSONString(vo));
        try {
            vo.check();
            return JsonResultVo.success(creditorComplexService.query(vo));
        } catch (ServiceException e) {
            log.warn("查询开票信息异常", e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e){
            log.error("查询开票信息异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "查询客户开票信息异常");
        }
    }


    /**
     * 主体列表
     *
     * @return
     */
    @PostMapping(value = "/creditorList")
    public JsonResultVo<?> creditorList() {
        try {
            List<CreditorListVo> list = new ArrayList<>();
            Stream.of(CreditorEnum.values()).forEach(e -> {
                List<Creditor> list1 = creditorService.findByCreditorAndDeleted(e, false);
                list.add(CreditorListVo.builder().creditor(e.name()).creditorName(e.getDesc()).existCode(list1.size() > 0 ? 1 : 0).build());
                //list.add(CreditorListVo.builder().creditor(e.name()).creditorName(e.getDesc()).build());
            });
            log.info("查询主体列表返回参数:{}", JSON.toJSONString(JsonResultVo.success().addData("list", list)));
            return JsonResultVo.success().addData("list", list);
        } catch (ServiceException se) {
            log.warn("查询主体列表异常，code:{}， message:{}", se.getCode(), se.getMessage());
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            log.error("查询主体列表异常:{}", e.getMessage(), e);
            return JsonResultVo.error();
        }
    }


    /**
     * 验证资金项
     * 息费为利息+资方罚息的总和；
     * 总服务费为服务费+逾期罚息+逾期管理费总和；
     * 总担保费为担保费+代偿金总和；
     * @param amounts
     * @return
     */
    public JsonResultVo<?> checkAmounts(List<String> amounts,ShiroUser shiroUser,CreditorConfigAddRequestVo vo){
        String msg;
        //息费 利息+资方罚息
        if (amounts.contains(ReceiptItemCodeAmount.INTEREST_FEE.name())){
            if (amounts.contains(ReceiptItemCodeAmount.INTEREST.name()) || amounts.contains(ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.name())){
                msg = String.format("选取%s时不能选取%s或者%s", ReceiptItemCodeAmount.INTEREST_FEE.getDesc(),ReceiptItemCodeAmount.INTEREST.getDesc(),ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getDesc());
                return JsonResultVo.error(CodeConstants.C_10101002, msg);
            }
        }
        if (amounts.contains(ReceiptItemCodeAmount.TOTAL_SERVICE_FEE.name())){
            if (amounts.contains(ReceiptItemCodeAmount.MGMT_FEE.name()) || amounts.contains(ReceiptItemCodeAmount.OVERDUE_INTEREST.name())
                    || amounts.contains(ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.name()) || amounts.contains(ReceiptItemCodeAmount.IN_REPAY_FEE.name())){
                msg = String.format("选取%s时不能选取%s或者%s或者%s或者%s", ReceiptItemCodeAmount.TOTAL_SERVICE_FEE.getDesc(),ReceiptItemCodeAmount.MGMT_FEE.getDesc(),ReceiptItemCodeAmount.OVERDUE_INTEREST.getDesc(),ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getDesc(),ReceiptItemCodeAmount.IN_REPAY_FEE.getDesc());
                return JsonResultVo.error(CodeConstants.C_10101002, msg);
            }
        }
        if (amounts.contains(ReceiptItemCodeAmount.TOTAL_GUARANTOR_FEE.name())){
            if (amounts.contains(ReceiptItemCodeAmount.GUARANTEE_FEE.name()) || amounts.contains(ReceiptItemCodeAmount.COMMUTATION.name())){
                msg = String.format("选取%s时不能选取%s或者%s", ReceiptItemCodeAmount.TOTAL_GUARANTOR_FEE.getDesc(),ReceiptItemCodeAmount.GUARANTEE_FEE.getDesc(),ReceiptItemCodeAmount.COMMUTATION.getDesc());
                return JsonResultVo.error(CodeConstants.C_10101002, msg);
            }
        }
        creditorComplexService.add(shiroUser, vo);
        return JsonResultVo.success();
    }


    /**
     * 配置修改接口
     * @param vo
     * @return
     */
    @PostMapping("/update")
    public JsonResultVo<?> update(@RequestBody CreditorConfigUpdateRequestVo vo) {
        log.info("更新开票信息:{}", JSON.toJSONString(vo));
        try {
            vo.check();
            ShiroUser shiroUser = ReceiptSecurityUtils.getShiroUser();
            creditorComplexService.update(vo,shiroUser);
            return JsonResultVo.success();
        } catch (ServiceException e) {
            log.warn("新增开票信息异常", e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e){
            log.error("新增开票信息异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "新增客户开票信息异常");
        }
    }


   @PostMapping("/detail")
    public JsonResultVo<?> creditorDetail(@RequestBody CreditorConfigDetailRequestVo vo) {
       log.info("查询主体开票信息:{}", JSON.toJSONString(vo));
       try {
           vo.check();
           return JsonResultVo.success(creditorComplexService.detail(vo));
       } catch (ServiceException e) {
           log.warn("查询主体信息异常", e);
           return JsonResultVo.error(e.getCode(), e.getMessage());
       } catch (Exception e) {
           log.error("查询主体信息异常", e);
           return JsonResultVo.error(CodeConstants.C_10101002, "新增客户开票信息异常");
       }
   }

    /**
     * 资金项列表
     *
     * @return
     */
    @PostMapping(value = "/receiptItemCodeList")
    public JsonResultVo<?> receiptItemCodeList() {
        try {
            List<OrganizationListVo> list = new ArrayList<>();
            Stream.of(ReceiptItemCodeAmount.values()).forEach(e -> {
                list.add(OrganizationListVo.builder().name(e.name()).value(e.getDesc()).build());
            });
            log.info("查询资金项列表返回报文:{}", JSON.toJSONString(JsonResultVo.success().addData("list", list)));
            return JsonResultVo.success().addData("list", list);
        } catch (ServiceException se) {
            log.warn("查询资金项列表异常，code:{}， message:{}", se.getCode(), se.getMessage());
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            log.error("查询资金项列表异常:{}", e.getMessage(), e);
            return JsonResultVo.error();
        }
    }

}
