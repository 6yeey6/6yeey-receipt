package com.ibg.receipt.vo.api.receiptChild;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.ReceiptItemCodeAmount;
import com.ibg.receipt.vo.api.base.BasePageVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 子单列表查询请求vo
 *
 * @author zhou
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptChildListRequestVo extends BasePageVo {

    private String uid;
    private String receiptOrderKey;
    private String receiptChildOrderKey;
    private String status;
    private List<String> creditors;
    private String operatorName;
    private String receiptUrl;
    private Integer priorityLevel;
    private String sendStatus;
    private String landUserName;
    private String partnerUserId;
    private List<CreditorEnum> creditorEnums;
    private String organization;
    private List<Byte> statusList;
    private String loanId;
    private List<String> receiptItemCodes;
    private List<ReceiptItemCodeAmount> receiptItemCodeList;

    public void checkParams() {
        Assert.notNull(landUserName, "当前登录人");
        if (creditors != null && creditors.size() > 0) {
            creditorEnums = creditors.stream().map(x -> Assert.enumNotValid(CreditorEnum.class, x,
                    "主体")).collect(Collectors.toList());
        }
        //资金项枚举转换
        if (receiptItemCodes != null && receiptItemCodes.size() > 0) {
            receiptItemCodeList = receiptItemCodes.stream().map(x -> Assert.enumNotValid(ReceiptItemCodeAmount.class, x,
                    "资金项")).collect(Collectors.toList());
        }
    }
}
