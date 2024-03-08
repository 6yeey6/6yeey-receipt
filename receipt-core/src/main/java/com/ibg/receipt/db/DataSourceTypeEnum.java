package com.ibg.receipt.db;

public enum DataSourceTypeEnum {

    MASTER() {
        @Override
        public String toString() {
            return "MASTER";
        }
    },
    SLAVE() {
        @Override
        public String toString() {
            return "SLAVE";
        }
    };

    @Override
    public abstract String toString();
}
