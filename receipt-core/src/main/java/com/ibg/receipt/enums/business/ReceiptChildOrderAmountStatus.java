package com.ibg.receipt.enums.business;

import java.util.EnumSet;

/**
 *
 */
public enum ReceiptChildOrderAmountStatus {

    INIT((byte) 0, "待开票"),
    DEALING((byte) 1, "开票中"),
    FINISH((byte) 2, "开票完成"),
    NODEAL((byte) 3, "无需处理"),
    ;

    private byte status;
    private String desc;

    public byte getStatus(){
        return status;
    }

    public String getDesc(){
        return desc;
    }

    private ReceiptChildOrderAmountStatus(byte status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ReceiptChildOrderAmountStatus getEnum(byte status) {
        for (ReceiptChildOrderAmountStatus statusEnum : ReceiptChildOrderAmountStatus.values()) {
            if (statusEnum.getStatus() == status) {
                return statusEnum;
            }
        }
        return null;
    }

    public static ReceiptChildOrderAmountStatus getByDec(String desc) {
        for (ReceiptChildOrderAmountStatus statusEnum : ReceiptChildOrderAmountStatus.values()) {
            if (statusEnum.getDesc().equals(desc)) {
                return statusEnum;
            }
        }
        return null;
    }

    /** 完成状态 */
    public static final EnumSet<ReceiptChildOrderAmountStatus> FINISHED_STATUS_SET = EnumSet.of(FINISH, NODEAL);
}
