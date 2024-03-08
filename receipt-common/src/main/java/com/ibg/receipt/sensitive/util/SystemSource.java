package com.ibg.receipt.sensitive.util;

import lombok.Getter;

/**
 * Description: 调用系统来源
 *
 * @author zhangxiusen
 * @date 2020-7-21
 */
@Getter
public enum SystemSource {

    PROXY("代理系统"),

    UCREDIT("友信普惠"),

    PORTAL("公共服务平台"),

    KTJR("开通金融"),

    ERMAS("贷后"),

    CMS("客服"),

    OTHERS("其他"),
    ;
    private String desc;

    SystemSource(String desc) {
        this.desc = desc;
    }

    public static SystemSource getEnum(String name){
        for (SystemSource systemSource : SystemSource.values()){
            if (systemSource.name().equals(name)){
                return systemSource;
            }
        }
        return null;
    }
}
