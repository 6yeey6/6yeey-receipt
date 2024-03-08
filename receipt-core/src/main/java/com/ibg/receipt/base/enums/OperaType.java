package com.ibg.receipt.base.enums;

public enum OperaType {

    ADD("增加"), SUB("减少");

    private String desc;

    private OperaType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
