package com.ibg.receipt.base.enums;

public enum ReceiptStatus {

     INIT((byte) 0, "待开票"), DEALING((byte) 1, "开票中"), SUCCESS((byte) 2, "开票完成"), NODEAL((byte) 3, "无需处理");

    private byte status;
    private String desc;

    ReceiptStatus(byte status, String desc) {
        this.status = status;
        this.desc = desc;
    }
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

    public static ReceiptStatus getEnum(int value) {
        for (ReceiptStatus status : ReceiptStatus.values()) {
            if (status.getStatus() == (byte)value) {
                return status;
            }
        }
        return null;
    }
    public static ReceiptStatus getEnumByDesc(String desc) {
        for (ReceiptStatus status : ReceiptStatus.values()) {
            if (status.getDesc().equals(desc)) {
                return status;
            }
        }
        return null;
    }
}
