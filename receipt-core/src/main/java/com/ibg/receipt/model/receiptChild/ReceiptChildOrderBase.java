package com.ibg.receipt.model.receiptChild;
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
@Table(name = "receipt_child_order_base")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptChildOrderBase extends BaseModel {
    /**
     * 工单key
     */
    @Column(name = "receipt_order_key",nullable = false)
    private String receiptOrderKey;
    /**
     * 用户uid
     */
    @Column(name = "uid",nullable = false)
    private String uid;
    /**
     * 借款单号
     */
    @Column(name = "loan_id")
    private String loanId;

    @Column(name = "partner_user_id")
    private String partnerUserId;

    @Column(name = "invoice_key")
    private String invoiceKey;
    /**
     * 主体code
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "creditor")
    private CreditorEnum creditor;

    @Column(name = "account")
    private String account;

    @Column(name = "user_name")
    private String userName;

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

    @Column(name = "repay_status")
    private String repayStatus;

    /**
     * 长银项目，loanId取此字段
     */
    @Column(name = "funder_loan_key_cy_old")
    private String funderLoanKeyCyOld;


    @Column(name = "dt")
    private String dt;

    @Column(name = "creditor_config_version")
    private String creditorConfigVersion;

    @Column(name = "need_repay_detail")
    private String needRepayDetail;

    @Column(name = "receipt_amount")
    private BigDecimal receiptAmount;

    @Column(name = "loan_contract_path")
    private String loanContractPath;

    @Column(name = "insure_letter_path")
    private String insureLetterPath;

    @Column(name = "guarantee_service_contract_path")
    private String guaranteeServiceContractPath;

    @Column(name = "id_card_front_path")
    private String idCardFrontPath;

    @Column(name = "id_card_back_path")
    private String idCardBackPath;

    @Column(name = "gather_auth_letter")
    private String gatherAuthLetter;
}