package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.ReceiptItemCodeAmount;
import com.ibg.receipt.enums.business.ReceiptItemCodeBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/23 15:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "creditor_amount_config")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditorAmountConfig extends BaseModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "creditor")
    private CreditorEnum creditor;

    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_item_code")
    private ReceiptItemCodeAmount receiptItemCode;

    @Column(name = "creditor_config_version")
    private String creditorConfigVersion;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "deleted")
    private Boolean deleted;
}
