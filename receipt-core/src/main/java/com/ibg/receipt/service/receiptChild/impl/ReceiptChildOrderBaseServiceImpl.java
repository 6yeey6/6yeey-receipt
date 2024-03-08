package com.ibg.receipt.service.receiptChild.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receiptChild.ReceiptChildOrderAmountRepository;
import com.ibg.receipt.dao.receiptChild.ReceiptChildOrderBaseRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReceiptChildOrderBaseServiceImpl extends BaseServiceImpl<ReceiptChildOrderBase, ReceiptChildOrderBaseRepository>
        implements ReceiptChildOrderBaseService {

    @Autowired
    @Override
    protected void setRepository(ReceiptChildOrderBaseRepository repository) {
        super.repository = repository;
    }
    @Override
    public List<ReceiptChildOrderBase> findByLoanIdAndCreditorOrderByIdDesc(String loanId, CreditorEnum creditor) {
        return repository.findByLoanIdAndCreditor(loanId,creditor);
    }
}
