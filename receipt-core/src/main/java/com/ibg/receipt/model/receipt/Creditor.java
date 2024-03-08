package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.OrganizationEnum;
import com.ibg.receipt.enums.business.ReceiptChannel;
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
 * @date 2022/8/23 14:57
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "creditor")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Creditor extends BaseModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "creditor")
    private CreditorEnum creditor;

    @Column(name = "creditor_name")
    private String creditorName;

    @Column(name = "creditor_config_version")
    private String creditorConfigVersion;

    @Column(name = "operator_name")
    private String operatorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_channel")
    private ReceiptChannel receiptChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization")
    private OrganizationEnum organization;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "need_audit")
    private Boolean needAudit;
}
