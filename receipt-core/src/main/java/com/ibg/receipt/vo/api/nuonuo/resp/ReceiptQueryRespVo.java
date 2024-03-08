package com.ibg.receipt.vo.api.nuonuo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptQueryRespVo {

    private String serialNo;
    private String orderNo;
    private String status;
    private String statusMsg;
    private String failCause;
    private String pdfUrl;
    private String pictureUrl;
    /**
     * 发票ofd地址（公共服务平台签章时返回）
     */
    private String ofdUrl;
    private Long invoiceTime;
    private String invoiceCode;
    private String invoiceNo;
    private String exTaxAmount;
    private String taxAmount;
    private String payerName;
    private String payerTaxNo;
    private String invoiceKind;
    private String checkCode;
    private List<InvoiceItemsBean> invoiceItems;

    @NoArgsConstructor
    @Data
    public static class InvoiceItemsBean {
        private String itemName;
        private String itemUnit;
        private String itemPrice;
        private String itemTaxRate;
        private String itemNum;
        private String itemAmount;
        private String itemTaxAmount;
        private String itemSpec;
        private String itemCode;
        private String isIncludeTax;
        private String invoiceLineProperty;
        private String zeroRateFlag;
        private String favouredPolicyName;
        private String favouredPolicyFlag;
    }

}
