package com.ibg.receipt.enums.application;

/**
 * 审核方式
 * 
 * @author taixin
 */
public enum AuditMode {
    SYSTEM_AUTO("系统自动"), REQUEST_TRIGGER("主动请求");

    private String desc;

    private AuditMode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
