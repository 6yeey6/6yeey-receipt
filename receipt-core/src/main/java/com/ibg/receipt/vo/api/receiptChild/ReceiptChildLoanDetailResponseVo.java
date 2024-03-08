package com.ibg.receipt.vo.api.receiptChild;

import com.ibg.receipt.vo.api.base.BasePageVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 子单列表查询响应vo
 *
 * @author zhou
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptChildLoanDetailResponseVo extends BasePageVo {

    private Summary summary;

    private List<LoanDetails> loanDetails;

    private List<LoanSummary> loanSummarys;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanSummary {
        private String loanId;
        private String userName;
        private String fundName;
        private BigDecimal loanAmount;
        private String repayStatus;


        private BigDecimal repayInterest;

        private BigDecimal repayMgmtFee;

        private BigDecimal repayOverdueInterest;

        private BigDecimal repayOverdueMgmtFee;

        private BigDecimal repayFunderOverdueInterest;

        private BigDecimal repayGuaranteeDeposit;

        private BigDecimal repayGuaranteeFee;

        private BigDecimal repayOverdueGuaranteeFee;


        private BigDecimal repayCommutation;

        private BigDecimal repayInRepayFee;

        private Date loanTime;

        private Date payOffTime;
        private String partnerUserId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private String userName;
        private BigDecimal totalReceiptAmount;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanDetails {
        private String loanId;
        private List<LoanDetail> loanDetails;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LoanDetail {
            private String creditor;
            private String creditorName;
            private String receiptItemName;
            private String receiptChannel;
            private BigDecimal receiptAmount;
            private Byte status;
            private Date finishTime;
            private Date sendTime;
            private Byte sendStatus;
            private String receiptUrl;
            private String organization;
        }
    }

}
