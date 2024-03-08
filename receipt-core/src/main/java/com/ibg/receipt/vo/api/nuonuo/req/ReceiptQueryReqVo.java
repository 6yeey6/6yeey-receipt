package com.ibg.receipt.vo.api.nuonuo.req;

import com.ibg.receipt.vo.api.nuonuo.base.NuoNuoBaseReqVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptQueryReqVo extends NuoNuoBaseReqVO {

    /**
     * 订单编号（最多查50个订单号）
     */
    private List<String> orderNos;
    /**
     * 发票流水号，两字段二选一，同时存在以流水号为准（最多查50个订单号）
     */
    private List<String> serialNos;
    /**
     * 是否需要提供明细 1-是, 0-否(不填默认 0)
     */
    private String isOfferInvoiceDetail;
}
