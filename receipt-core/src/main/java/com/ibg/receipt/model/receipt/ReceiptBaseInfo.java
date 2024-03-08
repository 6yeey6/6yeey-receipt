package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.enums.business.CreditorEnum;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "receipt_base_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptBaseInfo extends BaseModel {

    @Column(name = "uid")
    private String uid;

    @Column(name = "account")
    private String account;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "partner_user_id")
    private String partnerUserId;

    @Column(name = "invoice_key")
    private String invoiceKey;

    @Column(name = "user_pid")
    private String userPid;

    @Column(name = "pid_valid")
    private String pidValid;

    @Column(name = "address")
    private String address;

    @Column(name = "bank_card")
    private String bankCard;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "funder_loan_key")
    private String funderLoanKey;

    @Column(name = "trust_name")
    private String trustName;

    @Column(name = "fund_status")
    private String fundStatus;

    @Column(name = "loan_id")
    private String loanId;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "fund_name")
    private String fundName;

    @Column(name = "loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "period")
    private Integer period;

    @Column(name = "in_repay_period")
    private Integer inRepayPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "loan_time")
    private Date loanTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payoff_time")
    private Date payoffTime;

    @Column(name = "his_overdueday")
    private Integer hisOverdueday;

    @Column(name = "repay_amount")
    private BigDecimal repayAmount;

    @Column(name = "repay_interest")
    private BigDecimal repayInterest;

    @Column(name = "repay_mgmt_fee")
    private BigDecimal repayMgmtFee;

    @Column(name = "repay_overdue_interest")
    private BigDecimal repayOverdueInterest;

    @Column(name = "repay_overdue_mgmt_fee")
    private BigDecimal repayOverdueMgmtFee;

    @Column(name = "repay_funder_overdue_interest")
    private BigDecimal repayFunderOverdueInterest;

    @Column(name = "repay_guarantee_deposit")
    private BigDecimal repayGuaranteeDeposit;

    @Column(name = "repay_guarantee_fee")
    private BigDecimal repayGuaranteeFee;

    @Column(name = "repay_overdue_guarantee_fee")
    private BigDecimal repayOverdueGuaranteeFee;


    @Column(name = "repay_commutation")
    private BigDecimal repayCommutation;

    @Column(name = "repay_in_repay_fee")
    private BigDecimal repayInRepayFee;

    @Column(name = "repay_status")
    private String repayStatus;
    /**
     * 主体
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "creditor")
    private CreditorEnum creditor;

    /**
     * 担保方
     */
    @Column(name = "guarantor")
    private String guarantor;

    /**
     * 长银项目，loanId取此字段
     */
    @Column(name = "funder_loan_key_cy_old")
    private String funderLoanKeyCyOld;

    /**
     */
    @Column(name = "ext_fee1")
    private String extFee1;

    /**
     */
    @Column(name = "ext_fee2")
    private String extFee2;

    /**
     */
    @Column(name = "ext_fee3")
    private String extFee3;

    /**
     */
    @Column(name = "ext_fee4")
    private String extFee4;

    /**
     */
    @Column(name = "ext_fee5")
    private String extFee5;

    @Column(name = "dt")
    private String dt;

    @Column(name = "status")
    private byte status;
}
