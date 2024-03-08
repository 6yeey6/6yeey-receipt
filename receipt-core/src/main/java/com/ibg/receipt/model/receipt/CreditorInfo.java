package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.enums.business.CreditorEnum;
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

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "creditor_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditorInfo extends BaseModel {

    /**
     * 主体
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "creditor")
    private CreditorEnum creditor;

    /**
     * 主体名称
     */
    @Column(name = "creditor_name")
    private String creditorName;

    /**
     * appKey
     */
    @Column(name = "app_key")
    private String appKey;

    /**
     * appSecret
     */
    @Column(name = "app_secret")
    private String appSecret;

    /**
     * 纳税号
     */
    @Column(name = "tax_no")
    private String taxNo;

    /**
     * 地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 电话
     */
    @Column(name = "mobile")
    private String mobile;

    /**
     * 开票员
     */
    @Column(name = "clerk")
    private String clerk;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 银行名称
     */
    @Column(name = "bank_name")
    private String bankName;

    /**
     * 银行卡号
     */
    @Column(name = "bank_card_no")
    private String bankCardNo;

    /**
     * 删除状态
     */
    @Column(name = "deleted")
    private Boolean deleted;
}
