package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "receipt_order_loan")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptOrderLoan extends BaseModel {
    /**
     * 工单key
     */
    @Column(name = "receipt_order_key")
    private String receiptOrderKey;
    /**
     * 借款单号
     */
    @Column(name = "loan_id")
    private String loanId;

}
