package com.ibg.receipt.model.receiptUploadInfo;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.enums.business.ReceiptItemCodeAmount;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "receipt_upload_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptUploadInfo extends BaseModel {
    /**
     * 上传批次号
     */
    @Column(name = "upload_batch_no",nullable = false)
    private String uploadBatchNo;
    /**
     * 借款单号
     */
    @Column(name = "loan_id")
    private String loanId;
    /**
     * 开票项编码
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_item_code",nullable = false)
    private ReceiptItemCodeAmount receiptItemCode;
    /**
     * 子单key
     */
    @Column(name = "receipt_child_order_key",nullable = false)
    private String receiptChildOrderKey;

    /**
     * 状态
     * 1:匹配成功；0:匹配失败
     */
    @Column(name = "status", nullable = false)
    private Byte status;
    /**
     * 上传文件名
     *
     */
    @Column(name = "file_name")
    private String fileName;
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
     * 上传发票用户id
     *
     */
    @Column(name = "receipt_user_id")
    private String receiptUserId;
    /**
     * 上传发票用户id
     *
     */
    @Column(name = "upload_time")
    private Date uploadTime;
}