package com.ibg.receipt.vo.api.receiptChild;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 子单成功VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildOrderSuccessVo {

    private String receiptFileId;

    private String receiptUrl;

}
