package com.ibg.receipt.service.receipt.complex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.enums.business.*;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.model.receipt.*;
import com.ibg.receipt.service.receipt.CreditorAmountConfigService;
import com.ibg.receipt.service.receipt.CreditorBaseConfigService;
import com.ibg.receipt.service.receipt.CreditorService;
import com.ibg.receipt.shiro.ShiroUser;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.DateUtils;
import com.ibg.receipt.vo.api.creditor.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/24 14:08
 */
@Slf4j
@Service
public class CreditorComplexService {

    @Autowired
    private CreditorService creditorService;

    @Autowired
    private CreditorBaseConfigService creditorBaseConfigService;

    @Autowired
    private CreditorAmountConfigService creditorAmountConfigService;

    public PageVo<CreditorConfigQueryResponseVo> query(CreditorConfigQueryRequestVo vo) {
        List<Creditor> pageCreditors;
        if(vo.getCreditors() != null && vo.getCreditors().size() > 0) {
            pageCreditors = creditorService.findByCreditorInAndDeleted(vo.getCreditorEnums(), false);
        } else {
            pageCreditors = creditorService.findByDeleted(false);
        }

        PageVo<CreditorConfigQueryResponseVo> pageVo;
        //if (CollectionUtils.isEmpty(pageCreditors.getContent())) {
        //    pageVo = new PageVo(null, null, vo.getPageNum(), vo.getPageSize());
        //} else {
            List<CreditorConfigQueryResponseVo> responseVos = buildCreditorConfigQueryResponseVos(pageCreditors);
            pageVo = new PageVo();
            pageVo.setPageNum(vo.getPageNum());
            pageVo.setPageSize(vo.getPageSize());
            pageVo.setList(this.getPageList(responseVos,vo.getPageNum(),vo.getPageSize()));
            pageVo.setTotal((long)responseVos.size());
        //}
        return pageVo;
    }

    private List<CreditorConfigQueryResponseVo> buildCreditorConfigQueryResponseVos(List<Creditor> creditors) {

        List<CreditorConfigQueryResponseVo> responseVos = new ArrayList<>();

        Map<CreditorEnum, List<Creditor>> receiptBaseInfoMap = creditors.stream()
                .collect(Collectors.groupingBy(Creditor::getCreditor));
        for (CreditorEnum creditorEnum : receiptBaseInfoMap.keySet()) {
            List<Creditor> list = creditorService.findByCreditorAndDeleted(creditorEnum, false);
            Creditor creditor = list.get(0);
            List<CreditorBaseConfig> creditorBaseConfigs = creditorBaseConfigService.findByCreditorAndCreditorConfigVersionAndDeleted(
                    creditor.getCreditor(), creditor.getCreditorConfigVersion(), false);

            List<CreditorAmountConfig> creditorAmountConfigs = creditorAmountConfigService.findByCreditorAndCreditorConfigVersionAndDeleted(
                    creditor.getCreditor(), creditor.getCreditorConfigVersion(), false);

            CreditorConfigQueryResponseVo responseVo = buildCreditorConfigQueryResponseVo(creditor, creditorBaseConfigs, creditorAmountConfigs);
            List<String> loanIds = list.parallelStream().map(Creditor::getUserName).collect(Collectors.toList());
            responseVo.setCapitalOperation(new HashSet<>(loanIds));
            responseVo.setBelongOrg(creditor.getOrganization().name());
            responseVo.setIsReceipt(ReceiptChannel.NOSYSTEM.equals(creditor.getReceiptChannel()) ? 0 : 1);
            responseVos.add(responseVo);
        }
        //for(Creditor creditor : creditors) {
        //
        //    List<CreditorBaseConfig> creditorBaseConfigs = creditorBaseConfigService.findByCreditorAndCreditorConfigVersionAndDeleted(
        //            creditor.getCreditor(), creditor.getCreditorConfigVersion(), false);
        //
        //    List<CreditorAmountConfig> creditorAmountConfigs = creditorAmountConfigService.findByCreditorAndCreditorConfigVersionAndDeleted(
        //            creditor.getCreditor(), creditor.getCreditorConfigVersion(), false);
        //
        //    CreditorConfigQueryResponseVo responseVo = buildCreditorConfigQueryResponseVo(creditor, creditorBaseConfigs, creditorAmountConfigs);
        //    responseVos.add(responseVo);
        //}
        return responseVos;
    }

