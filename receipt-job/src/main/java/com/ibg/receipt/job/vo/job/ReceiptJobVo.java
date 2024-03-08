package com.ibg.receipt.job.vo.job;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptJobVo {
    /**
     * 批次
     */
    private String batchNo;
    /**
     * job操作时间
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date jobDate;

    private List<String> loanIdList;

}
