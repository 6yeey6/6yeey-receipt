package com.ibg.receipt.job.constants;

/**
 * 参数类型
 */
public enum ParamType {

    TIMING_PARAM {
        @Override
        public String toString() {
            return "定时任务参数";
        }
    },
    LOOP_DELAY_PARAM {
        @Override
        public String toString() {
            return "循环任务延迟参数";
        }
    },
    LOOP_INTERVAL_PARAM {
        @Override
        public String toString() {
            return "循环任务间隔参数";
        }
    },
    CRON_PARAM {
        @Override
        public String toString() {
            return "CRON任务参数";
        }
    };

    @Override
    public abstract String toString();

}
