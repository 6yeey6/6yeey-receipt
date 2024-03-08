package com.ibg.receipt.base.enums;

/**
 * @desc: 资金方类型
 */
public enum TradeType {

    MIDDLE("中间商"), 
    FUNDER("资金方");

    TradeType(String desc){
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
