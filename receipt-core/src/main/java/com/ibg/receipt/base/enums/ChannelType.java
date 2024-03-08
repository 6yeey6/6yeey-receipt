package com.ibg.receipt.base.enums;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum ChannelType {

    FUNDER("资金方"), GUARANTOR("担保方"), SERVICE("服务方");

    private String desc;

    private ChannelType(String desc) {
        this.desc = desc;
    }

    public static Optional<ChannelType> parse(String type) {
        return Stream.of(ChannelType.values()).filter(e -> e.name().equals(type)).findFirst();
    }
}
