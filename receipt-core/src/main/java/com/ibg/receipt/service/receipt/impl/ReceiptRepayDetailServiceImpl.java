package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receipt.ReceiptOrderRepository;
import com.ibg.receipt.dao.receipt.ReceiptRepayDetailRepository;
import com.ibg.receipt.model.receipt.ReceiptRepayDetail;
import com.ibg.receipt.service.receipt.ReceiptRepayDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReceiptRepayDetailServiceImpl extends BaseServiceImpl<ReceiptRepayDetail, ReceiptRepayDetailRepository>
        implements ReceiptRepayDetailService {

    @Autowired
    @Override
    protected void setRepository(ReceiptRepayDetailRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<ReceiptRepayDetail> findByLoanIdIn(List<String> needRepayDetailsList) {
        return repository.findByLoanIdIn(needRepayDetailsList);
    }
}
