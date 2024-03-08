package com.ibg.receipt.vo.api.order;

import com.ibg.receipt.base.exception.Assert;

import lombok.Data;

import java.util.List;

/**
 * 贷款单明细查询
 * @author zhangjilong
 */
@Data
public class OrderLoanDetailRequestVo {

    private List<String> loanNos;

    private String partnerUserId;

    private String source;

    public void checkParams() {
        Assert.notEmpty(loanNos, "进件号列表");
    }

}
