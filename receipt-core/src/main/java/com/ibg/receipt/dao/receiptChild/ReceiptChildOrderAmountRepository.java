package com.ibg.receipt.dao.receiptChild;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.enums.business.ReceiptChildOrderAmountStatus;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReceiptChildOrderAmountRepository extends BaseRepository<ReceiptChildOrderAmount>, JpaSpecificationExecutor<ReceiptChildOrderAmount> {
    List<ReceiptChildOrderAmount> findByReceiptOrderKey(String receiptOrderKey);

    List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(String[] receiptChildOrderKeys);

    List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyInAndStatus(String[] receiptChildOrderKeys, byte status);

    ReceiptChildOrderAmount findByReceiptChildOrderKey(String receiptChildOrderKey);

    List<ReceiptChildOrderAmount> findByReceiptOrderKeyIn(List<String> receiptOrderKeys);

    List<ReceiptChildOrderAmount> findByStatus(byte status);

    List<ReceiptChildOrderAmount> findByLoanId(String loanId);

    List<ReceiptChildOrderAmount> findByIdInAndStatus(Long[] receiptOrderKeysArray, byte status);

    @Query(value = "SELECT * FROM receipt_child_order_amount t  where t.loan_id in :loanIds AND t.receipt_item_code in :receiptItemCodes", nativeQuery = true)
    List<ReceiptChildOrderAmount> findByLoanIdInAndReceiptItemCodeIn(@Param("loanIds") String[] loanIds,@Param("receiptItemCodes") String[] receiptItemCodes);

    List<ReceiptChildOrderAmount> findByIdInAndStatusIn(Long[] receiptOrderKeysArray, List<Byte> statusList);
    @Query(value = "SELECT * FROM receipt_child_order_amount t  where t.id in :ids", nativeQuery = true)
    List<ReceiptChildOrderAmount> findByIdIn(@Param("ids") String[] receiptOrderKeysArray);

    List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(List<String> repayChildOrderKeys);
}
