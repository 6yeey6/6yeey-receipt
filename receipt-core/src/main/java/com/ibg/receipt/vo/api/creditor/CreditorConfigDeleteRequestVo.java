package com.ibg.receipt.vo.api.creditor;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.ReceiptItemCodeBase;
import lombok.Data;

import java.util.stream.Collectors;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/24 14:50
 */
@Data
public class CreditorConfigDeleteRequestVo {

    private String creditor;

    private CreditorEnum creditorEnum;

    private String creditorConfigVersion;

    public void check() {
        Assert.notBlank(creditor, "主体");
        Assert.notBlank(creditorConfigVersion, "配置版本号");

        creditorEnum = Assert.enumNotValid(CreditorEnum.class, creditor, "主体");
    }
}
