package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.model.receipt.ReceiptRepayDetail;

import java.util.List;


public interface ReceiptRepayDetailRepository extends BaseRepository<ReceiptRepayDetail> {

    List<ReceiptRepayDetail> findByLoanIdIn(List<String> needRepayDetailsList);
}
