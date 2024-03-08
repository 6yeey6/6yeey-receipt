package com.ibg.receipt.base.constant;

/**
 * api传入的调用方编号
 * 
 * @author taixin
 */
public enum SourceCode {

    INNER_ACCOUNT() {
        @Override
        public String toString() {
            return "账务系统";
        }
    },
    INNER_PROXY() {
        @Override
        public String toString() {
            return "账务代理系统";
        }
    },
    INNER_PAY() {
        @Override
        public String toString() {
            return "支付系统";
        }
    },
    PARTNER_HAOHUAN() {
        @Override
        public String toString() {
            return "好还";
        }
    },
    PARTNER_UCREDIT() {
        @Override
        public String toString() {
            return "友信";
        }
    },
    PARTNER_VERIFY() {
        @Override
        public String toString() {
            return "好还审核";
        }
    },
    CHANNEL_YNTRUST() {
        @Override
        public String toString() {
            return "云南信托";
        }
    },
    PARTNER_RRD() {
        @Override
        public String toString() {
            return "人人贷借款";
        }
    },
    PARTNER_RRD_BH() {
        @Override
        public String toString() {
            return "人人贷借款-渤海信托";
        }
    },
    PARTNER_RRD_WC() {
        @Override
        public String toString() {
            return "人人贷借款-微财";
        }
    }
    ;

    public abstract String toString();
}
