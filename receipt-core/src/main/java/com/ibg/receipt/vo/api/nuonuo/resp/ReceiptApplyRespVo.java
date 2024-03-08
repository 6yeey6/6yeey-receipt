package com.ibg.receipt.vo.api.nuonuo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptApplyRespVo{

    /**
     * 发票流水号
     */
    private String invoiceSerialNum;
}
