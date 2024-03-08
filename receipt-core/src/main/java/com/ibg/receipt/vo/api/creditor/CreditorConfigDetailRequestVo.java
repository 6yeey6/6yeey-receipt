package com.ibg.receipt.vo.api.creditor;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.ReceiptItemCodeAmount;
import com.ibg.receipt.enums.business.ReceiptItemCodeBase;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/24 14:01
 */
@Data
public class CreditorConfigDetailRequestVo {

    private String creditor;

    private CreditorEnum creditorEnum;

    public void check() {
        Assert.notBlank(creditor, "主体");
        creditorEnum = Assert.enumNotValid(CreditorEnum.class, creditor, "主体");
    }
}
