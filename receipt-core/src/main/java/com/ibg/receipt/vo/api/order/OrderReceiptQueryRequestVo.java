package com.ibg.receipt.vo.api.order;

import com.ibg.receipt.base.exception.Assert;

import lombok.Data;

/**
 * 工单查询
 * @author zhangjilong
 */
@Data
public class OrderReceiptQueryRequestVo {

    private String receiptOrderKey;

    private String receiptOrderStatus;

    private String partnerUserId;

    private Integer pageNum;

    private Integer pageSize;

    private Integer priorityLevel;

    public void checkParams() {
        Assert.notNull(pageNum, "页码");
        Assert.notNull(pageSize, "分页大小");
    }

}
