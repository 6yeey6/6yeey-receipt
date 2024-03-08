package com.ibg.receipt.enums.job;

public enum JobType {

    COMMON_JOB("COMMON", "普通任务"), TIME_JOB("TIME", "定时任务");

    private String type;
    private String desc;

    private JobType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static JobType getEnum(String type) {
        for (JobType jobType : JobType.values()) {
            if (jobType.getType().equals(type)) {
                return jobType;
            }
        }
        return null;
    }
}
