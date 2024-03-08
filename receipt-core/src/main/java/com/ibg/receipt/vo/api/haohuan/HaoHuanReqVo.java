package com.ibg.receipt.vo.api.haohuan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HaoHuanReqVo {
    /**
     * 是否需要提供明细 1-是, 0-否(不填默认 0)
     */
    private String loanIds;
}
