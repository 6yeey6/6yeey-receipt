package com.ibg.receipt.job.encrypt;

import lombok.Data;

import java.util.List;


@Data
public class EncryptQueryResult {
    //id
    private Long id;
    //值
    private List<EncryptValueData> value;




}

    @Data
    class EncryptValueData{
        private String valueFiledName;
        private String value;
    }
