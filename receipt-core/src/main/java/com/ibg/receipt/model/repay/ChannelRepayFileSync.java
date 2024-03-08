package com.ibg.receipt.model.repay;

import com.ibg.receipt.base.enums.FunderChannelCode;
import com.ibg.receipt.base.model.BaseModel;
import lombok.*;

import javax.persistence.*;

/**
 * 还款结果同步资金方表
 * @author lvzhonglin <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "channel_repay_file_sync")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRepayFileSync extends BaseModel {
    /**
     * 上传批次号
     */
    @Column(name = "batch_no")
    private String batchNo;
    /**
     * 资金平台还款订单号
     */
    @Column(name = "repay_order_key")
    private String repayOrderKey;

    /**
     * 文件同步日期
     */
    @Column(name = "sync_date")
    private String syncDate;

    /**
     * 文件同步结果 ProcessStatus
     */
    @Column(name = "sync_result")
    private String syncResult;

    /** 资金方渠道编号 */
    @Enumerated(EnumType.STRING)
    @Column(name = "funder_code", nullable = false)
    private FunderChannelCode funderCode;

}
