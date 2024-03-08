package com.ibg.receipt.vo.api.haohuan.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuoNuoApplyRespVo {

    private String code;

    private ResultData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultData {
        private String invoiceSerialNum;
    }

}