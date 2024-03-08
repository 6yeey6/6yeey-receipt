package com.ibg.receipt.vo.api.haohuan;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HaoHuanRespVo {

    private String code;

    private ResultData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultData {
        private List<ContractInfo> contractInfo;


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ContractInfo {
            private String partnerLoanNo;
            private List<ContractAddress> contractAddress;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class ContractAddress {
                private String title;
                private String type;
                private String url;
            }

        }
    }


}