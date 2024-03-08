package com.ibg.receipt.job.encrypt;

import com.ibg.receipt.util.StringUtils;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EncryptBankBean {
    private Long id;
    private String value;
    String tableName;
    String fieldName;

    public boolean isValid(){
        return id != null && StringUtils.isNotBlank(value) && StringUtils.isNotBlank(tableName) && StringUtils.isNotBlank(fieldName);
    }
}
