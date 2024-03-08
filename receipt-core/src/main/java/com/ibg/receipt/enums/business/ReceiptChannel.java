package com.ibg.receipt.enums.business;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/30 14:11
 */
public enum ReceiptChannel {

    NUONUO("诺诺网"),

    MANUAL("人工干预"),

    NOSYSTEM("系统不支持"),

    ;
    private final String desc;

    public String getDesc() {
        return this.desc;
    }

    private ReceiptChannel(String desc) {
        this.desc = desc;
    }
}
