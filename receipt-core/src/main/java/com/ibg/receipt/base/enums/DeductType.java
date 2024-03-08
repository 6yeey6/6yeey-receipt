package com.ibg.receipt.base.enums;

/**
 * @desc:   资金方还款划扣类型
 * @author: lvzhonglin
 * @date: 2021/1/4 19:52
 */
public enum DeductType {

    FUNDER("资金方"),
    PAY_CHANNEL("支付渠道");

    DeductType(String desc){
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
