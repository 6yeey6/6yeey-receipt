package com.ibg.receipt.job.constants;

/**
 * 任务状态
 */
public enum TaskStatus {

    RUNNING() {
        @Override
        public String toString() {
            return "运行";
        }
    },
    STOPPING() {
        @Override
        public String toString() {
            return "停止";
        }
    };

    @Override
    public abstract String toString();

}