    /**
     * 内存分页
     *
     * @param list
     * @param pageNum
     * @param pageSize
     * @return
     */
    private List<CreditorConfigQueryResponseVo> getPageList(List<CreditorConfigQueryResponseVo> list, Integer pageNum, Integer pageSize) {
        int start = (pageNum - 1) * pageSize;
        int limit = pageNum * pageSize;
        // 从缓存中获取全量数据
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        if (limit > list.size()) {
            limit = list.size();
        }
        return list.subList(start, limit);
    }

    private CreditorConfigQueryResponseVo buildCreditorConfigQueryResponseVo(Creditor creditor, List<CreditorBaseConfig> creditorBaseConfigs,
        List<CreditorAmountConfig> creditorAmountConfigs) {
        CreditorConfigQueryResponseVo responseVo = new CreditorConfigQueryResponseVo();

        responseVo.setCreditor(creditor.getCreditor().name());
        responseVo.setCreditorConfigVersion(creditor.getCreditorConfigVersion());
        responseVo.setCreditorName(creditor.getCreditor().getDesc());

        if(CollectionUtils.isNotEmpty(creditorBaseConfigs)) {
            responseVo.setItems(creditorBaseConfigs.stream().map(creditorBaseConfig -> creditorBaseConfig.getReceiptItemCode().name()).collect(Collectors.toList()));
        }

        if(CollectionUtils.isNotEmpty(creditorAmountConfigs)) {
            List<String> receiptItemCodeAmounts = creditorAmountConfigs.stream().map(creditorAmountConfig -> creditorAmountConfig.getReceiptItemCode().name()).collect(Collectors.toList());
            responseVo.setAmounts(receiptItemCodeAmounts);
        }

        return responseVo;
    }

    @Transactional
    public void add(ShiroUser shiroUser, CreditorConfigAddRequestVo vo) {

        checkAdd(vo.getCreditorEnum());

        List<Creditor> creditorList = buildCreditor(shiroUser, vo);
        for (Creditor creditor : creditorList){
            creditorService.save(creditor);
        }
        List<CreditorBaseConfig> creditorBaseConfigs = vo.getReceiptItemCodeBases().stream().map(receiptItemCodeBase ->
                buildCreditorBaseConfig(true,null,creditorList.get(0), receiptItemCodeBase)).collect(Collectors.toList());
        creditorBaseConfigService.batchSave(creditorBaseConfigs);

        List<CreditorAmountConfig> creditorAmountConfigs = vo.getReceiptItemCodeAmounts().stream().map(receiptItemCodeAmount ->
                buildCreditorAmountConfig(true,null,creditorList.get(0), receiptItemCodeAmount)).collect(Collectors.toList());

        creditorAmountConfigService.batchSave(creditorAmountConfigs);
    }

    public void deleteCreditorConfig(ShiroUser shiroUser, CreditorConfigDeleteRequestVo vo) {

        Creditor creditor = deleteCreditor(shiroUser, vo);

        deleteCreditorBaseConfigs(creditor);

        deleteCreditorAmountConfigs(creditor);

    }

