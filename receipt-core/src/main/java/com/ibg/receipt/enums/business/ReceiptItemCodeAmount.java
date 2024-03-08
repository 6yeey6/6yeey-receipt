package com.ibg.receipt.enums.business;

import java.util.EnumSet;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/23 15:36
 */
public enum ReceiptItemCodeAmount {

    INTEREST("repayInterest", "利息"),
    MGMT_FEE("repayMgmtFee", "服务费"),
    OVERDUE_INTEREST("repayOverdueInterest", "罚息"),
    OVERDUE_MGMT_FEE("repayOverdueMgmtFee", "逾期管理费"),
    IN_REPAY_FEE("repayInRepayFee", "提前还款违约金"),
    FUNDER_OVERDUE_INTEREST("repayFunderOverdueInterest", "资金方罚息"),
    GUARANTEE_FEE("repayGuaranteeFee", "担保费"),
    GUARANTEE_DEPOSIT("repayGuaranteeDeposit", "保障金"),
    OVERDUE_GUARANTEE_FEE("repayOverdueGuaranteeFee", "逾期担保费"),
    COMMUTATION("repayCommutation", "代偿金"),
    INTEREST_FEE ("interestFee","息费"),
    TOTAL_SERVICE_FEE("totalServiceFee","总服务费"),
    TOTAL_GUARANTOR_FEE("totalGuarantorFee","总担保费");

    private String code;
    private String desc;

    public String getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }

    private ReceiptItemCodeAmount(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReceiptItemCodeAmount getEnum(String name) {
        for (ReceiptItemCodeAmount codes : ReceiptItemCodeAmount.values()) {
            if (codes.name().equals(name)) {
                return codes;
            }
        }
        return null;
    }
    public static ReceiptItemCodeAmount getEnumByDesc(String desc) {
        for (ReceiptItemCodeAmount codes : ReceiptItemCodeAmount.values()) {
            if (codes.getDesc().equals(desc)) {
                return codes;
            }
        }
        return null;
    }

    /**
     * 通过code获取
     * @param codeName
     * @return
     */
    public static ReceiptItemCodeAmount getReceiptItemCodeAmountByCode(String codeName) {
        for (ReceiptItemCodeAmount codes : ReceiptItemCodeAmount.values()) {
            if (codes.getCode().equals(codeName)) {
                return codes;
            }
        }
        return null;
    }
}
