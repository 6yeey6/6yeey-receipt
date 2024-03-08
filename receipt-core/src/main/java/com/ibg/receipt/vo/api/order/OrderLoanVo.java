package com.ibg.receipt.vo.api.order;

import com.ibg.receipt.base.exception.Assert;
import lombok.Builder;
import lombok.Data;

/**
 * 贷款单
 * @author zhangjilong
 */
@Data
@Builder
public class OrderLoanVo {

    /**
     * 进件号
     */
    private String loanNo;

    /**
     * 用户姓名
     */
    private String customerName;

    /**
     * 用户id
     */
    private String partneruserId;

    /**
     * 资金方
     */
    private String funderName;

    /**
     * 借款总额
     */
    private String loanAmount;

    /**
     * 借款状态
     */
    private String loanStatus;

    /**
     * 放款时间
     */
    private Long loanDate;

    /**
     * 结清时间
     */
    private Long payoffDate;

    /**
     * 创建时间
     */
    private Long creteDate;

    /**
     * 完成时间
     */
    private Long finishedDate;

    /**
     * 创建状态
     */
    private Boolean isLocal;

    /**
     * 未完成
     */
    private Integer unfinishedCount;

    /**
     * 已完成
     */
    private Integer finishedCount;

    /**
     * 总个数
     */
    private Integer totalCount;

    /**
     * 借款总期次
     */
    private Integer periods;


}
