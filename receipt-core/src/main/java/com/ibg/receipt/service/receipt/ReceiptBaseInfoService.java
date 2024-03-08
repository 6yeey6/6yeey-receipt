package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receipt.ReceiptOrder;

import java.util.Date;
import java.util.List;

public interface ReceiptBaseInfoService extends BaseService<ReceiptBaseInfo> {

    List<ReceiptBaseInfo> findByLoanIdAndCreditor(String loanId, CreditorEnum Creditor);

    List<ReceiptBaseInfo> findByCreateTimeBetweenAndStatus(Date startTime, Date endTime, byte status);

    List<ReceiptBaseInfo> findByLoanIdAndStatus(String loanId,byte status);

    List<ReceiptBaseInfo> findByLoanId(String loanId);
}
