package com.ibg.receipt.job.encrypt;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EncryptQueryBean {
    String tableName;
    Class instance;


}
