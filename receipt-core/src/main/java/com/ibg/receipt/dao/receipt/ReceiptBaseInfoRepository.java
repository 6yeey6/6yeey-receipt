package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ReceiptBaseInfoRepository extends BaseRepository<ReceiptBaseInfo> {

    List<ReceiptBaseInfo> findByLoanIdAndCreditor(String loanId, CreditorEnum Creditor);

    @Query(value = "select t.* from receipt_base_info t,receipt_repay_detail a where t.loan_id = a.loan_id AND t.status =  :status and t.creditor !='' and t.creditor !='NONE'"
            + " AND  t.create_time between  :startTime  and  :endTime AND  a.create_time between  :startTime  and  :endTime group by t.id", nativeQuery = true)
    List<ReceiptBaseInfo> findByCreateTimeBetweenAndStatus(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("status") byte status);
    @Query(value = "select t.* from receipt_base_info t where t.loan_id = :loanId AND t.status =  :status and t.creditor !='' and t.creditor != 'UCREDIT' and t.creditor !='NONE'", nativeQuery = true)
    List<ReceiptBaseInfo> findByLoanIdAndStatus(@Param("loanId") String loanId,@Param("status") byte status);

    @Query(value = "select t.* from receipt_base_info t where t.loan_id = :loanId and t.creditor !='' and t.creditor != 'UCREDIT' and t.creditor !='NONE' ", nativeQuery = true)
    List<ReceiptBaseInfo> findByLoanId(@Param("loanId")String loanId);
}
