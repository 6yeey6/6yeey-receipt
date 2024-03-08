package com.ibg.receipt.base.enums;

/**
 */
public enum BusiType {

    LOAN("放款"),
    REPAY("还款");

    BusiType(String desc){
        this.desc = desc;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
