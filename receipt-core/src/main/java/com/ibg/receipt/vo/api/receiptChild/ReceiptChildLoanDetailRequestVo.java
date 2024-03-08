package com.ibg.receipt.vo.api.receiptChild;

import com.ibg.receipt.vo.api.base.BasePageVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 子单列表查询请求vo
 *
 * @author zhou
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptChildLoanDetailRequestVo  {

    private String uid;
    private String receiptOrderKey;
    private String receiptChildOrderKey;
    private String status;
    private String creditor;
    private String operatorName;



}
