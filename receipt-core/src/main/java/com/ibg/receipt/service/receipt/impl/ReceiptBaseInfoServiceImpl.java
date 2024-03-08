package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receipt.ReceiptBaseInfoRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receipt.ReceiptOrderLoan;
import com.ibg.receipt.service.receipt.ReceiptBaseInfoService;
import com.ibg.receipt.service.receipt.ReceiptOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ReceiptBaseInfoServiceImpl extends BaseServiceImpl<ReceiptBaseInfo, ReceiptBaseInfoRepository>
        implements ReceiptBaseInfoService {
    @Autowired
    private ReceiptOrderService receiptOrderService;
    @Autowired
    @Override
    protected void setRepository(ReceiptBaseInfoRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<ReceiptBaseInfo> findByLoanIdAndCreditor(String loanId, CreditorEnum Creditor) {
        return repository.findByLoanIdAndCreditor(loanId,Creditor);
    }

    @Override
    public List<ReceiptBaseInfo> findByCreateTimeBetweenAndStatus(Date startTime, Date endTime,byte status) {
        return repository.findByCreateTimeBetweenAndStatus(startTime, endTime,status);
    }

    @Override
    public List<ReceiptBaseInfo> findByLoanIdAndStatus(String loanId,byte status) {
        return repository.findByLoanIdAndStatus(loanId,status);
    }
    @Override
    public List<ReceiptBaseInfo> findByLoanId(String loanId){
        return repository.findByLoanId(loanId);
    }
}
