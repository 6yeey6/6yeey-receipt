package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import lombok.*;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "receipt_repay_detail")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptRepayDetail extends BaseModel {
    /**
     * loan_id
     */
    @Column(name = "loan_id", nullable = false)
    private String loanId;
    /**
     * 用户uid
     */
    @Column(name = "period", nullable = false)
    private Integer period;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payoff_time", nullable = false)
    private Date payoffTime;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "principal")
    private BigDecimal principal;

    @Column(name = "interest")
    private BigDecimal interest;

    @Column(name = "mgmt_fee")
    private BigDecimal mgmtFee;

    @Column(name = "fund_overdue_interest")
    private BigDecimal fundOverdueInterest;

    @Column(name = "overdue_interest")
    private BigDecimal overdueInterest;

    @Column(name = "overdue_mgmt_fee")
    private BigDecimal overdueMgmtFee;

    @Column(name = "guarantee_fee")
    private BigDecimal guaranteeFee;

    @Column(name = "in_repay_fee")
    private BigDecimal inRepayFee;

    @Column(name = "commutation")
    private BigDecimal commutation;

    @Column(name = "grace_period_interest")
    private BigDecimal gracePeriodInterest;

    @Column(name = "repay_overdue_guarantee_fee")
    private BigDecimal repayOverdueGuaranteeFee;
}