package com.ibg.receipt.vo.api.receiptChild;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 子单列表查询请求vo
 *
 * @author zhou
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptOrderAmountCondVo {
    private String ids;
    //private String creditor;
    private List<String> creditors;
    private String landUserName;
    private Integer priorityLevel;
    private String receiptOrderKey;
    private String sendStatus;
    private String status;
    private String uid;
    private String partnerUserId;
    private String receiptChildOrderKey;

    private List<String> creditorList;
    private List<String> statusList;



}
