package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receipt.ReceiptOrderLoan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ReceiptOrderLoanRepository extends BaseRepository<ReceiptOrderLoan> {

    @Query(value = "SELECT rp.* FROM receipt_order_loan rp , receipt_order l  where  rp.receipt_order_key = l.receipt_order_key AND l.status = :status"
            + " AND  l.request_time between  :startTime  and  :endTime ", nativeQuery = true)
    List<ReceiptOrderLoan> findByRequestTimeAndStatus(@Param("status") byte status, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    ReceiptOrderLoan findByLoanId(String loanId);

    List<ReceiptOrderLoan> findByReceiptOrderKeyIn(List<String> receiptOrderKeys);

    List<ReceiptOrderLoan> findByLoanIdIn(List<String> loanIds);
}
