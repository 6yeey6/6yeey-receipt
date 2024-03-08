package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.model.receipt.ReceiptOrderLoan;

import java.util.Date;
import java.util.List;

public interface ReceiptOrderLoanService extends BaseService<ReceiptOrderLoan> {

    List<ReceiptOrderLoan> findByRequestTimeAndStatus(byte status, Date startTime, Date endTime);

    ReceiptOrderLoan findByLoanId(String loanId);
    List<ReceiptOrderLoan> findByLoanIds(List<String> loanIds);

    List<ReceiptOrderLoan> findByReceiptOrerKeys(List<String> receiptOrderKeys);

    ReceiptOrderLoan saveReceiptOrderLoan(ReceiptOrderLoan orderLoan);
}
