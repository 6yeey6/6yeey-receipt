package com.ibg.receipt.job.constants;

/**
 * 任务类型
 */
public enum TaskType {

    TIMING_TASK {
        @Override
        public String toString() {
            return "定时任务";
        }
    },
    LOOP_TASK {
        @Override
        public String toString() {
            return "循环任务";
        }
    },
    CRON_TASK {
        @Override
        public String toString() {
            return "CRON任务";
        }
    };

    @Override
    public abstract String toString();

}
