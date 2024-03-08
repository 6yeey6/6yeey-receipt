package com.ibg.receipt.vo.api.fee;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lining
 * @date 19-5-9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ExtFeeVo {

    private String feeExtType;

    private BigDecimal amount;
}
