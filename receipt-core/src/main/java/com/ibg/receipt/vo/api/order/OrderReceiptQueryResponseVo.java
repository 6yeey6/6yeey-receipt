package com.ibg.receipt.vo.api.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工单查询
 * @author zhangjilong
 */

@Data
@Builder
public class OrderReceiptQueryResponseVo {
    private String receiptOrderKey;
    private String createUser;
    private Date createTime;
    private String partnerUserId;
    private String customerName;
    private BigDecimal receiptTotalAmount;
    private String receiptOrderStatus;
    private Integer handleDays;
    private String email;
    private Integer priorityLevel;
    private Date finishedTime;
    private Integer unfinishedCount;
    private Integer finishedCount;
    private Integer totalCount;
}
