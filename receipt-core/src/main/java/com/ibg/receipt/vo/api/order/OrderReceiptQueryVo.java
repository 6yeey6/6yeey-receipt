package com.ibg.receipt.vo.api.order;

import lombok.Builder;
import lombok.Data;

/**
 * 工单
 * @author zhangjilong
 */
@Data
@Builder
public class OrderReceiptQueryVo {

    private String receiptOrderKey;
    private String partnerUserId;
    private String customerName;
    private String receiptTotalAmount;
    private String receiptOrderStatus;
    private String handleDays;
}
