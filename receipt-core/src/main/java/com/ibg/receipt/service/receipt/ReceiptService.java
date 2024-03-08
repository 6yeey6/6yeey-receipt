package com.ibg.receipt.service.receipt;


import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;
import com.ibg.receipt.vo.api.receiptChild.ChildOrderSuccessVo;

import java.util.List;

public interface ReceiptService {

    void initReceiptChildOrderSuccess(List<ReceiptChildOrderAmount> amountList, List<ReceiptChildOrderBase> baseList, List<ReceiptBaseInfo> list, String receiptOrderKey);

    void childOrderSuccess(String receiptChildOrderKey, ChildOrderSuccessVo vo);

    void updateReceiptChildOrderAmountList(List<ReceiptChildOrderAmount> list);
}
