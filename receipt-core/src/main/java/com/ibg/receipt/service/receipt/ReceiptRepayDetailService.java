package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.model.receipt.ReceiptRepayDetail;

import java.util.List;

public interface ReceiptRepayDetailService extends BaseService<ReceiptRepayDetail> {

    List<ReceiptRepayDetail> findByLoanIdIn(List<String> needRepayDetailsList);
}
