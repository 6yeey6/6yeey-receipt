package com.ibg.receipt.enums.application;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum ApplicationAuditStatus {

    WAITING("待审核"), PROCESSING("处理中"), APPROVE("通过"), FUNDER_REJECT("审核方拒绝"), GURANTOR_REJECT("担保方拒绝"), FIX_AND_RETRY("等待修复并重试"),FUNDER_ERR_REJECT("审核方异常并拒绝");

    private String desc;

    private ApplicationAuditStatus(String desc) {
        this.desc = desc;
    }

    public static Optional<ApplicationAuditStatus> parse(String status) {
        return Stream.of(ApplicationAuditStatus.values()).filter(e -> e.name().equals(status)).findFirst();
    }

    // 失败的审核状态
    public static EnumSet<ApplicationAuditStatus> REJECT_STATUS = EnumSet.of(FUNDER_REJECT, GURANTOR_REJECT);

    // 允许进件修改的审核状态
    public static EnumSet<ApplicationAuditStatus> CAN_UPDATE_STATUS = EnumSet.of(WAITING, APPROVE, FIX_AND_RETRY);

    public static EnumSet<ApplicationAuditStatus> CAN_AUDIT_STATUS = EnumSet.of(WAITING, FIX_AND_RETRY);

    // 进件未被拒绝的状态
    public static EnumSet<ApplicationAuditStatus> NOT_REJECT_STATUS = EnumSet.of(WAITING, APPROVE, FIX_AND_RETRY,PROCESSING);

}
