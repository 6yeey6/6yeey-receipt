package com.ibg.receipt.base.enums;

public enum AmountType {

    AVAILABLE("可用额度"), FROZEN("冻结额度"), USED("已使用额度");

    private String desc;

    private AmountType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
