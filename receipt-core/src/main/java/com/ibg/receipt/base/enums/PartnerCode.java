package com.ibg.receipt.base.enums;

import com.ibg.receipt.base.constant.SourceCode;

public enum PartnerCode {

    HAOHUAN("好分期", SourceCode.INNER_PAY), UCREDIT("友信普惠", SourceCode.PARTNER_UCREDIT), RRD("人人贷借款", SourceCode.INNER_PAY),
    XIAOWEI("小微贷", SourceCode.INNER_PAY);

    private String desc;
    private SourceCode sourceCode;

    private PartnerCode(String desc, SourceCode sourceCode) {
        this.desc = desc;
        this.setSourceCode(sourceCode);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public SourceCode getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

}
