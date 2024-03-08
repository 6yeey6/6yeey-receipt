package com.ibg.receipt.vo.api.nuonuo.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibg.receipt.vo.api.nuonuo.base.NuoNuoBaseReqVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRetryReqVo extends NuoNuoBaseReqVO {

    /**
     * 发票流水号，流水号和订单号两字段二选一，同时存在以流水号为准
     */
    @JsonProperty("fpqqlsh")
    private String fpqqlsh;

    /**
     * 订单号
     */
    @JsonProperty("orderno")
    private String orderno;
}
