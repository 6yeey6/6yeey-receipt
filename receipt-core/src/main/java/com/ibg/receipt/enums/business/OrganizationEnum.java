package com.ibg.receipt.enums.business;

/**
 *
 * 所属机构
 */
public enum OrganizationEnum {

    WEICAI("我司"),

    FUNDER("外部资方/担保");

    private final String desc;

    public String getDesc() {
        return this.desc;
    }

    private OrganizationEnum(String desc) {
        this.desc = desc;
    }
}
