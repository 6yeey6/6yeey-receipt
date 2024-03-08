package com.ibg.receipt.enums.application;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

/**
 * 进件银行卡用途
 * 
 * @author taixin
 */
@Getter
public enum ApplBankUsage {
    LOAN_GATHER((byte) 1, "收款"), REPAY_PAY((byte) 2, "还款");

    private Byte code;
    private String desc;

    private ApplBankUsage(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<ApplBankUsage> parse(Byte code) {
        return Stream.of(ApplBankUsage.values()).filter(e -> e.code.equals(code)).findFirst();
    }
}
