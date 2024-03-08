package com.ibg.receipt.vo.api.fee;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class FeeVo {

    /** 总金额 */
    private BigDecimal amount;

    /** 本金 */
    private BigDecimal principal;

    /** 利息 */
    private BigDecimal interest;

    /** 服务费 */
    private BigDecimal serviceFee;

    /** 逾期罚息 */
    private BigDecimal overdueInterest;

    /** 逾期管理费 */
    private BigDecimal overdueMgmtFee;

    /** 其他费用 */
    private BigDecimal otherFee;

    /** 提前还款违约金 */
    private BigDecimal inRepayFee;

    private List<ExtFeeVo> extFeeVos;
}
