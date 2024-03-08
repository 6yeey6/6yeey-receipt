package com.ibg.receipt.model.receiptChild;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.ReceiptChannel;
import com.ibg.receipt.enums.business.ReceiptItemCodeAmount;
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
@Table(name = "receipt_child_order_amount")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptChildOrderAmount extends BaseModel {
    /**
     * 工单key
     */
    @Column(name = "receipt_order_key",nullable = false)
    private String receiptOrderKey;
    /**
     * 子单key
     */
    @Column(name = "receipt_child_order_key",nullable = false)
    private String receiptChildOrderKey;

    /**
     * 主体
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "creditor")
    private CreditorEnum creditor;

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
     * 开票渠道
     * nuonuo 诺诺网
     * manual 人工处理
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_channel")
    private ReceiptChannel receiptChannel;
    /**
     * 用户uid
     */
    @Column(name = "uid",nullable = false)
    private String uid;

    /**
     * 开票项编码
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_item_code",nullable = false)
    private ReceiptItemCodeAmount receiptItemCode;

    /**
     * 开票金额
     */
    @Column(name = "receipt_amount",nullable = false)
    private BigDecimal receiptAmount;

    /**
     * 状态
     * 0 待开票
     * 1 开票中（自动开启诺诺流程、人工点击导出后更新）
     * 2 开票完成（诺诺渠道回传数据、人工上传文件后更新）
     * 3 无需处理
     */
    @Column(name = "status", nullable = false)
    private Byte status;

    /**
     * 审核标识 false/null 不需要审核
     *         true 审核
     */
    @Column(name = "need_audit")
    private Boolean needAudit;


    /**
     * 子单完成时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_time")
    private Date requestTime;

    /**
     * 子单完成时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finish_time")
    private Date finishTime;

    /**
     * 子邮件发送状态
     */
    @Column(name = "send_status",nullable = false)
    private Byte sendStatus;


    /**
     * 子邮件发送时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "send_time")
    private Date sendTime;
    /**
     * 处理人姓名
     *
     */
    @Column(name = "operator_name")
    private String operatorName;

    /**
     * 发票影像地址
     *
     */
    @Column(name = "receipt_url")
    private String receiptUrl;

    /**
     * 发票影像文件id
     *
     */
    @Column(name = "receipt_file_id")
    private String receiptFileId;

    /**
     * 发票影像地址
     *
     */
    @Column(name = "creditor_config_version")
    private String creditorConfigVersion;


    @Column(name = "ext_info")
    private String extInfo;

    /**
     * 优先级
     *
     */
    @Column(name = "priority_level")
    private Integer priorityLevel;

    @Column(name = "item_ext_info")
    private String itemExtInfo;
}