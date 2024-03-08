package com.ibg.receipt.enums.business;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/26 11:40
 */
public enum UserSource {

    MANAGEMENT_PLATFORM("管理平台"),

    CUSTOMER_SYSTEM("客服系统"),

    BUS_SYSTEM("业务系统"),

    ;
    private final String desc;

    public String getDesc() {
        return this.desc;
    }

    private UserSource(String desc) {
        this.desc = desc;
    }
}
