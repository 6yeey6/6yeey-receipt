package com.ibg.receipt.enums.business;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/25 16:16
 */
public enum ItemType {

    FILE("文件"),

    STRING("信息字串"),

    ;
    private final String desc;

    public String getDesc() {
        return this.desc;
    }

    private ItemType(String desc) {
        this.desc = desc;
    }
}
