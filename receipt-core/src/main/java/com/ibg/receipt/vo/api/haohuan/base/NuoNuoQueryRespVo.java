package com.ibg.receipt.vo.api.haohuan.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuoNuoQueryRespVo {

    private String code;

    private List<ResultData> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultData {
        private String pdfUrl;
        private String ofdUrl;
        private String status;
        private String failCause;
        private String orderNo;
        /**
         * 主体名称
         */
        private String saleName;
        /**
         * 税号
         */
        private String salerTaxNum;
    }

}