    private Creditor deleteCreditor(ShiroUser shiroUser, CreditorConfigDeleteRequestVo vo) {
        List<Creditor> creditorList = creditorService.findByCreditorAndcreditorConfigVersionAndDeleted(vo.getCreditorEnum(),
                vo.getCreditorConfigVersion(), false);
        if(creditorList.size() == 0) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "不存在需要删除的开票配置信息");
        }
        for (Creditor creditor : creditorList){
            creditor.setDeleted(true);
            creditor.setOperatorName(shiroUser.getUserName());
            creditorService.update(creditor);
        }
        return creditorList.get(0);
    }

    private void deleteCreditorBaseConfigs(Creditor creditor) {
        List<CreditorBaseConfig> creditorBaseConfigs = creditorBaseConfigService.findByCreditorAndCreditorConfigVersionAndDeleted(creditor.getCreditor(),
                creditor.getCreditorConfigVersion(), false);
        creditorBaseConfigs.stream().forEach(creditorBaseConfig -> creditorBaseConfig.setDeleted(true));

        creditorBaseConfigService.update(creditorBaseConfigs);

    }

    private void deleteCreditorAmountConfigs(Creditor creditor) {
        List<CreditorAmountConfig> creditorAmountConfigs = creditorAmountConfigService.findByCreditorAndCreditorConfigVersionAndDeleted(creditor.getCreditor(),
                creditor.getCreditorConfigVersion(), false);

        if(CollectionUtils.isNotEmpty(creditorAmountConfigs)) {

            creditorAmountConfigs.stream().forEach(creditorAmountConfig -> creditorAmountConfig.setDeleted(true));

            creditorAmountConfigService.update(creditorAmountConfigs);
        }

    }

    private void checkAdd(CreditorEnum creditorEnum) {
        List<Creditor> creditors = creditorService.findByCreditorAndDeleted(creditorEnum, false);
        if(CollectionUtils.isNotEmpty(creditors)) {
            log.warn("已经存在对应的主体开票信息，不允许再次新增", JSON.toJSONString(creditors));
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "已经存在对应的主体开票信息，不允许再次新增");
        }
        List<CreditorBaseConfig> creditorBaseConfigs = creditorBaseConfigService.findByCreditorAndDeleted(creditorEnum, false);
        if(CollectionUtils.isNotEmpty(creditorBaseConfigs)) {
            log.error("已经存在对应的主体开票基础信息，不允许再次新增", JSON.toJSONString(creditorBaseConfigs));
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "已经存在对应的主体开票基础信息，不允许再次新增");
        }

        List<CreditorAmountConfig> creditorAmountConfigs = creditorAmountConfigService.findByCreditorAndDeleted(creditorEnum, false);
        if(CollectionUtils.isNotEmpty(creditorAmountConfigs)) {
            log.error("已经存在对应的资金开票基础信息，不允许再次新增", JSON.toJSONString(creditorAmountConfigs));
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "已经存在对应的资金开票基础信息，不允许再次新增");
        }
    }

    private static List<Creditor> buildCreditor(ShiroUser shiroUser, CreditorConfigAddRequestVo vo) {

        ReceiptChannel receiptChannel;
        //如果是不开票，是系统
        if("0".equals(vo.getIsReceipt())){
            receiptChannel = ReceiptChannel.NOSYSTEM;
        }else{
            //开票，我司还是担保方开票
            if (OrganizationEnum.WEICAI.equals(vo.getBelongOrgEnum())){
                receiptChannel = ReceiptChannel.NUONUO;
            }else {
                receiptChannel = ReceiptChannel.MANUAL;
            }
        }
        String creditorConfigVersion = DateUtils.format(new Date(), DateUtils.DATE_TIME_NO_BLANK_FORMAT);
        List<Creditor> list = new ArrayList<>();
        for (String capitalOperation :vo.getCapitalOperation()){
            Creditor creditor = new Creditor();
            creditor.setCreditor(vo.getCreditorEnum());
            creditor.setCreditorName(creditor.getCreditor().name());
            creditor.setOperatorName(shiroUser.getUserName());
            creditor.setReceiptChannel(receiptChannel);
            creditor.setUserName(capitalOperation);
            creditor.setOrganization(vo.getBelongOrgEnum());
            creditor.setCreditorConfigVersion(creditorConfigVersion);
            creditor.setDeleted(false);
            list.add(creditor);
        }
        return list;
    }


    private static CreditorBaseConfig buildCreditorBaseConfig(Boolean isAdd, String version,Creditor creditor, ReceiptItemCodeBase receiptItemCodeBase) {

        CreditorBaseConfig creditorBaseConfig = new CreditorBaseConfig();

        creditorBaseConfig.setCreditor(creditor.getCreditor());
        creditorBaseConfig.setCreditorConfigVersion(isAdd ? creditor.getCreditorConfigVersion():version);
        creditorBaseConfig.setDeleted(false);
        creditorBaseConfig.setReceiptItemCode(receiptItemCodeBase);
        creditorBaseConfig.setItemType(receiptItemCodeBase.getItemType());
        creditorBaseConfig.setCreateTime(new Date());
        creditorBaseConfig.setUpdateTime(new Date());

        return creditorBaseConfig;
    }

    private static CreditorAmountConfig buildCreditorAmountConfig(Boolean isAdd, String version,Creditor creditor, ReceiptItemCodeAmount receiptItemCodeAmount) {

        CreditorAmountConfig creditorAmountConfig = new CreditorAmountConfig();

        creditorAmountConfig.setCreditor(creditor.getCreditor());
        creditorAmountConfig.setCreditorConfigVersion(isAdd ? creditor.getCreditorConfigVersion():version);
        creditorAmountConfig.setDeleted(false);
        creditorAmountConfig.setReceiptItemCode(receiptItemCodeAmount);
        creditorAmountConfig.setItemType("0");
        creditorAmountConfig.setCreateTime(new Date());
        creditorAmountConfig.setUpdateTime(new Date());
        
        return creditorAmountConfig;
    }

    @Transactional
    public void update(CreditorConfigUpdateRequestVo vo,ShiroUser shiroUser) {

        CreditorEnum creditorEnum = CreditorEnum.getEnum(vo.getCreditor());
        //查询需要更新的主体列表，多个用户多条
        List<Creditor> creditorList = creditorService.findByCreditorAndDeleted(creditorEnum,false);
        //删除以前的主体，新增主体
        for (Creditor creditor : creditorList){
            creditor.setDeleted(true);
            creditorService.update(creditor);
        }
        //删除以前的配置
        List<CreditorBaseConfig>  creditorBaseConfigList = creditorBaseConfigService.findByCreditorAndDeleted(creditorEnum,false);
        for (CreditorBaseConfig baseConfig : creditorBaseConfigList){
            baseConfig.setDeleted(true);
            creditorBaseConfigService.update(baseConfig);
        }
        List<CreditorAmountConfig>  creditorAmountConfigList = creditorAmountConfigService.findByCreditorAndDeleted(creditorEnum,false);
        for (CreditorAmountConfig amountConfig : creditorAmountConfigList){
            amountConfig.setDeleted(true);
            creditorAmountConfigService.update(amountConfig);
        }
       //增加新配置
        String creditorConfigVersion = DateUtils.format(new Date(), DateUtils.DATE_TIME_NO_BLANK_FORMAT);
        for (String capitalOperation : vo.getCapitalOperation()) {
            ReceiptChannel receiptChannel;
            //如果是不开票，是系统
            if("0".equals(vo.getIsReceipt())){
                receiptChannel = ReceiptChannel.NOSYSTEM;
            }else{
                //开票，我司还是担保方开票
                if (OrganizationEnum.WEICAI.equals(vo.getBelongOrgEnum())){
                    receiptChannel = ReceiptChannel.NUONUO;
                }else {
                    receiptChannel = ReceiptChannel.MANUAL;
                }
            }
            List<Creditor> list = new ArrayList<>();
            Creditor creditor = new Creditor();
            creditor.setCreditor(vo.getCreditorEnum());
            creditor.setCreditorName(creditorEnum.name());
            creditor.setOperatorName(shiroUser.getUserName());
            creditor.setReceiptChannel(receiptChannel);
            creditor.setUserName(capitalOperation);
            creditor.setOrganization(vo.getBelongOrgEnum());
            creditor.setCreditorConfigVersion(creditorConfigVersion);
            creditor.setDeleted(false);
            list.add(creditor);
            creditorService.save(creditor);
        }
        List<CreditorBaseConfig> creditorBaseConfigs = vo.getReceiptItemCodeBases().stream().map(receiptItemCodeBase ->
                buildCreditorBaseConfig(false,creditorConfigVersion,creditorList.get(0), receiptItemCodeBase)).collect(Collectors.toList());
        creditorBaseConfigService.batchSave(creditorBaseConfigs);

        List<CreditorAmountConfig> creditorAmountConfigs = vo.getReceiptItemCodeAmounts().stream().map(receiptItemCodeAmount ->
                buildCreditorAmountConfig(false,creditorConfigVersion,creditorList.get(0), receiptItemCodeAmount)).collect(Collectors.toList());
        creditorAmountConfigService.batchSave(creditorAmountConfigs);
    }

    /**
     * 查询详情
     * @param vo
     * @return
     */
    public  List<CreditorConfigQueryResponseVo> detail(CreditorConfigDetailRequestVo vo){
        List<Creditor> pageCreditors = creditorService.findByCreditorAndDeleted(vo.getCreditorEnum(), false);
        return buildCreditorConfigQueryResponseVos(pageCreditors);
    }
}
