package com.ibg.receipt.service.receiptChild;

import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.enums.business.ReceiptChildOrderAmountStatus;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildListRequestVo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ReceiptChildOrderAmountService extends BaseService<ReceiptChildOrderAmount> {


    PageVo<ReceiptChildOrderAmount> page(ReceiptChildListRequestVo vo, Integer pageNum, Integer pageSize);

    List<ReceiptChildOrderAmount> findByReceiptOrderKey(String receiptOrderKey);


    List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(String[] receiptChildOrderKeys);

    List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyInAndStatus(String[] receiptChildOrderKeys, byte status);

    ReceiptChildOrderAmount findByReceiptChildOrderKey(String receiptChildOrderKey);

    List<ReceiptChildOrderAmount> findByReceiptOrerKeys(List<String> receiptOrderKeys);

    List<ReceiptChildOrderAmount> findByLoanId(String partnerLoanNo);


    List<ReceiptChildOrderAmount> findByStatus(byte status);

    List<ReceiptChildOrderAmount> findByIdInAndStatus(String[] receiptOrderKeysArray, byte status);

    List<ReceiptChildOrderAmount> findByLoanIdInAndReceiptItemCodeIn(String[] loanIds, String[] receiptItemCodes);

    List<ReceiptChildOrderAmount> findByIdInAndStatusIn(String[] receiptOrderKeysArray, List<Byte> asList);

    List<ReceiptChildOrderAmount> findAll(Specification cond);

    List<ReceiptChildOrderAmount> findByIdIn(String[] receiptOrderKeysArray);

    List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(List<String> repayChildOrderKeys);
}
