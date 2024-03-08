package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receipt.ReceiptBaseInfoRepository;
import com.ibg.receipt.dao.receipt.ReceiptOrderLoanRepository;
import com.ibg.receipt.model.receipt.ReceiptOrderLoan;
import com.ibg.receipt.service.receipt.ReceiptOrderLoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ReceiptOrderLoanServiceImpl extends BaseServiceImpl<ReceiptOrderLoan, ReceiptOrderLoanRepository>
        implements ReceiptOrderLoanService {

    @Autowired
    @Override
    protected void setRepository(ReceiptOrderLoanRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<ReceiptOrderLoan> findByRequestTimeAndStatus(byte status, Date startTime, Date endTime) {
        return repository.findByRequestTimeAndStatus(status, startTime, endTime);
    }

    @Override
    public ReceiptOrderLoan findByLoanId(String loanId) {
        return repository.findByLoanId(loanId);
    }

    @Override
    public List<ReceiptOrderLoan> findByLoanIds(List<String> loanIds) {
        return repository.findByLoanIdIn(loanIds);
    }

    @Override
    public List<ReceiptOrderLoan> findByReceiptOrerKeys(List<String> receiptOrderKeys) {
        return repository.findByReceiptOrderKeyIn(receiptOrderKeys);
    }

    @Override
    public ReceiptOrderLoan saveReceiptOrderLoan(ReceiptOrderLoan orderLoan) {
        return repository.save(orderLoan);
    }
}
