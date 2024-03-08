package com.ibg.receipt.service.receipt.impl;

import com.google.common.collect.Lists;
import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.base.vo.CustomPageRequest;
import com.ibg.receipt.dao.receipt.ReceiptOrderRepository;
import com.ibg.receipt.enums.business.ReceiptChildOrderAmountStatus;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.service.receipt.ReceiptOrderService;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.order.OrderReceiptQueryRequestVo;
import com.sun.javafx.fxml.expression.Expression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ReceiptOrderServiceImpl extends BaseServiceImpl<ReceiptOrder, ReceiptOrderRepository>
        implements ReceiptOrderService {

    @Autowired
    @Override
    protected void setRepository(ReceiptOrderRepository repository) {
        super.repository = repository;
    }

    @Override
    public ReceiptOrder findByReceiptOrderKey(String receiptOrderKey) {
        return repository.findByReceiptOrderKey(receiptOrderKey);
    }

    @Override
    public Page<ReceiptOrder> findAllqueryPaged(OrderReceiptQueryRequestVo requestVo) {

        Specification<ReceiptOrder> receiptOrderSpecification = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();

            if (StringUtils.isNotBlank(requestVo.getReceiptOrderKey())) {
                predicates.add(cb.equal(root.get("receiptOrderKey"), requestVo.getReceiptOrderKey()));
            }
            if (StringUtils.isNotBlank(requestVo.getReceiptOrderStatus())) {
//                ReceiptChildOrderAmountStatus status = ReceiptChildOrderAmountStatus
//                        .getByDec(requestVo.getReceiptOrderStatus());
                predicates.add(cb.equal(root.get("status"), Byte.valueOf(requestVo.getReceiptOrderStatus())));
            }
            if (StringUtils.isNotBlank(requestVo.getPartnerUserId())) {
                predicates.add(cb.equal(root.get("uid"), requestVo.getPartnerUserId()));
            }
            if (requestVo.getPriorityLevel() != null) {
                predicates.add(cb.equal(root.get("priorityLevel"), requestVo.getPriorityLevel()));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
        return repository.findAll(receiptOrderSpecification,
                new CustomPageRequest(requestVo.getPageNum(), requestVo.getPageSize(), Sort.Direction.DESC, "id"));
    }

    @Override
    public ReceiptOrder saveReceiptOrder(ReceiptOrder receiptOrder) {
        return repository.save(receiptOrder);
    }
}
