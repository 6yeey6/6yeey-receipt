package com.ibg.receipt.vo.api.manage;

import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author liuye07
 */
@Data
public class QueryBusinessContractReqVO {

    /**
     * 借款单号
     */
    private String loanId;
}
