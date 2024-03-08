package com.ibg.receipt.base.enums;

import java.util.EnumSet;

public enum PartnerSubType {

    HAOJIE("好借"), HAOHUAN("好还"), HAOMAI("好买"), XIAOWEI("小微贷");

    private String desc;

    PartnerSubType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    /** 受托支付 */
    public static EnumSet<PartnerSubType> ENTRUST_SET = EnumSet.of(HAOMAI);
}
