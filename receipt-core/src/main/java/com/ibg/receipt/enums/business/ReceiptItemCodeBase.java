package com.ibg.receipt.enums.business;

/**
 * @author wanghongbo01
 * @date 2022/8/23 15:46
 */
public enum ReceiptItemCodeBase {

    //CUSTOMER_NAME("客户姓名"),
    //ID_CARD_NO("身份证"),
    //IPHONE("电话号码"),
    //ADDRESS("居住地址"),
    //FUNDER_LOAN_NO("资金方借款编号"),
    //LOAN_CREDITOR("放款主体"),
    //PERIODS("期限"),
    //LOAN_DATE("放款时间"),
    //PAY_OFF_TIME("结清时间"),
    //LOAN_AMOUNT("借款金额"),
    //RECEIPT_SIDE("开票方"),
    //REAL_PAY_DETAIL("实还明细"),
    //TRUST_PLAN("信托计划"),
    //
    //LOAN_CONTRACT("借款合同"),
    //GUARANTEE_CONTRACT("担保合同"),
    //GUARANTEE_ERVICES_CONTRACT("担保咨询服务合同"),
    //ID_CARD_FRONT_BACK("身份证正反面"),


    CUSTOMER_NAME("userName","姓名", ItemType.STRING),
    ID_CARD_NO("userPid","身份证号", ItemType.STRING),
    IPHONE("account", "电话号码", ItemType.STRING),
    ADDRESS("address","居住地址", ItemType.STRING),
    FUNDER_LOAN_NO("funderLoanKey","资方进件编号", ItemType.STRING),
    LOAN_ID("loanId","业务借款号", ItemType.STRING),
    LOAN_CREDITOR("fundName","放款主体", ItemType.STRING),
    PERIODS("period","期限", ItemType.STRING),
    LOAN_DATE("loanTime","放款时间", ItemType.STRING),
    PAY_OFF_TIME("payoffTime","结清时间", ItemType.STRING),
    LOAN_AMOUNT("loanAmount","借款金额", ItemType.STRING),
    RECEIPT_SIDE("creditor","开票方", ItemType.STRING),
    RECEIPT_AMOUNT("receiptAmount","开票金额", ItemType.STRING),
    TRUST_NAME("trustName","信托计划", ItemType.STRING),
    REPAY_DETAIL("needRepayDetail","实还明细", ItemType.STRING),
    FUNDER_LOAN_KEY_CY_OLD("funderLoanKeyCyOld","老长银资方编码",ItemType.STRING),
    REPAY_STATUS("repayStatus","还款状态",ItemType.STRING),
    BANK_CARD("bankCard","银行卡号",ItemType.STRING),
    BANK_NAME("bankName","开户行",ItemType.STRING),
    //UID("uid","用户uid", ItemType.STRING),
    //CUSTOMER_NAME("userName","姓名", ItemType.STRING),
    //ID_CARD_NO("userPid","身份证号", ItemType.STRING),
    //IPHONE("account", "电话号码", ItemType.STRING),
    //ADDRESS("address","居住地址", ItemType.STRING),
    //LOAN_KEY("funderLoanKey","进件编号", ItemType.STRING),
    //FUNDER_LOAN_KEY("funderLoanKey","资方借款编号", ItemType.STRING),
    //LOAN_CREDITOR("","放款主体", ItemType.STRING),
    //PERIOD("period","期限", ItemType.STRING),
    //LOAN_TIME("loanTime","放款时间", ItemType.STRING),
    //PAYOFF_TIME("payoffTime","结清时间", ItemType.STRING),
    //LOAN_AMOUNT("loanAmount","借款金额", ItemType.STRING),
    //RECEIPT_SIDE("creditor","开票方", ItemType.STRING),
    //RECEIPT_AMOUT("","开票金额", ItemType.STRING),
    //REPAY_DETAIL("repayDetail","实还明细", ItemType.STRING),
    //TRUST_NAME("trustName","信托计划", ItemType.STRING),
//    PID_VALID("pidValid","身份证有效期", ItemType.STRING),
//    BANK_CARD("bankCard","放款银行卡号", ItemType.STRING),
//    BANK_NAME("bankName","银行名称", ItemType.STRING),
//    FUND_STATUS("fundStatus","资金端还款状态", ItemType.STRING),
//    LOAN_ID("loanId","业务借款号", ItemType.STRING),
//    FUND_CODE("fundCode","资金方", ItemType.STRING),
//    FUND_NAME("fundName","资方名称", ItemType.STRING),
//    IN_REPAY_PERIOD("inRepayPeriod","提前结清开始期次", ItemType.STRING),
//    HIS_OVERDUEDAY("hisOverdueday","历史逾期期数", ItemType.STRING),
//    REPAY_AMOUNT("repayAmount","历史逾期期数", ItemType.STRING),
//    REPAY_STATUS("repayStatus","还款状态", ItemType.STRING),
//    FUNDER_LOAN_KEY_CY_OLD("funderLoanKeyCyOld","逾期担保费", ItemType.STRING),
    /**
     * 合同地址
     */
    LOAN_CONTRACT("loanContractPath","借款合同", ItemType.FILE),
    GUARANTEE_CONTRACT("insureLetterPath","担保服务合同", ItemType.FILE),
    GUARANTEE_ERVICES_CONTRACT("guaranteeServiceContractPath","担保咨询服务合同", ItemType.FILE),
    GATHER_AUTH_LETTER("gatherAuthLetter","代扣授权书",ItemType.FILE),
    //ID_CARD_FRONT("idCardFrontPath","身份证正面", ItemType.FILE),
    //ID_CARD_BACK("idCardBackPath","身份证反面", ItemType.FILE),

    ;
    private String code;
    private String desc;
    private ItemType itemType;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    private ReceiptItemCodeBase(String code, String desc, ItemType itemType) {
        this.code = code;
        this.desc = desc;
        this.itemType = itemType;
    }

    public static ReceiptItemCodeBase getEnum(String name) {
        for (ReceiptItemCodeBase codes : ReceiptItemCodeBase.values()) {
            if (codes.name().equals(name)) {
                return codes;
            }
        }
        return null;
    }

    public static String getEnumByCode(String code) {
        for (ReceiptItemCodeBase codes : ReceiptItemCodeBase.values()) {
            if (codes.getCode().equals(code)) {
                return codes.getDesc();
            }
        }
        return null;
    }
}
