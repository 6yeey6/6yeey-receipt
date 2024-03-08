package com.ibg.receipt.job.vo.receipt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectVo {
    /**
     * 批次
     */
    private String item;

    private Object value;

}
