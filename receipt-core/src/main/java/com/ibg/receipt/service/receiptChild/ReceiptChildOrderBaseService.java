package com.ibg.receipt.service.receiptChild;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;

import java.util.List;

public interface ReceiptChildOrderBaseService extends BaseService<ReceiptChildOrderBase> {


    List<ReceiptChildOrderBase> findByLoanIdAndCreditorOrderByIdDesc(String loanId, CreditorEnum creditor);
}
