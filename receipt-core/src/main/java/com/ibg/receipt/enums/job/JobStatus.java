package com.ibg.receipt.enums.job;

public enum JobStatus {
                           FAIL((byte)-1, "失败"),
                           INIT((byte)0, "新建"),
                           DEALING((byte)1, "处理中"),
                           SUCCESS((byte)2, "成功"),
                           NOTICE_MANUAL((byte)3, "待人工干预");
    private byte status;
    private String desc;

    JobStatus(byte status, String desc) {
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

    public static JobStatus getEnum(int val) {
        for (JobStatus status : JobStatus.values()) {
            if (status.getStatus() == (byte)val) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据status 获取message
     * @param val
     * @return
     */
    public static String getMessage(int val) {
        for (JobStatus status : JobStatus.values()) {
            if (status.getStatus() == (byte)val) {
                return status.getDesc();
            }
        }
        return null;
    }
}
