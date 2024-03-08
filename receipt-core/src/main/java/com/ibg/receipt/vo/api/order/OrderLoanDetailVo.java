package com.ibg.receipt.vo.api.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 贷款单明细
 * 
 * @author zhangjilong
 */
@Data
@Builder
public class OrderLoanDetailVo {
    private String loanId;
    private String fundCode;
    private String fundName;
    private BigDecimal loanAmount;
    private String loanStatus;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal guarantee;
    private BigDecimal fundOverdueInterest;
    private BigDecimal inRepayFee;
    private BigDecimal overInterest;
    private BigDecimal mgmt;
    private BigDecimal commutation;
    private BigDecimal overdueGuaranteeFee;
    private Long loanTime;
    private Long payOffTime;
}
