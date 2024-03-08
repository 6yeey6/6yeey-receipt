package com.ibg.receipt.enums.application;

/**
 * 预授信结果
 *
 */
public enum PreCreditStatus {
    SUCCESS("预授信通过"), FAIL("预授信拒绝");

    private String desc;

    private PreCreditStatus(String desc) {
        this.setDesc(desc);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
