package com.ibg.receipt.enums.application;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum AuditStatusEnum {

    WAITING("待审核"), PROCESSING("处理中"), APPROVE("通过"), REJECT("拒绝"), FIX_AND_RETRY("等待修复并重试"), FUNDER_ERR_REJECT("审核方异常并拒绝");

    private String desc;

    private AuditStatusEnum(String desc) {
        this.desc = desc;
    }

    public static Optional<AuditStatusEnum> parse(String status) {
        return Stream.of(AuditStatusEnum.values()).filter(e -> e.name().equals(status)).findFirst();
    }

    // 允许进件修改的审核状态
    public static EnumSet<AuditStatusEnum> CAN_UPDATE_STATUS = EnumSet.of(WAITING);
    // 允许重新进件的审核状态
    public static EnumSet<AuditStatusEnum> CAN_RESUBM_STATUS = EnumSet.of(WAITING);

    // 审核不通过
    public static EnumSet<AuditStatusEnum> AUDIT_UNSUCCESS = EnumSet.of(REJECT, FIX_AND_RETRY, FUNDER_ERR_REJECT);

}
