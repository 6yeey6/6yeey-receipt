package com.ibg.receipt.dao.receiptChild;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;

import java.util.List;


public interface ReceiptChildOrderBaseRepository extends BaseRepository<ReceiptChildOrderBase> {

    List<ReceiptChildOrderBase> findByLoanIdAndCreditor(String loanId, CreditorEnum creditor);
}
