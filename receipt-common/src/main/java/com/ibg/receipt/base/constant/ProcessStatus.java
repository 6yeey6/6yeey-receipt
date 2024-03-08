package com.ibg.receipt.base.constant;

import java.util.EnumSet;

public enum ProcessStatus {

    FAIL((byte) -1, "失败"), INIT((byte) 0, "新建"), DEALING((byte) 1, "处理中"), SUCCESS((byte) 2, "成功"), NONE((byte) 3,
            "无需退保");

    private byte status;
    private String desc;

    ProcessStatus(byte status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    public static EnumSet<ProcessStatus> PROCESSING = EnumSet.of(INIT, DEALING);

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ProcessStatus getEnum(int value) {
        for (ProcessStatus status : ProcessStatus.values()) {
            if (status.getStatus() == (byte)value) {
                return status;
            }
        }
        return null;
    }
}
