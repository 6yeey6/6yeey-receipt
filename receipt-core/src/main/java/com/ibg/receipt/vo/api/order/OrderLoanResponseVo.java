package com.ibg.receipt.vo.api.order;

import lombok.Data;
import java.util.List;

/**
 * 贷款单查询
 * @author zhangjilong
 */

@Data
public class OrderLoanResponseVo {
    private List<OrderLoanVo> orderLoanList;
}
