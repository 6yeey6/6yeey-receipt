package com.ibg.receipt.service.receiptChild.impl;

import com.alibaba.fastjson.JSON;
import com.ibg.receipt.base.constant.ConfigConstants;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.dao.receiptChild.ReceiptChildOrderAmountRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.OrganizationEnum;
import com.ibg.receipt.enums.business.ReceiptChannel;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.service.common.ConfigService;
import com.ibg.receipt.service.receipt.CreditorService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildListRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReceiptChildOrderAmountServiceImpl extends BaseServiceImpl<ReceiptChildOrderAmount, ReceiptChildOrderAmountRepository>
        implements ReceiptChildOrderAmountService {

    @Autowired
    private ConfigService configService;
    @Autowired
    private CreditorService creditorService;
    @Autowired
    @Override
    protected void setRepository(ReceiptChildOrderAmountRepository repository) {
        super.repository = repository;
    }


    @Override
    public PageVo<ReceiptChildOrderAmount> page(ReceiptChildListRequestVo vo, Integer pageNum, Integer pageSize) {
        Assert.notNull(pageNum, "pageNum");
        Assert.notNull(pageSize, "pageSize");
        if(StringUtils.isNotBlank(vo.getLandUserName())){
            //发票平台才有校验;登录人不是管理员角色,只能查看自己创建的主体类
            if(!configService.hitKeyNew(ConfigConstants.RECEIPT_ADMIN,vo.getLandUserName())) {
                log.info("运营平台非管理员登录!请求参数:{}",JSON.toJSONString(vo));
                //当前人查询主题列表
                List<CreditorEnum> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(), false).stream().map(x -> x.getCreditor()).collect(Collectors.toList());
                if(creditorList.size() == 0){
                    return null;
                }
                log.info("运营平台非管理员名下主体:{}",creditorList.toString());
            }
            log.info("运营平台管理员登录!请求参数:{}",JSON.toJSONString(vo));
        }else{
            log.info("客服平台登录登录!请求参数:{}", JSON.toJSONString(vo));
        }
        Specification specification = buildSpecification(vo);
        if(specification == null){
            log.error("无对应主体，无法查询数据");
            return null;
        }
        //List<ReceiptChildOrderAmount> result = new ArrayList<>();
        //return repository.findAll(specification
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        List<ReceiptChildOrderAmount> list = repository.findAll(specification, sort);
        /*if(StringUtils.isNotBlank(vo.getLandUserName())){
            //发票平台才有校验;登录人不是管理员角色,只能查看自己创建的主体类
            if(!configService.hitKey(ConfigConstants.RECEIPT_ADMIN,vo.getLandUserName())) {
                //当前人查询主题列表
                List<CreditorEnum> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(), false).stream().map(x -> x.getCreditor()).collect(Collectors.toList());
                list = creditorList.size() > 0 ? list.stream().filter(x ->  creditorList.contains(x.getCreditor())).collect(Collectors.toList()) : null;
            }
        }*/
        PageVo<ReceiptChildOrderAmount> pageVo = new PageVo<ReceiptChildOrderAmount>();
        pageVo.setPageNum(vo.getPageNum());
        pageVo.setPageSize(vo.getPageSize());
        pageVo.setTotal(CollectionUtils.isEmpty(list) ? 0L: (long)list.size());
        pageVo.setList(this.getPageList(list,vo.getPageNum(),vo.getPageSize()));
        return pageVo;
    }

    private List<ReceiptChildOrderAmount> getPageList(List<ReceiptChildOrderAmount> list, Integer pageNum, Integer pageSize) {
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

    @Override
    public List<ReceiptChildOrderAmount> findByReceiptOrderKey(String receiptOrderKey) {
        return repository.findByReceiptOrderKey(receiptOrderKey);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(String[] receiptChildOrderKeys) {
        return repository.findByReceiptChildOrderKeyIn(receiptChildOrderKeys);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyInAndStatus(String[] receiptChildOrderKeys, byte status) {
        return repository.findByReceiptChildOrderKeyInAndStatus(receiptChildOrderKeys,status);
    }

    @Override
    public ReceiptChildOrderAmount findByReceiptChildOrderKey(String receiptChildOrderKey) {
        return repository.findByReceiptChildOrderKey(receiptChildOrderKey);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByReceiptOrerKeys(List<String> receiptOrderKeys) {
        return repository.findByReceiptOrderKeyIn(receiptOrderKeys);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByLoanId(String partnerLoanNo) {
        return repository.findByLoanId(partnerLoanNo);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByStatus(byte status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByIdInAndStatus(String[] receiptOrderKeysArray, byte status) {
        Long[] l = new Long[receiptOrderKeysArray.length];
        for (int i=0;i<receiptOrderKeysArray.length;i++){
            l[i] = Long.parseLong(receiptOrderKeysArray[i]);
        }
        return repository.findByIdInAndStatus(l,status);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByLoanIdInAndReceiptItemCodeIn(String[] loanIds, String[] receiptItemCodes) {
        return repository.findByLoanIdInAndReceiptItemCodeIn(loanIds,receiptItemCodes);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByIdInAndStatusIn(String[] receiptOrderKeysArray, List<Byte> statusList) {
        Long[] l = new Long[receiptOrderKeysArray.length];
        for (int i=0;i<receiptOrderKeysArray.length;i++){
            l[i] = Long.parseLong(receiptOrderKeysArray[i]);
        }
        return repository.findByIdInAndStatusIn(l,statusList);
    }

    @Override
    public List<ReceiptChildOrderAmount> findAll(Specification cond) {
        return repository.findAll(cond);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByIdIn(String[] receiptOrderKeysArray) {
        return repository.findByIdIn(receiptOrderKeysArray);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(List<String> repayChildOrderKeys) {
        return repository.findByReceiptChildOrderKeyIn(repayChildOrderKeys);
    }

    private Specification buildSpecification(ReceiptChildListRequestVo vo) {
        Specification specification = (Specification<ReceiptChildListRequestVo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.isNotBlank(vo.getReceiptChildOrderKey())){
                predicates.add(criteriaBuilder.equal(root.get("receiptChildOrderKey"), vo.getReceiptChildOrderKey()));
            }
            if(StringUtils.isNotBlank(vo.getReceiptOrderKey())){
                predicates.add(criteriaBuilder.equal(root.get("receiptOrderKey"), vo.getReceiptOrderKey()));
            }
             if (vo.getCreditors() != null && vo.getCreditors().size() > 0){
                Predicate ins = root.get("creditor").in(vo.getCreditorEnums());
                predicates.add(ins);
                //predicates.add(criteriaBuilder.equal(root.get("creditor"), CreditorEnum.valueOf(vo.getCreditor())));
            }
            if(StringUtils.isNotBlank(vo.getOperatorName())){
                predicates.add(criteriaBuilder.equal(root.get("operatorName"), vo.getOperatorName()));
            }
            //通过订单号查询
            if(StringUtils.isNotBlank(vo.getLoanId())){
                predicates.add(criteriaBuilder.equal(root.get("loanId"), vo.getLoanId()));
            }
            //通过资金项查询
            if (CollectionUtils.isNotEmpty(vo.getReceiptItemCodes())){
                Predicate ins = root.get("receiptItemCode").in(vo.getReceiptItemCodeList());
                predicates.add(ins);
            }
            if(vo.getStatusList() != null && vo.getStatusList().size()>0){
                Predicate ins = root.get("status").in(vo.getStatusList());
                predicates.add(ins);
                //predicates.add(criteriaBuilder.equal(root.get("status"), Byte.valueOf(vo.getStatus())));
            }
            if(StringUtils.isNotBlank(vo.getUid())){
                predicates.add(criteriaBuilder.equal(root.get("uid"), vo.getUid()));
            }
            if(StringUtils.isNotBlank(vo.getSendStatus())){
                predicates.add(criteriaBuilder.equal(root.get("sendStatus"), Byte.valueOf(vo.getSendStatus())));
            }
            if(StringUtils.isNotBlank(vo.getPartnerUserId())){
                predicates.add(criteriaBuilder.equal(root.get("partnerUserId"), vo.getPartnerUserId()));
            }
            if(vo.getPriorityLevel() != null){
                predicates.add(criteriaBuilder.equal(root.get("priorityLevel"), vo.getPriorityLevel()));
            }
            if(StringUtils.isNotBlank(vo.getOrganization())){
                if(vo.getOrganization().equals(OrganizationEnum.WEICAI.name())){
                    predicates.add(criteriaBuilder.equal(root.get("receiptChannel"), ReceiptChannel.NUONUO));
                }else{
                    List<ReceiptChannel> list = Arrays.asList(ReceiptChannel.MANUAL,ReceiptChannel.NOSYSTEM);
                    Predicate ins = root.get("receiptChannel").in(list);
                    predicates.add(ins);
                    //predicates.add(criteriaBuilder.equal(root.get("receiptChannel"), ReceiptChannel.MANUAL));
                }
            }
            if(StringUtils.isNotBlank(vo.getLandUserName())){
                //发票平台才有校验;登录人不是管理员角色,只能查看自己创建的主体类
                if(!configService.hitKey(ConfigConstants.RECEIPT_ADMIN,vo.getLandUserName())) {

                    //当前人查询主题列表
                    List<CreditorEnum> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(), false).stream().map(x -> x.getCreditor()).collect(Collectors.toList());
                    if (creditorList != null && creditorList.size() > 0) {
                        log.info("当前实体类:{}",creditorList.toString());
                        //List<CreditorEnum> creditorEnums = new ArrayList<>();
                        //creditorList.forEach(x->creditorEnums.add(CreditorEnum.getEnum(x)));
                        Predicate ins = root.get("creditor").in(creditorList);
                        predicates.add(ins);
                    }
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            /*if(StringUtils.isNotBlank(vo.getLandUserName())){
                //发票平台才有校验;登录人不是管理员角色,只能查看自己创建的主体类
                if(!configService.hitKey(ConfigConstants.RECEIPT_ADMIN,vo.getLandUserName())) {
                    //当前人查询主题列表
                    //List<String> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(), false).stream().map(x -> x.getCreditor().name()).collect(Collectors.toList());
                    List<CreditorEnum> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(), false).stream().map(x -> x.getCreditor()).collect(Collectors.toList());
                    if (creditorList != null && creditorList.size() > 0) {
                        Path<Object> path = root.get("creditor");
                        CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                        for (int i = 0; i < creditorList.size(); i++) {
                            in.value(creditorList.get(i));
                        }
                        predicates.add(criteriaBuilder.and(criteriaBuilder.and(in)));
                    }
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));*/
        };
        return specification;
    }
}
