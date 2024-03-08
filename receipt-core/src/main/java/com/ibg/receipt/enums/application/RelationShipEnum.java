package com.ibg.receipt.enums.application;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum RelationShipEnum {

	NONE(0, "NONE", "未知"),
    CUSTOMER(1, "CUSTOMER", "本人"),
    COMPANY(2, "COMPANY", "单位"),
    SPOUSE(3, "SPOUSE", "配偶"),
    CHILDREN(4, "CHILDREN", "子女"),
    PARENTS(5, "PARENTS", "父母"),
    SIBLINGS(6, "SIBLINGS", "兄弟姐妹"),
    RELATIVES(7, "RELATIVES", "其他亲属"),
    FRIENDS(8, "FRIENDS", "朋友"),
    COLLEAGUE(9, "COLLEAGUE", "同事");
	
	private Integer code;
    private String name;
    private String desc;

    private RelationShipEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static Optional<RelationShipEnum> parse(String name) {
        return Stream.of(RelationShipEnum.values()).filter(e -> e.name().equals(name)).findFirst();
    }
}
