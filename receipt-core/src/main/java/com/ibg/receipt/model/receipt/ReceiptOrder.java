package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.enums.business.UserSource;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "receipt_order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptOrder extends BaseModel {
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
     * 用户姓名
     */
    @Column(name = "user_name",nullable = false)
    private String userName;

    /**
     * 用户uid
     */
    //@Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Byte status;

    /**
     * 完成进度
     */
    @Column(name = "scale")
    private String scale;

    /**
     * 用户邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 用户联系电话
     */
    @Column(name = "mobile")
    private String mobile;


    /**
     * 发票工单请求时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_time", nullable = false)
    private Date requestTime;

    /**
     * 整体完成时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finish_time")
    private Date finishTime;

    /**
     * 创建人姓名
     */
    @Column(name = "creator_name")
    private String creatorName;


    /**
     * 处理人姓名
     *
     */
    @Column(name = "operator_name")
    private String operatorName;

    /**
     * 优先级
     *
     */
    @Column(name = "priority_level")
    private Integer priorityLevel;

    /**
     * 系统来源
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private UserSource source;

}