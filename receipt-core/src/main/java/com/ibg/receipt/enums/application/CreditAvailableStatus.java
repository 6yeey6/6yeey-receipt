package com.ibg.receipt.enums.application;

/**
 * @desc: 用户可用额度状态
 * @author: lvzhonglin
 * @date: 2021/11/2 11:13
 */
public enum CreditAvailableStatus {
    AVAILABLE("A", "本地无额度"),
    EXPIRE("F", "额度过期"),
    NORMAL("S", "额度正常"),
    FROZEN("N", "额度冻结");

    private String status;
    private String desc;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    CreditAvailableStatus(String status, String des){
        this.status = status;
        this.desc = des;
    }
}
