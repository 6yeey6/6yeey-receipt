package com.ibg.receipt.base.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceChannelCode {

    TB("天邦担保"),
    YR("亿融"),
    ZRX_FG("中融信"),
    HY_FG ("昊悦担保");
    private final String name;


}
