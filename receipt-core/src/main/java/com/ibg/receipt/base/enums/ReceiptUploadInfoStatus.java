package com.ibg.receipt.base.enums;

public enum ReceiptUploadInfoStatus {

     FAIL((byte) 0, "匹配失败"), SUCC((byte) 1, "匹配成功");

    private byte status;
    private String desc;

    ReceiptUploadInfoStatus(byte status, String desc) {
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

    public static ReceiptUploadInfoStatus getEnum(int value) {
        for (ReceiptUploadInfoStatus status : ReceiptUploadInfoStatus.values()) {
            if (status.getStatus() == (byte)value) {
                return status;
            }
        }
        return null;
    }
}
