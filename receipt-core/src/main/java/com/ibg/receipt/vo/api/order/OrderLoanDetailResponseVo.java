package com.ibg.receipt.vo.api.order;

import java.util.List;

import lombok.Data;

/**
 * 贷款单查询
 * @author zhangjilong
 */

@Data
public class OrderLoanDetailResponseVo {

    private String userName;

    private String sumLoanAmount;

    private List<OrderLoanDetailVo> list;
}
