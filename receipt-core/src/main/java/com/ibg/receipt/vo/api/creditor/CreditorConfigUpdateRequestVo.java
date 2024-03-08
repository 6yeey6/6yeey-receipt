package com.ibg.receipt.vo.api.creditor;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.OrganizationEnum;
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
public class CreditorConfigUpdateRequestVo {

    private List<String> items;

    private String creditor;

    private CreditorEnum creditorEnum;

    private List<ReceiptItemCodeBase> receiptItemCodeBases;

    private List<String> amounts;

    private List<ReceiptItemCodeAmount> receiptItemCodeAmounts;

    // 资金运营
    private List<String> capitalOperation;
    //所属机构
    private String belongOrg;

    private OrganizationEnum belongOrgEnum;

    private String isReceipt;

    public void check() {
        Assert.notBlank(creditor, "主体");
        Assert.notEmpty(items, "开票信息");
        Assert.notEmpty(amounts, "资金项");
        Assert.notEmpty(capitalOperation, "资金运营");
        Assert.notBlank(belongOrg, "所属机构");

        creditorEnum = Assert.enumNotValid(CreditorEnum.class, creditor, "主体");
        belongOrgEnum = Assert.enumNotValid(OrganizationEnum.class, belongOrg, "所属机构");
        receiptItemCodeBases = items.stream().map(item -> Assert.enumNotValid(ReceiptItemCodeBase.class, item,
                "开票信息")).collect(Collectors.toList());
        receiptItemCodeAmounts = amounts.stream().map(amount -> Assert.enumNotValid(ReceiptItemCodeAmount.class, amount,
                "资金项")).collect(Collectors.toList());
    }
}
