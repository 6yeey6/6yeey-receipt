package com.ibg.receipt.job.constants;

/**
 * 处理状态
 */
public enum ProcessStatus {

    WAIT_PROCESS {
        @Override
        public String toString() {
            return "等待处理";
        }
    },
    PROCESSING {
        @Override
        public String toString() {
            return "处理中";
        }
    },
    PROCESS_SUCCESS {
        @Override
        public String toString() {
            return "处理成功";
        }
    },
    PROCESS_FAIL {
        @Override
        public String toString() {
            return "处理失败";
        }
    },
    INVALID {
        @Override
        public String toString() {
            return "失效";
        }
    };

    @Override
    public abstract String toString();

}
